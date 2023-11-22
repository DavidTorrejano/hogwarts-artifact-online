package com.edu.hogwartsartifactonline.hogwartsuser.converter;

import com.edu.hogwartsartifactonline.hogwartsuser.HogwartsUser;
import com.edu.hogwartsartifactonline.hogwartsuser.UserService;
import com.edu.hogwartsartifactonline.hogwartsuser.dto.UserDTO;
import com.edu.hogwartsartifactonline.system.StatusCode;
import com.edu.hogwartsartifactonline.system.exception.ObjectNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    List<HogwartsUser> hogwartsUsers;

    @BeforeEach
    void setUp() {

        hogwartsUsers = new ArrayList<>();

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


        hogwartsUsers.add(u1);
        hogwartsUsers.add(u2);
        hogwartsUsers.add(u3);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindAllUsersSuccess() throws Exception{
        // Given
        given(userService.findAll()).willReturn(hogwartsUsers);

        // Then and When
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data.size()").value(3))
                .andExpect(jsonPath("$.data[1].username").value("Javier"))
                .andExpect(jsonPath("$.data[1].roles").value("user"));
    }

    @Test
    void testSaveNewUserSuccess() throws Exception {
        // Given
        HogwartsUser u1 = new HogwartsUser();
        u1.setId(1);
        u1.setUsername("John");
        u1.setPassword("12345");
        u1.setEnabled(true);
        u1.setRoles("user");

        String json =objectMapper.writeValueAsString(u1);

        UserDTO userDTO = new UserDTO(1, "John", true, "user");

        given(userService.save(Mockito.any(HogwartsUser.class))).willReturn(u1);

        // Then and When
        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/users")
                .contentType(MediaType.APPLICATION_JSON).content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Save User Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value(userDTO.username()))
                .andExpect(jsonPath("$.data.password").doesNotExist());

    }

    @Test
    void testFindUserByIdSuccess() throws Exception{
        // Given
        HogwartsUser u1 = new HogwartsUser();
        u1.setId(1);
        u1.setUsername("John");
        u1.setPassword("12345");
        u1.setEnabled(true);
        u1.setRoles("user");

        UserDTO userDTO = new UserDTO(1, "John", true, "user");

        given(userService.findById(1)).willReturn(u1);

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/users/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value(userDTO.username()))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    void testFindUserByIdNotFound() throws Exception{
        // Given
        given(userService.findById(Mockito.any(Integer.class)))
                .willThrow(new ObjectNotFoundException("User", 1));

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message")
                        .value("Could not find User with Id: 1, we are sorry :/"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testUpdateUserSuccess() throws Exception{
        // Given
        UserDTO update = new UserDTO(2, "Ricardo Arjona", false, "admin");

        String json = objectMapper.writeValueAsString(update);

        HogwartsUser updatedUser = new HogwartsUser();
        updatedUser.setId(2);
        updatedUser.setUsername("Ricardo Arjona");
        updatedUser.setEnabled(false);
        updatedUser.setRoles("admin");

        given(userService.update(eq(2), Mockito.any(HogwartsUser.class)))
                .willReturn(updatedUser);

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/users/2")
                .contentType(MediaType.APPLICATION_JSON).content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update User Success"))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.enabled").value(false));
    }

    @Test
    void testUpdateUserNotFound() throws Exception{
        // Given
        UserDTO update = new UserDTO(2, "Ricardo Arjona", false, "admin");

        String json = objectMapper.writeValueAsString(update);

        given(userService.update(eq(2), Mockito.any(HogwartsUser.class)))
                .willThrow(new ObjectNotFoundException("User", 2));

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/users/2")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message")
                        .value("Could not find User with Id: 2, we are sorry :/"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteUserByIdSuccess() throws Exception{
        // Given
        doNothing().when(userService).deleteById(3);

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/users/3")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("User Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteUserByIdNotFound() throws Exception{
        // Given
        doThrow(new ObjectNotFoundException("User", 3)).when(userService)
                        .deleteById(3);

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/users/3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message")
                        .value("Could not find User with Id: 3, we are sorry :/"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

}