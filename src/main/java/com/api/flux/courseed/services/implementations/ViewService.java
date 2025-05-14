package com.api.flux.courseed.services.implementations;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.api.flux.courseed.persistence.documents.Category;
import com.api.flux.courseed.persistence.documents.Institution;
import com.api.flux.courseed.persistence.documents.View;
import com.api.flux.courseed.persistence.repositories.CategoryRepository;
import com.api.flux.courseed.persistence.repositories.CourseRepository;
import com.api.flux.courseed.persistence.repositories.InstitutionRepository;
import com.api.flux.courseed.persistence.repositories.UserRepository;
import com.api.flux.courseed.persistence.repositories.ViewRepository;
import com.api.flux.courseed.projections.dtos.CourseDto;
import com.api.flux.courseed.projections.dtos.CourseViewsStatsDto;
import com.api.flux.courseed.projections.dtos.SaveViewDto;
import com.api.flux.courseed.projections.dtos.TotalViewsDto;
import com.api.flux.courseed.projections.dtos.UserDto;
import com.api.flux.courseed.projections.dtos.ViewDto;
import com.api.flux.courseed.projections.mappers.CategoryMapper;
import com.api.flux.courseed.projections.mappers.CourseMapper;
import com.api.flux.courseed.projections.mappers.InstitutionMapper;
import com.api.flux.courseed.projections.mappers.ViewMapper;
import com.api.flux.courseed.services.interfaces.InterfaceViewService;
import com.api.flux.courseed.web.exceptions.CustomWebExchangeBindException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ViewService implements InterfaceViewService {

    @Autowired
    private ViewRepository viewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private ViewMapper viewMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Override
    public Mono<TotalViewsDto> getTotalViews() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        return viewRepository.count()
            .flatMap(total -> viewRepository.countByCreatedAtBetween(startOfMonth, endOfMonth)
                .map(lastMonth -> new TotalViewsDto(total, lastMonth))
            );
    }

    @Override
    public Mono<Page<ViewDto>> findViewsByCourseId(String courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return viewRepository.findByCourseId(courseId, pageable)
            .flatMap(view -> userRepository.findById(view.getUserId())
                .map(user -> {
                    ViewDto viewDto = viewMapper.toViewDto(view);
                    viewDto.setUser(new UserDto(user.getId(), user.getEmail()));
                    return viewDto;
                })
            )
            .collectList()
            .zipWith(viewRepository.count())
            .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    @Override
    public Mono<Object> createView(Principal principal, SaveViewDto saveViewDto) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> courseRepository.findById(saveViewDto.getCourseId())
                .flatMap(course -> viewRepository.findByCourseIdAndUserId(course.getId(), user.getId())
                    .flatMap(view -> Mono.error(
                        new CustomWebExchangeBindException(
                            saveViewDto.getCourseId(),
                            "courseId",
                            "¡Genial que reacciones al programa! Ten en cuenta que solo se permite una reacción por programa."
                        ).getWebExchangeBindException()
                    ))
                    .switchIfEmpty(Mono.defer(() -> {
                        View view = viewMapper.toView(saveViewDto);
                        view.setUserId(user.getId());
                        return viewRepository.save(view)
                            .flatMap(createdView -> {
                                ViewDto viewDto = viewMapper.toViewDto(createdView);
                                viewDto.setCourse(courseMapper.toCourseDto(course));
                                viewDto.setUser(new UserDto(user.getId(), user.getEmail()));

                                return Mono.just(viewDto);
                            });
                    }))
                )
                .switchIfEmpty(Mono.error(
                    new CustomWebExchangeBindException(
                        saveViewDto.getCourseId(),
                        "courseId",
                        "No hemos podido encontrar el curso indicado. Te sugerimos que verifiques la información y lo intentes de nuevo."
                    ).getWebExchangeBindException())
                )
            )
            .switchIfEmpty(Mono.error(
                new CustomWebExchangeBindException(
                    principal.getName(),
                    "auth",
                    "Parece que el usuario autenticado no se encuentra en el sistema. Te recomendamos cerrar sesión y volver a ingresar."
                ).getWebExchangeBindException()
            ));

    }

    @Override
    public Mono<Page<ViewDto>> findViewsByAuthUser(Principal principal, int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
    
    return userRepository.findByEmail(principal.getName())
        .flatMap(user -> {
            // Si search está vacío, obtenemos todas las vistas del usuario
            if (search == null || search.trim().isEmpty()) {
                return viewRepository.findByUserId(user.getId(), pageable)
                    .flatMap(this::enrichViewWithCourseData)
                    .collectList()
                    .zipWith(viewRepository.countByUserId(user.getId()))
                    .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
            } else {
                String searchPattern = search.toLowerCase();
                return courseRepository.findByTitleContainingIgnoreCase(searchPattern)
                    .flatMap(course -> viewRepository.findByCourseIdAndUserId(course.getId(), user.getId())
                        .flatMap(this::enrichViewWithCourseData))
                    .collectList()
                    .map(filteredViews -> {
                        // Paginamos manualmente la lista filtrada
                        int start = (int) pageable.getOffset();
                        int end = Math.min((start + pageable.getPageSize()), filteredViews.size());
                        
                        List<ViewDto> pageContent = start >= filteredViews.size() ? 
                            List.of() : filteredViews.subList(start, end);
                            
                        return new PageImpl<>(pageContent, pageable, filteredViews.size());
                    });
            }
        });
    }

    private Mono<ViewDto> enrichViewWithCourseData(View view) {
        return courseRepository.findById(view.getCourseId())
            .flatMap(course -> {
                Mono<Category> categoryMono = categoryRepository.findById(course.getCategoryId());
                Mono<Institution> institutionMono = institutionRepository.findById(course.getInstitutionId());
    
                return Mono.zip(categoryMono, institutionMono)
                    .map(tuple -> {
                        CourseDto courseDto = courseMapper.toCourseDto(course);
                        courseDto.setCategory(categoryMapper.toCategoryDto(tuple.getT1()));
                        courseDto.setInstitution(institutionMapper.toInstitutionDto(tuple.getT2()));
    
                        ViewDto viewDto = viewMapper.toViewDto(view);
                        viewDto.setCourse(courseDto);
                        viewDto.setUser(new UserDto(view.getUserId(), null)); // El email se establecerá más tarde
                        return viewDto;
                    });
            });
    }

    @Override
    public Mono<List<CourseViewsStatsDto>> findCoursesWithDecreasingViews() {
        LocalDateTime startOfLastMonth = LocalDateTime.now()
            .withDayOfMonth(1)
            .minusMonths(1)
            .withHour(0).withMinute(0).withSecond(0).withNano(0);

        long lastMonthEpoch = startOfLastMonth.toEpochSecond(ZoneOffset.UTC);

        return viewRepository.findCoursesWithDecreasingViews(lastMonthEpoch)
            .collectList();
    }

    @Override
    public Mono<Integer> getTotalViewsBySuscriptor(Principal principal) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> courseRepository.findByUserId(user.getId())
                .collectList()
                .flatMapMany(Flux::fromIterable)
                .flatMap(course -> viewRepository.findByCourseId(course.getId()))
                .count()
                .map(Long::intValue)
            );
    }
}
