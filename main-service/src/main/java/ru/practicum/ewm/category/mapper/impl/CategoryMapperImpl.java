package ru.practicum.ewm.category.mapper.impl;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapperImpl implements CategoryMapper {
    @Override
    public Category mapToCategory(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    @Override
    public CategoryDto mapToCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    @Override
    public List<CategoryDto> mapToCategoryDto(List<Category> categories) {
        return categories.stream().map(this::mapToCategoryDto).collect(Collectors.toList());
    }
}
