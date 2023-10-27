package ru.practicum.ewm.category.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        log.info(String.format("createUser %s", categoryDto));

        if (categoryRepository.existsCategoriesByName(categoryDto.getName())) {
            throw new ConflictException("Такая категория уже создана.");
        }

        Category createdCategory = categoryRepository.save(categoryMapper.mapToCategory(categoryDto));
        return categoryMapper.mapToCategoryDto(createdCategory);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        log.debug(String.format("getCategories from-%d size-%d", from, size));

        int offset = from > 0 ? from / size : 0;
        PageRequest page = PageRequest.of(offset, size);

        List<Category> categories = categoryRepository.findAll(page).getContent();

        return categories.stream().map(categoryMapper::mapToCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Не найдена выбранная категория"));

        return categoryMapper.mapToCategoryDto(category);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        log.info(String.format("updateCategory categoryDto-%s", categoryDto));

        Category category = categoryRepository.findById(categoryDto.getId()).orElseThrow(
                () -> new NotFoundException("Не найдена выбранная категория"));

        if (categoryRepository.existsCategoriesByNameAndIdNot(categoryDto.getName(), categoryDto.getId())) {
            throw new ConflictException("Такая категория уже есть");
        }

        category.setName(categoryDto.getName());

        Category updatedCategory = categoryRepository.save(category);

        return categoryMapper.mapToCategoryDto(updatedCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        log.info(String.format("deleteCategory categoryId-%d", categoryId));

        categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Не найдена выбранная категория"));

        if (eventRepository.existsEventsByCategory_Id(categoryId)) {
            throw new ConflictException("Такой пользователь уже есть");
        }

        categoryRepository.deleteById(categoryId);

    }
}
