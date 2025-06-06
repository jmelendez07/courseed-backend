package com.api.flux.courseed.services.implementations;

import java.security.Principal;
import java.text.DecimalFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;

import com.api.flux.courseed.persistence.documents.Category;
import com.api.flux.courseed.persistence.documents.Content;
import com.api.flux.courseed.persistence.documents.Course;
import com.api.flux.courseed.persistence.documents.Institution;
import com.api.flux.courseed.persistence.documents.Reaction;
import com.api.flux.courseed.persistence.documents.Review;
import com.api.flux.courseed.persistence.documents.UserCourseRecomended;
import com.api.flux.courseed.persistence.repositories.CategoryRepository;
import com.api.flux.courseed.persistence.repositories.ContentRepository;
import com.api.flux.courseed.persistence.repositories.CourseRepository;
import com.api.flux.courseed.persistence.repositories.InstitutionRepository;
import com.api.flux.courseed.persistence.repositories.ProfileRepository;
import com.api.flux.courseed.persistence.repositories.ReactionRepository;
import com.api.flux.courseed.persistence.repositories.ReviewRepository;
import com.api.flux.courseed.persistence.repositories.UserCourseRecomendedRepository;
import com.api.flux.courseed.persistence.repositories.UserInterestRepository;
import com.api.flux.courseed.persistence.repositories.UserRepository;
import com.api.flux.courseed.persistence.repositories.ViewRepository;
import com.api.flux.courseed.projections.dtos.CourseDto;
import com.api.flux.courseed.projections.dtos.FormPredictionDto;
import com.api.flux.courseed.projections.dtos.MostCommonReactionDto;
import com.api.flux.courseed.projections.dtos.PredictionDataDto;
import com.api.flux.courseed.projections.dtos.RecomendeCourseDto;
import com.api.flux.courseed.projections.dtos.UserDto;
import com.api.flux.courseed.projections.dtos.ViewDto;
import com.api.flux.courseed.projections.mappers.CategoryMapper;
import com.api.flux.courseed.projections.mappers.ContentMapper;
import com.api.flux.courseed.projections.mappers.CourseMapper;
import com.api.flux.courseed.projections.mappers.InstitutionMapper;
import com.api.flux.courseed.projections.mappers.ProfileMapper;
import com.api.flux.courseed.projections.mappers.ReactionMapper;
import com.api.flux.courseed.projections.mappers.ReviewMapper;
import com.api.flux.courseed.projections.mappers.UserMapper;
import com.api.flux.courseed.projections.mappers.ViewMapper;
import com.api.flux.courseed.services.interfaces.InterfacePredictionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instance;

