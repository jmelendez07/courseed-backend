package com.api.flux.courseed.projections.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.api.flux.courseed.persistence.documents.Category;
import com.api.flux.courseed.projections.dtos.CategoryDto;
import com.api.flux.courseed.projections.dtos.SaveCategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category toCategory(SaveCategoryDto saveCategoryDto);
}
