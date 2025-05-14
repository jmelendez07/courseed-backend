package com.api.flux.courseed.projections.mappers;

import com.api.flux.courseed.persistence.documents.Category;
import com.api.flux.courseed.projections.dtos.CategoryDto;
import com.api.flux.courseed.projections.dtos.SaveCategoryDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-05T12:22:52-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.z20250331-1358, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoryDto toCategoryDto(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setId( category.getId() );
        categoryDto.setName( category.getName() );

        return categoryDto;
    }

    @Override
    public Category toCategory(SaveCategoryDto saveCategoryDto) {
        if ( saveCategoryDto == null ) {
            return null;
        }

        Category category = new Category();

        category.setName( saveCategoryDto.getName() );

        return category;
    }
}