@Service
public class PredictionService implements InterfacePredictionService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCourseRecomendedRepository userCourseRecomendedRepository;

    @Autowired
    private UserInterestRepository userInterestRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private ViewRepository viewRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ViewMapper viewMapper;

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private ReactionMapper reactionMapper;

    private Instances dataStructure;
    private Classifier classifier;

    public PredictionService() {
        try {
            // Cargar el modelo desde el classpath
            ClassPathResource modelResource = new ClassPathResource("j48modelCourseed.model");
            classifier = (Classifier) weka.core.SerializationHelper.read(modelResource.getInputStream());

            // Cargar el archivo ARFF desde el classpath
            ClassPathResource arffResource = new ClassPathResource("CourseedUsers.user_course_dataset.arff");
            DataSource source = new DataSource(arffResource.getInputStream());
            dataStructure = source.getDataSet();
            dataStructure.setClassIndex(dataStructure.numAttributes() - 1);
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar los recursos necesarios para PredictionService", e);
        }   
    }

    public Mono<UserCourseRecomended> getUserCourseRecomended(String userId, String courseId) {
        return profileRepository.findByUserId(userId)
            .flatMap(profile -> courseRepository.findById(courseId)
                .flatMap(course -> categoryRepository.findById(course.getCategoryId())
                    .flatMap(category -> institutionRepository.findById(course.getInstitutionId())
                        .flatMap(institution -> userCourseRecomendedRepository.findByCourseIdAndUserProfileId(courseId, profile.getId())
                            .flatMap(userCourseRecomended -> userInterestRepository.findByUserProfileId(profile.getId())
                                .flatMap(userInterest -> categoryRepository.findById(userInterest.getCategoryId())
                                    .flatMap(interest -> {
                                        Instance instance = new DenseInstance(16);
                                        instance.setDataset(dataStructure);
                                        instance.setValue(0, profile.getId());
                                        instance.setValue(1, interest.getName());
                                        instance.setValue(2, profile.getAvailableHoursTime());
                                        instance.setValue(3, profile.getBudget());
                                        instance.setValue(4, profile.getPlatformPrefered());
                                        instance.setValue(5, courseId);
                                        instance.setValue(6, institution.getName());
                                        instance.setValue(7, course.getModality());
                                        instance.setValue(8, userCourseRecomended.getCourseHours());
                                        instance.setValue(9, course.getPrice() == null ? 0 : course.getPrice());
                                        instance.setValue(10, category.getName());
                                        instance.setValue(11, userCourseRecomended.getRatingAvg());
                                        instance.setValue(12, userCourseRecomended.getMaxReaction());
                                        instance.setValue(13, userCourseRecomended.getTotalViews());
                                        instance.setValue(14, userCourseRecomended.getReviewsCount());
        
                                        double predictionValue;
                                        try {
                                            predictionValue = classifier.classifyInstance(instance);
                                            System.out.println("Prediction Value: " + predictionValue);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            return Mono.error(new RuntimeException("Error during classification", e));
                                        }
                                        String prediction = dataStructure.classAttribute().value((int) predictionValue);
                                        double[] probabilities;
                                        String confidencePercentage = null;
                                        try {
                                            probabilities = classifier.distributionForInstance(instance);
                                            double confidence = probabilities[(int) predictionValue];
                                            DecimalFormat df = new DecimalFormat("#.#");
                                            confidencePercentage = df.format(confidence * 100) + "%";
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            return Mono.error(new RuntimeException("Error during prediction", e));
                                        }
        
                                        userCourseRecomended.setRecomended(prediction.equals("true"));
                                        userCourseRecomended.setConfidence(confidencePercentage);
        
                                        return Mono.just(userCourseRecomended);
                                    })
                                )
                            )
                    ))
                )
            );
    }

    public Mono<RecomendeCourseDto> predictCourseRecommendation(FormPredictionDto formData) {
        try {
            // Crear la instancia para la predicción
            Instance instance = new DenseInstance(16);
            instance.setDataset(dataStructure);
            
            // Asignar los valores desde el DTO a la instancia
            instance.setValue(0, formData.getUser_profileId());
            instance.setValue(1, formData.getUser_interest());
            instance.setValue(2, formData.getUser_availableTime());
            instance.setValue(3, formData.getBudget());
            instance.setValue(4, formData.getPlatform_preference());
            instance.setValue(5, formData.getCourse_id());
            instance.setValue(6, formData.getCourse_institution());
            instance.setValue(7, formData.getCourse_modality());
            instance.setValue(8, formData.getCourse_duration());
            instance.setValue(9, formData.getCourse_price());
            instance.setValue(10, formData.getCourse_category());
            instance.setValue(11, formData.getCourse_rating_avg());
            instance.setValue(12, formData.getCourse_max_reaction());
            instance.setValue(13, formData.getCourse_visits());
            instance.setValue(14, formData.getCourse_reviews_count());

            double predictionValue = classifier.classifyInstance(instance);
            String prediction = dataStructure.classAttribute().value((int) predictionValue);            

            double[] probabilities = classifier.distributionForInstance(instance);
            double confidence = probabilities[(int) predictionValue];
            DecimalFormat df = new DecimalFormat("#.#");
            String confidencePercentage = df.format(confidence * 100) + "%";

            RecomendeCourseDto result = new RecomendeCourseDto();
            result.setId(formData.getCourse_id());
            result.setTitle("Curso específico");
            result.setCategory(formData.getCourse_category());
            result.setInstitution(formData.getCourse_institution());
            result.setPrice(formData.getCourse_price() != null ? formData.getCourse_price().toString() : "0");
            result.setRecommended("true".equals(prediction));
            result.setConfidence(confidencePercentage);
            
            return Mono.just(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.error(new RuntimeException("Error al realizar la predicción", e));
        }
    }

    public Mono<List<RecomendeCourseDto>> getRecomendedCoursesByUser(String userId) {
    return profileRepository.findByUserId(userId)
        .flatMap(profile -> courseRepository.findAll()
            .flatMap(course -> userCourseRecomendedRepository.findByCourseIdAndUserProfileId(course.getId(), profile.getId())
                .flatMap(userCourseRecomended -> userInterestRepository.findByUserProfileId(profile.getId())
                    .flatMap(userInterest -> categoryRepository.findById(userInterest.getCategoryId())
                        .flatMap(insterest -> {

                            return categoryRepository.findById(course.getCategoryId())
                                .flatMap(category -> institutionRepository.findById(course.getInstitutionId())
                                    .flatMap(institution -> {
                                        Instance instance = new DenseInstance(16);
                                        instance.setDataset(dataStructure);
                                        instance.setValue(0, profile.getId());
                                        instance.setValue(1, insterest.getName());
                                        instance.setValue(2, profile.getAvailableHoursTime());
                                        instance.setValue(3, profile.getBudget());
                                        instance.setValue(4, profile.getPlatformPrefered());
                                        instance.setValue(5, course.getId());
                                        instance.setValue(6, institution.getName());
                                        instance.setValue(7, course.getModality() == null ? "Semipresencial" : course.getModality());
                                        instance.setValue(8, userCourseRecomended.getCourseHours());
                                        instance.setValue(9, course.getPrice() == null ? 0 : course.getPrice());
                                        instance.setValue(10, category.getName());
                                        instance.setValue(11, userCourseRecomended.getRatingAvg());
                                        instance.setValue(12, userCourseRecomended.getMaxReaction());
                                        instance.setValue(13, userCourseRecomended.getTotalViews());
                                        instance.setValue(14, userCourseRecomended.getReviewsCount());
        
                                        try {
                                            double predictionValue = classifier.classifyInstance(instance);
                                            String prediction = dataStructure.classAttribute().value((int) predictionValue);
                                            
                                            if (!"true".equals(prediction)) {
                                                return Mono.empty();
                                            }
                                            
                                            double[] probabilities = classifier.distributionForInstance(instance);
                                            double confidence = probabilities[(int) predictionValue];
                                            DecimalFormat df = new DecimalFormat("#.#");
                                            String confidencePercentage = df.format(confidence * 100) + "%";
        
                                            userCourseRecomended.setRecomended(true);
                                            userCourseRecomended.setConfidence(confidencePercentage);
        
                                            RecomendeCourseDto recomendedCourse = new RecomendeCourseDto();
                                            recomendedCourse.setId(course.getId());
                                            recomendedCourse.setTitle(course.getTitle());
                                            recomendedCourse.setCategory(category.getName());
                                            recomendedCourse.setInstitution(institution.getName());
                                            recomendedCourse.setPrice(course.getPrice() != null ? course.getPrice().toString() : "0");
                                            recomendedCourse.setRecommended(true);
                                            recomendedCourse.setConfidence(confidencePercentage);
                                            
                                            return Mono.just(recomendedCourse);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            return Mono.empty();
                                        }
                                    })
                                );
                        })
                    )
                )
            )
            .collectList()
        );
    }

    public Mono<Integer> getTotalCoursesRecomended(Principal principal) {
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> profileRepository.findByUserId(user.getId())
                .flatMap(profile -> courseRepository.findAll()
                    .flatMap(course -> categoryRepository.findById(course.getCategoryId())
                        .flatMap(category -> {
                            Mono<Double> ratingAvgMono = reviewRepository.getAverageRatingByCourseId(course.getId())
                                .defaultIfEmpty(0.0);
                            
                            Mono<String> maxReactionMono = reactionRepository.findMostCommonReactionByCourseId(course.getId())
                                .map(MostCommonReactionDto::getType)
                                .defaultIfEmpty("NONE");
                            
                            Mono<Long> viewsCountMono = viewRepository.countByCourseId(course.getId())
                                .defaultIfEmpty(0L);
                            
                            Mono<Long> reviewsCountMono = reviewRepository.countByCourseId(course.getId())
                                .defaultIfEmpty(0L);
                            
                            return Mono.zip(ratingAvgMono, maxReactionMono, viewsCountMono, reviewsCountMono)
                                .flatMap(tuple -> {
                                    Double ratingAvg = tuple.getT1();
                                    String maxReaction = tuple.getT2();
                                    Long viewsCount = tuple.getT3();
                                    Long reviewsCount = tuple.getT4();
                                    
                                    try {
                                        Instance instance = new DenseInstance(13);
                                        instance.setDataset(dataStructure);
                                        instance.setValue(0, profile.getInterest());
                                        instance.setValue(1, profile.getAvailableHoursTime());
                                        instance.setValue(2, profile.getBudget());
                                        instance.setValue(3, courseService.standarizeModality(profile.getPlatformPrefered()));
                                        instance.setValue(4, courseService.standarizeModality(course.getModality()));
                                        instance.setValue(5, courseService.standarizeDuration(course.getDuration()));
                                        instance.setValue(6, course.getPrice() == null ? 0 : course.getPrice());
                                        instance.setValue(7, categoryService.standarizeCategory(category.getName()));
                                        instance.setValue(8, ratingAvg);
                                        instance.setValue(9, maxReaction);
                                        instance.setValue(10, viewsCount.intValue());
                                        instance.setValue(11, reviewsCount.intValue());

                                        double predictionValue = classifier.classifyInstance(instance);
                                        String prediction = dataStructure.classAttribute().value((int) predictionValue);

                                        return Mono.just(prediction.equals("true") ? 1 : 0);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        return Mono.just(0);
                                    }
                                });
                        })
                        .defaultIfEmpty(0)
                    )
                    .reduce(0, Integer::sum)
                )
            
            )
            .defaultIfEmpty(0);
    }

    public Mono<Page<CourseDto>> getRecomendedCoursesByAuth(Principal principal, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> profileRepository.findByUserId(user.getId())
                .flatMap(profile -> courseRepository.findAll()
                    .flatMap(course -> categoryRepository.findById(course.getCategoryId())
                        .flatMap(category -> institutionRepository.findById(course.getInstitutionId())
                            .flatMap(institution -> {

                                Mono<Double> ratingAvgMono = reviewRepository.getAverageRatingByCourseId(course.getId())
                                    .defaultIfEmpty(0.0);
                                
                                Mono<String> maxReactionMono = reactionRepository.findMostCommonReactionByCourseId(course.getId())
                                    .map(MostCommonReactionDto::getType)
                                    .defaultIfEmpty("NONE");
                                
                                Mono<Long> viewsCountMono = viewRepository.countByCourseId(course.getId())
                                    .defaultIfEmpty(0L);
                                
                                Mono<Long> reviewsCountMono = reviewRepository.countByCourseId(course.getId())
                                    .defaultIfEmpty(0L);

                                Flux<Review> reviewFlux = reviewRepository.findByCourseId(course.getId());
                                
                                return Mono.zip(ratingAvgMono, maxReactionMono, viewsCountMono, reviewsCountMono, reviewFlux.collectList())
                                    .flatMap(tuple -> {
                                        Double ratingAvg = tuple.getT1();
                                        String maxReaction = tuple.getT2();
                                        Long viewsCount = tuple.getT3();
                                        Long reviewsCount = tuple.getT4();
                                        
                                        try {
                                            Instance instance = new DenseInstance(13);
                                            instance.setDataset(dataStructure);
                                            instance.setValue(0, profile.getInterest());
                                            instance.setValue(1, profile.getAvailableHoursTime());
                                            instance.setValue(2, profile.getBudget());
                                            instance.setValue(3, courseService.standarizeModality(profile.getPlatformPrefered()));
                                            instance.setValue(4, courseService.standarizeModality(course.getModality()));
                                            instance.setValue(5, courseService.standarizeDuration(course.getDuration()));
                                            instance.setValue(6, course.getPrice() == null ? 0 : course.getPrice());
                                            instance.setValue(7, categoryService.standarizeCategory(category.getName()));
                                            instance.setValue(8, ratingAvg);
                                            instance.setValue(9, maxReaction);
                                            instance.setValue(10, viewsCount.intValue());
                                            instance.setValue(11, reviewsCount.intValue());

                                            double predictionValue = classifier.classifyInstance(instance);
                                            String prediction = dataStructure.classAttribute().value((int) predictionValue);

                                            double[] probabilities = classifier.distributionForInstance(instance);
                                            double confidence = probabilities[(int) predictionValue];
                                            DecimalFormat df = new DecimalFormat("#.#");
                                            String confidencePercentage = df.format(confidence * 100) + "%";

                                            if (prediction.equals("true")) {
                                                PredictionDataDto predictionDto = new PredictionDataDto();
                                                predictionDto.setUserInterest(instance.stringValue(0));
                                                predictionDto.setUserAvailableTime(instance.value(1));
                                                predictionDto.setBudget((int) instance.value(2));
                                                predictionDto.setPlatformPreference(instance.stringValue(3));
                                                predictionDto.setCourseModality(instance.stringValue(4));
                                                predictionDto.setCourseDuration((int) instance.value(5));
                                                predictionDto.setCoursePrice(instance.value(6));
                                                predictionDto.setCourseCategory(instance.stringValue(7));
                                                predictionDto.setCourseRatingAvg(instance.value(8));
                                                predictionDto.setCourseMaxReaction(instance.stringValue(9));
                                                predictionDto.setCourseVisits((int) instance.value(10));
                                                predictionDto.setCourseReviewsCount((int) instance.value(11));
                                                predictionDto.setCourseRecomended(prediction.equals("true"));
                                                predictionDto.setConfidence(confidencePercentage);

                                                CourseDto courseDto = courseMapper.toCourseDto(course);
                                                courseDto.setCategory(categoryMapper.toCategoryDto(category));
                                                courseDto.setInstitution(institutionMapper.toInstitutionDto(institution));
                                                courseDto.setReviews(tuple.getT5().stream()
                                                    .map(reviewMapper::toReviewDto)
                                                    .toList()
                                                );
                                                courseDto.setPrediction(predictionDto);
                                                
                                                return Mono.just(courseDto);
                                            } else {
                                                return Mono.empty();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            return Mono.empty();
                                        }
                                    });
                            })
                        )
                    )
                    .collectList()
                    .flatMap(courses -> {
                        List<CourseDto> paginatedCourses = courses.stream()
                            .filter(c -> c != null)
                            .skip(page * size)
                            .limit(size)            
                            .toList();
        
                        Page<CourseDto> pageResult = new PageImpl<>(paginatedCourses, pageable, courses.size());
                        return Mono.just(pageResult);
                    })
                )
            )
            .defaultIfEmpty(new PageImpl<>(List.of(), pageable, 0));
    }

    public Mono<Page<CourseDto>> getRecomendedCoursesByHistoryAndAuth(Principal principal, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        return userRepository.findByEmail(principal.getName())
            .flatMap(user -> profileRepository.findByUserId(user.getId())
                .flatMap(profile -> {

                    Mono<String> lastViewedCategorySortMono = viewRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                        .take(1)
                        .next()
                        .flatMap(view -> courseRepository.findById(view.getCourseId())
                            .flatMap(course -> categoryRepository.findById(course.getCategoryId())
                                .map(category -> categoryService.standarizeCategory(category.getName()))
                            )
                        )
                        .defaultIfEmpty(profile.getInterest());

                    Mono<String> lastViewedModalitySortMono = viewRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                        .take(1)
                        .next()
                        .flatMap(view -> courseRepository.findById(view.getCourseId())
                            .map(course -> courseService.standarizeModality(course.getModality()))
                        )
                        .defaultIfEmpty(courseService.standarizeModality(profile.getPlatformPrefered()));
                    
                    return lastViewedCategorySortMono.flatMapMany(lastViewedCategory -> lastViewedModalitySortMono
                        .flatMapMany(lastViewedModality -> courseRepository.findAll()
                            .flatMap(course -> categoryRepository.findById(course.getCategoryId())
                                .flatMap(category -> institutionRepository.findById(course.getInstitutionId())
                                    .flatMap(institution -> {

                                        Mono<Double> ratingAvgMono = reviewRepository.getAverageRatingByCourseId(course.getId())
                                            .defaultIfEmpty(0.0);
                                        
                                        Mono<String> maxReactionMono = reactionRepository.findMostCommonReactionByCourseId(course.getId())
                                            .map(MostCommonReactionDto::getType)
                                            .defaultIfEmpty("NONE");
                                        
                                        Mono<Long> viewsCountMono = viewRepository.countByCourseId(course.getId())
                                            .defaultIfEmpty(0L);
                                        
                                        Mono<Long> reviewsCountMono = reviewRepository.countByCourseId(course.getId())
                                            .defaultIfEmpty(0L);

                                        Flux<Review> reviewFlux = reviewRepository.findByCourseId(course.getId());
                                        
                                        return Mono.zip(ratingAvgMono, maxReactionMono, viewsCountMono, reviewsCountMono, reviewFlux.collectList())
                                            .flatMap(tuple -> {
                                                Double ratingAvg = tuple.getT1();
                                                String maxReaction = tuple.getT2();
                                                Long viewsCount = tuple.getT3();
                                                Long reviewsCount = tuple.getT4();
                                                
                                                try {
                                                    Instance instance = new DenseInstance(13);
                                                    instance.setDataset(dataStructure);
                                                    // Usar la categoría del último curso visto en lugar del interés del perfil
                                                    instance.setValue(0, lastViewedCategory);
                                                    instance.setValue(1, profile.getAvailableHoursTime());
                                                    instance.setValue(2, profile.getBudget());
                                                    instance.setValue(3, lastViewedModality);
                                                    instance.setValue(4, courseService.standarizeModality(course.getModality()));
                                                    instance.setValue(5, courseService.standarizeDuration(course.getDuration()));
                                                    instance.setValue(6, course.getPrice() == null ? 0 : course.getPrice());
                                                    instance.setValue(7, categoryService.standarizeCategory(category.getName()));
                                                    instance.setValue(8, ratingAvg);
                                                    instance.setValue(9, maxReaction);
                                                    instance.setValue(10, viewsCount.intValue());
                                                    instance.setValue(11, reviewsCount.intValue());

                                                    double predictionValue = classifier.classifyInstance(instance);
                                                    String prediction = dataStructure.classAttribute().value((int) predictionValue);

                                                    double[] probabilities = classifier.distributionForInstance(instance);
                                                    double confidence = probabilities[(int) predictionValue];
                                                    DecimalFormat df = new DecimalFormat("#.#");
                                                    String confidencePercentage = df.format(confidence * 100) + "%";

                                                    if (prediction.equals("true")) {
                                                        PredictionDataDto predictionDto = new PredictionDataDto();
                                                        predictionDto.setUserInterest(instance.stringValue(0));
                                                        predictionDto.setUserAvailableTime(instance.value(1));
                                                        predictionDto.setBudget((int) instance.value(2));
                                                        predictionDto.setPlatformPreference(instance.stringValue(3));
                                                        predictionDto.setCourseModality(instance.stringValue(4));
                                                        predictionDto.setCourseDuration((int) instance.value(5));
                                                        predictionDto.setCoursePrice(instance.value(6));
                                                        predictionDto.setCourseCategory(instance.stringValue(7));
                                                        predictionDto.setCourseRatingAvg(instance.value(8));
                                                        predictionDto.setCourseMaxReaction(instance.stringValue(9));
                                                        predictionDto.setCourseVisits((int) instance.value(10));
                                                        predictionDto.setCourseReviewsCount((int) instance.value(11));
                                                        predictionDto.setCourseRecomended(prediction.equals("true"));
                                                        predictionDto.setConfidence(confidencePercentage);

                                                        CourseDto courseDto = courseMapper.toCourseDto(course);
                                                        courseDto.setCategory(categoryMapper.toCategoryDto(category));
                                                        courseDto.setInstitution(institutionMapper.toInstitutionDto(institution));
                                                        courseDto.setReviews(tuple.getT5().stream()
                                                            .map(reviewMapper::toReviewDto)
                                                            .toList()
                                                        );
                                                        courseDto.setPrediction(predictionDto);
                                                        
                                                        return Mono.just(courseDto);
                                                    } else {
                                                        return Mono.empty();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    return Mono.empty();
                                                }
                                            });
                                    })
                                )
                            )
                        )
                    )
                    .collectList()
                    .flatMap(courses -> {
                        List<CourseDto> paginatedCourses = courses.stream()
                            .filter(c -> c != null)
                            .skip(page * size)
                            .limit(size)            
                            .toList();
        
                        Page<CourseDto> pageResult = new PageImpl<>(paginatedCourses, pageable, courses.size());
                        return Mono.just(pageResult);
                    });
                })
            )
            .defaultIfEmpty(new PageImpl<>(List.of(), pageable, 0));
    }

    public Mono<Page<UserDto>> getRecomendedUsersByCourse(String courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return courseRepository.findById(courseId)
            .flatMap(course -> categoryRepository.findById(course.getCategoryId())
                .flatMap(category -> institutionRepository.findById(course.getInstitutionId())
                    .flatMap(institution -> userRepository.findAll()
                        .flatMap(user -> profileRepository.findByUserId(user.getId())
                            .flatMap(profile -> {
                                Mono<Double> ratingAvgMono = reviewRepository.getAverageRatingByCourseId(course.getId())
                                    .defaultIfEmpty(0.0);
                                
                                Mono<String> maxReactionMono = reactionRepository.findMostCommonReactionByCourseId(course.getId())
                                    .map(MostCommonReactionDto::getType)
                                    .defaultIfEmpty("NONE");
                                
                                Mono<Long> viewsCountMono = viewRepository.countByCourseId(course.getId())
                                    .defaultIfEmpty(0L);
                                
                                Mono<Long> reviewsCountMono = reviewRepository.countByCourseId(course.getId())
                                    .defaultIfEmpty(0L);

                                Flux<Review> reviewFlux = reviewRepository.findByCourseId(course.getId());

                                return Mono.zip(ratingAvgMono, maxReactionMono, viewsCountMono, reviewsCountMono, reviewFlux.collectList())
                                    .flatMap(tuple -> {
                                        Double ratingAvg = tuple.getT1();
                                        String maxReaction = tuple.getT2();
                                        Long viewsCount = tuple.getT3();
                                        Long reviewsCount = tuple.getT4();
                                        
                                        try {
                                            Instance instance = new DenseInstance(13);
                                            instance.setDataset(dataStructure);
                                            instance.setValue(0, profile.getInterest());
                                            instance.setValue(1, profile.getAvailableHoursTime());
                                            instance.setValue(2, profile.getBudget());
                                            instance.setValue(3, courseService.standarizeModality(profile.getPlatformPreference()));
                                            instance.setValue(4, courseService.standarizeModality(course.getModality()));
                                            instance.setValue(5, courseService.standarizeDuration(course.getDuration()));
                                            instance.setValue(6, course.getPrice() == null ? 0 : course.getPrice());
                                            instance.setValue(7, categoryService.standarizeCategory(category.getName()));
                                            instance.setValue(8, ratingAvg);
                                            instance.setValue(9, maxReaction);
                                            instance.setValue(10, viewsCount.intValue());
                                            instance.setValue(11, reviewsCount.intValue());

                                            double predictionValue = classifier.classifyInstance(instance);
                                            String prediction = dataStructure.classAttribute().value((int) predictionValue);

                                            double[] probabilities = classifier.distributionForInstance(instance);
                                            double confidence = probabilities[(int) predictionValue];
                                            DecimalFormat df = new DecimalFormat("#.#");
                                            String confidencePercentage = df.format(confidence * 100) + "%";

                                            if (prediction.equals("true")) {
                                                PredictionDataDto predictionDto = new PredictionDataDto();
                                                predictionDto.setUserInterest(instance.stringValue(0));
                                                predictionDto.setUserAvailableTime(instance.value(1));
                                                predictionDto.setBudget((int) instance.value(2));
                                                predictionDto.setPlatformPreference(instance.stringValue(3));
                                                predictionDto.setCourseModality(instance.stringValue(4));
                                                predictionDto.setCourseDuration((int) instance.value(5));
                                                predictionDto.setCoursePrice(instance.value(6));
                                                predictionDto.setCourseCategory(instance.stringValue(7));
                                                predictionDto.setCourseRatingAvg(instance.value(8));
                                                predictionDto.setCourseMaxReaction(instance.stringValue(9));
                                                predictionDto.setCourseVisits((int) instance.value(10));
                                                predictionDto.setCourseReviewsCount((int) instance.value(11));
                                                predictionDto.setCourseRecomended(prediction.equals("true"));
                                                predictionDto.setConfidence(confidencePercentage);

                                                UserDto userDto = userMapper.toUserDto(user);
                                                userDto.setProfile(profileMapper.toProfileDto(profile));
                                                userDto.setPrediction(predictionDto);
                                                
                                                return Mono.just(userDto);
                                            } else {
                                                return Mono.empty();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            return Mono.empty();
                                        }
                                    });
                            })
                        )
                        .collectList()
                        .flatMap(users -> {
                            List<UserDto> paginatedUsers = users.stream()
                                .filter(c -> c != null)
                                .skip(page * size)
                                .limit(size)            
                                .toList();
            
                            Page<UserDto> pageResult = new PageImpl<>(paginatedUsers, pageable, users.size());
                            return Mono.just(pageResult);
                        })
                    )
                )
            )
            .defaultIfEmpty(new PageImpl<>(List.of(), pageable, 0));
    }

    public Mono<Page<CourseDto>> getAllCoursesWithAvgConfidence(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String searchRegex = (search != null && !search.isEmpty()) ? ".*" + search + ".*" : ".*";
        Flux<Course> courseFlux = courseRepository.findByTitleRegexIgnoreCaseOrUrlRegexIgnoreCaseOrDurationRegexIgnoreCaseOrModalityRegexIgnoreCase(
            searchRegex, searchRegex, searchRegex, searchRegex, pageable
        );

        return courseFlux.flatMap(course -> {
                Mono<Category> categoryMono = categoryRepository.findById(course.getCategoryId());
                Mono<Institution> institutionMono = institutionRepository.findById(course.getInstitutionId());
                Flux<Content> contentFlux = contentRepository.findByCourseId(course.getId());
                Flux<Reaction> reactionFlux = reactionRepository.findByCourseId(course.getId());
                Flux<Review> reviewFlux = reviewRepository.findByCourseId(course.getId());
                Flux<ViewDto> viewFlux = viewRepository.findByCourseId(course.getId())
                    .flatMap(view -> userRepository.findById(view.getUserId())
                        .map(user -> {
                            ViewDto viewDto = viewMapper.toViewDto(view);
                            viewDto.setUser(new UserDto(user.getId(), user.getEmail()));
                            return viewDto;
                        })
                    );

                return Mono.zip(categoryMono, institutionMono, contentFlux.collectList(), reactionFlux.collectList(), reviewFlux.collectList(), viewFlux.collectList())
                    .map(tuple -> {
                        CourseDto courseDto = courseMapper.toCourseDto(course);
                        courseDto.setCategory(categoryMapper.toCategoryDto(tuple.getT1()));
                        courseDto.setInstitution(institutionMapper.toInstitutionDto(tuple.getT2()));
                        courseDto.setContents(tuple.getT3().stream()
                            .map(contentMapper::toContentDto)
                            .toList()
                        );
                        courseDto.setReactions(tuple.getT4().stream().map(reactionMapper::toReactionDto).toList());
                        courseDto.setReviews(tuple.getT5().stream()
                            .map(reviewMapper::toReviewDto)
                            .toList()
                        );
                        courseDto.setViews(tuple.getT6());

                        return courseDto;
                    });
            })
            .collectList()
            .zipWith(courseRepository.count())
            .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }
}
