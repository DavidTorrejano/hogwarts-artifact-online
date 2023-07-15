package com.edu.hogwartsartifactonline.hogwartsuser.converter;

import com.edu.hogwartsartifactonline.hogwartsuser.HogwartsUser;
import com.edu.hogwartsartifactonline.hogwartsuser.UserRepository;
import com.edu.hogwartsartifactonline.hogwartsuser.UserService;
import com.edu.hogwartsartifactonline.hogwartsuser.dto.UserDTO;
import com.edu.hogwartsartifactonline.system.exception.ObjectNotFoundException;
import org.h2.engine.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    List<HogwartsUser> users;

    @BeforeEach
    void setUp() {

        HogwartsUser u1 = new HogwartsUser();
        u1.setId(1);
        u1.setUsername("John");
        u1.setPassword("12345");
        u1.setEnabled(true);
        u1.setRoles("user");

        HogwartsUser u2 = new HogwartsUser();
        u2.setId(2);
        u2.setUsername("Javier");
        u2.setPassword("abcdf");
        u2.setEnabled(true);
        u2.setRoles("user");

        HogwartsUser u3 = new HogwartsUser();
        u3.setId(3);
        u3.setUsername("Adrien");
        u3.setPassword("qwerty");
        u3.setEnabled(false);
        u3.setRoles("user");

        users = new ArrayList<>();
        users.add(u1);
        users.add(u2);
        users.add(u3);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindAllUsersSuccess() {
        // Given
        given(userRepository.findAll()).willReturn(users);

        // When
        List<HogwartsUser> actualUsers = userService.findAll();

        // Then
        assertThat(actualUsers.size()).isEqualTo(3);
        assertThat(actualUsers.get(2).getUsername()).isEqualTo("Adrien");

        // Verify number of times method findAll is used

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testSaveUserSuccess(){
        // Given
        HogwartsUser u1 = new HogwartsUser();
        u1.setId(1);
        u1.setUsername("John");
        u1.setPassword("12345");
        u1.setEnabled(true);
        u1.setRoles("user");

        given(userRepository.save(u1)).willReturn(u1);

        // When
        HogwartsUser returnedUser = userService.save(u1);

        // Then
        assertThat(returnedUser.getUsername()).isEqualTo("John");
        assertThat(returnedUser.getPassword()).isEqualTo(u1.getPassword());
        assertThat(returnedUser.isEnabled()).isTrue();

    }

    @Test
    void testFindUserByIdSuccess(){
        // Given
        HogwartsUser u1 = new HogwartsUser();
        u1.setId(1);
        u1.setUsername("John");
        u1.setPassword("12345");
        u1.setEnabled(true);
        u1.setRoles("user");

        given(userRepository.findById(1)).willReturn(Optional.of(u1));

        // When
        HogwartsUser obtainedUser = userService.findById(1);

        // Then
        assertThat(obtainedUser.getUsername()).isEqualTo(u1.getUsername());
        assertThat(obtainedUser.getPassword()).isEqualTo("12345");
        assertThat(obtainedUser.getRoles()).isEqualTo("user");

        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testFindUserByIdNotFound(){
        // Given
        given(userRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());

        //When
        Throwable thrown = catchThrowable(()->
                userService.findById(1)
        );

        // Then
        assertThat(thrown).isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find User with Id: 1, we are sorry :/");

        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testUpdateUserSuccess(){
        // Given
        HogwartsUser oldUser = new HogwartsUser();
        oldUser.setId(3);
        oldUser.setUsername("Adrien");
        oldUser.setPassword("qwerty");
        oldUser.setEnabled(false);
        oldUser.setRoles("user");

        HogwartsUser update = new HogwartsUser();
        update.setUsername("Adrien Kujo");
        update.setEnabled(true);
        update.setRoles("user admin");

        given(userRepository.findById(oldUser.getId())).willReturn(Optional.of(oldUser));
        given(userRepository.save(oldUser)).willReturn(oldUser);

        // When
        HogwartsUser updatedUser = userService.update(3, update);

        // Then
        assertThat(updatedUser.getId()).isEqualTo(3);
        assertThat(updatedUser.getUsername()).isEqualTo("Adrien Kujo");
        assertThat(updatedUser.isEnabled()).isTrue();

        Mockito.verify(userRepository, times(1)).findById(3);
        verify(userRepository, times(1)).save(oldUser);

    }

    @Test
    void testUpdateUserNotSuccess(){
        // Given

        HogwartsUser update = new HogwartsUser();
        update.setUsername("Armando");
        update.setEnabled(true);
        update.setRoles("user");

        given(userRepository.findById(2)).willReturn(Optional.empty());

        //When
        Throwable thrown = catchThrowable(()->
                userService.update(2, update)
        );

        // Then
        assertThat(thrown).isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find User with Id: 2, we are sorry :/");

        verify(userRepository, times(1)).findById(2);
    }

    @Test
    void testDeleteUserByIdSuccess(){
        //Given
        HogwartsUser oldUser = new HogwartsUser();
        oldUser.setId(3);
        oldUser.setUsername("Adrien");
        oldUser.setPassword("qwerty");
        oldUser.setEnabled(false);
        oldUser.setRoles("user");

        given(userRepository.findById(3)).willReturn(Optional.of(oldUser));
        doNothing().when(userRepository).deleteById(3);

        // When
        userService.deleteById(3);

        // Then
        verify(userRepository, times(1)).deleteById(3);

    }

    @Test
    void testDeleteUserByIdNotFound(){
        // Given
        given(userRepository.findById(3)).willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () ->
                userService.deleteById(3)
                );

        // Then
        verify(userRepository, times(1)).findById(3);
    }
}