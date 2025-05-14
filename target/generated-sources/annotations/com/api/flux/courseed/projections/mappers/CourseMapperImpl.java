package com.api.flux.courseed.projections.mappers;

import com.api.flux.courseed.persistence.documents.Course;
import com.api.flux.courseed.projections.dtos.CourseDto;
import com.api.flux.courseed.projections.dtos.SaveCourseDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-14T13:00:55-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Eclipse Adoptium)"
)
@Component
public class CourseMapperImpl implements CourseMapper {

    @Override
    public Course toCourse(SaveCourseDto saveCourseDto) {
        if ( saveCourseDto == null ) {
            return null;
        }

        Course course = new Course();

        course.setUrl( saveCourseDto.getUrl() );
        course.setTitle( saveCourseDto.getTitle() );
        course.setDescription( saveCourseDto.getDescription() );
        course.setPrerequisites( saveCourseDto.getPrerequisites() );
        course.setPrice( saveCourseDto.getPrice() );
        course.setDuration( saveCourseDto.getDuration() );
        course.setModality( saveCourseDto.getModality() );
        course.setCategoryId( saveCourseDto.getCategoryId() );
        course.setInstitutionId( saveCourseDto.getInstitutionId() );

        return course;
    }

    @Override
    public CourseDto toCourseDto(Course course) {
        if ( course == null ) {
            return null;
        }

        CourseDto courseDto = new CourseDto();

        courseDto.setId( course.getId() );
        courseDto.setUrl( course.getUrl() );
        courseDto.setTitle( course.getTitle() );
        courseDto.setImage( course.getImage() );
        courseDto.setDescription( course.getDescription() );
        courseDto.setPrice( course.getPrice() );
        courseDto.setType( course.getType() );
        courseDto.setDuration( course.getDuration() );
        courseDto.setModality( course.getModality() );

        return courseDto;
    }
}
