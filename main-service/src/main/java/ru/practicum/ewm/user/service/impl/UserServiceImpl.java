package ru.practicum.ewm.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userRequestDto) {
        log.info("createUser "+userRequestDto);

        if (userRepository.existsUserByName(userRequestDto.getName())) {
            throw new ConflictException("Username already used.");
        }

        User createdUser = userRepository.save(userMapper.mapToUser(userRequestDto));

        return userMapper.mapToUserDto(createdUser);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users;

        int offset = from > 0 ? from / size : 0;
        PageRequest page = PageRequest.of(offset, size);

        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(page).getContent();
        } else {
            users = userRepository.findAllByIdIn(ids, page);
        }

        return userMapper.mapToUserDto(users);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info(String.format("Delete user id-%d", userId));

        try {
            userRepository.deleteById(userId);
            log.info(String.format("User id-%d deleted", userId));

        } catch (EmptyResultDataAccessException e) {
            String errorMessage = String.format("User id-%d not found", userId);
            log.info(errorMessage);

            throw new NotFoundException(errorMessage);
        }
    }
}
