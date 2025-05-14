package com.api.flux.courseed.projections.mappers;

import com.api.flux.courseed.persistence.documents.Course;
import com.api.flux.courseed.projections.dtos.CourseDto;
import com.api.flux.courseed.projections.dtos.SaveCourseDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-05T17:28:02-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.z20250331-1358, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class CourseMapperImpl implements CourseMapper {

    @Override
    public Course toCourse(SaveCourseDto saveCourseDto) {
        if ( saveCourseDto == null ) {
            return null;
        }

        Course course = new Course();

        course.setCategoryId( saveCourseDto.getCategoryId() );
        course.setDescription( saveCourseDto.getDescription() );
        course.setDuration( saveCourseDto.getDuration() );
        course.setImage( saveCourseDto.getImage() );
        course.setInstitutionId( saveCourseDto.getInstitutionId() );
        course.setModality( saveCourseDto.getModality() );
        course.setPrerequisites( saveCourseDto.getPrerequisites() );
        course.setPrice( saveCourseDto.getPrice() );
        course.setTitle( saveCourseDto.getTitle() );
        course.setUrl( saveCourseDto.getUrl() );

        return course;
    }

    @Override
    public CourseDto toCourseDto(Course course) {
        if ( course == null ) {
            return null;
        }

        CourseDto courseDto = new CourseDto();

        courseDto.setDescription( course.getDescription() );
        courseDto.setDuration( course.getDuration() );
        courseDto.setId( course.getId() );
        courseDto.setImage( course.getImage() );
        courseDto.setModality( course.getModality() );
        courseDto.setPrice( course.getPrice() );
        courseDto.setTitle( course.getTitle() );
        courseDto.setType( course.getType() );
        courseDto.setUrl( course.getUrl() );

        return courseDto;
    }
}
