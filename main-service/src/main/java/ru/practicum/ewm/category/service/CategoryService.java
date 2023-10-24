package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoriesId(Long catId);

    CategoryDto createCategories(CategoryDto categoryDto);

    CategoryDto updateCategories(CategoryDto categoryDto);

    void deleteCategories(Long catId);

}
