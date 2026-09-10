package com.example.hotel.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.hotel.dto.response.UserResponse;
import com.example.hotel.entity.User;
import com.example.hotel.mapper.UserMapper;
import com.example.hotel.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldReturnUserById() {

        Long userId = 1L;

        User user = new User();
        user.setName("Wilmer");
        user.setEmail("wilmer@gmail.com");

        UserResponse response = new UserResponse();
        response.setName("Wilmer");
        response.setEmail("wilmer@gmail.com");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result = userService.getUserById(userId);

        assertEquals("Wilmer", result.getName());
        assertEquals("wilmer@gmail.com", result.getEmail());

        verify(userRepository).findById(userId);
        verify(userMapper).toResponse(user);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> {
            userService.getUserById(userId);
        });
        verify(userRepository).findById(userId);
    }

    @Test
    void shouldReturnUsers() {

        User user1 = new User();
        user1.setName("Wilmer");
        user1.setEmail("wilmer@gmail.com");

        User user2 = new User();
        user2.setName("Juan");
        user2.setEmail("juan@gmail.com");

        UserResponse response1 = new UserResponse();
        response1.setName("Wilmer");
        response1.setEmail("wilmer@gmail.com");

        UserResponse response2 = new UserResponse();
        response2.setName("Juan");
        response2.setEmail("juan@gmail.com");

        Pageable pageable = Pageable.ofSize(10);

        Page<User> userPage = new PageImpl<>(
                List.of(user1, user2));

        when(userRepository.findAll(pageable))
                .thenReturn(userPage);

        when(userMapper.toResponse(user1))
                .thenReturn(response1);

        when(userMapper.toResponse(user2))
                .thenReturn(response2);

        Page<UserResponse> result = userService.getUsers(pageable);

        assertEquals(2, result.getContent().size());

        assertEquals("Wilmer", result.getContent().get(0).getName());
        assertEquals("wilmer@gmail.com", result.getContent().get(0).getEmail());

        assertEquals("Juan", result.getContent().get(1).getName());
        assertEquals("juan@gmail.com", result.getContent().get(1).getEmail());

        verify(userRepository).findAll(pageable);
        verify(userMapper).toResponse(user1);
        verify(userMapper).toResponse(user2);
    }

    @Test
    void shouldReturnEmptyPageWhenThereAreNoUsers() {

        Pageable pageable = Pageable.ofSize(10);

        Page<User> emptyPage = new PageImpl<>(List.of());

        when(userRepository.findAll(pageable))
                .thenReturn(emptyPage);

        Page<UserResponse> result = userService.getUsers(pageable);

        assertEquals(0, result.getContent().size());

        verify(userRepository).findAll(pageable);
    }
}
