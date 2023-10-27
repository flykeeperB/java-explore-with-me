package ru.practicum.ewm.category.mapper.impl;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryMapperImpl implements CategoryMapper {
    @Override
    public Category mapToCategory(CategoryDto categoryDto) {
        Category category = Category.builder()
                .name(categoryDto.getName())
                .build();

        if (categoryDto.getId() != null) {
            category.setId(categoryDto.getId());
        }

        return category;
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
