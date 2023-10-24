package ru.practicum.ewm.user.mapper.impl;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMapperImpl implements UserMapper {
    @Override
    public User mapToUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    @Override
    public UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public List<UserDto> mapToUserDto(List<User> users) {
        return users.stream().map(this::mapToUserDto).collect(Collectors.toList());
    }
}
