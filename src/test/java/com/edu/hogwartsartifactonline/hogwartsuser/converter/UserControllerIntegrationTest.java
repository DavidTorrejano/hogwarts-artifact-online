package com.edu.hogwartsartifactonline.hogwartsuser.converter;

import com.edu.hogwartsartifactonline.hogwartsuser.HogwartsUser;
import com.edu.hogwartsartifactonline.system.StatusCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for user API endpoints")
@Tag("integration")
public class UserControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    String token;

    /**
     *
     * @throws Exception this is in case there is something wrong.
     * @resultActions is the result of a http request using mockMvc.
     * @andDo(print()) allow us to make an additional operation during the test, in this case print the response
     * content on the console
     * @andReturn() It indicates that the test should return an "MvcResult" object containing information
     * about the simulated http response, such as: status, header and content.
     * @mvcResult.getResponse() We obtain the object "MockHttpServletResponse" from mvcResult, which contains the
     * simulated http response.
     * @getContentAsString Converts the content in the response (JSON or HTML) to a readable string.
     * @JSONObject Is a Java class that represents a Json object, and provides methods to access its properties
     * and values
     * @json.getJSONObject("data") Obtains the object associated with the property "data"
     * @getString("token") Obtains the value of the property "token" as a readable String.
     *
     *
     */
    @BeforeEach
    void setUp() throws Exception{
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/users/login")
                .with(httpBasic("Adrien", "qwerty")));

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(contentAsString);
        token = "Bearer " + json.getJSONObject("data").getString("token");
    }

    @Test
    @DisplayName("Getting a list with the existing users")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void testFindAllUsersSuccess() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/users")
                .accept(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(3)));
    }

    @Test
    @DisplayName("List of users with not ADMIN role")
    void testFindAllUsersNotSuccessUserRoleNotAllowed() throws Exception{
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/users/login")
                .with(httpBasic("Javier", "abcdf")));

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(contentAsString);
        String token = "Bearer " + json.getJSONObject("data").getString("token");

        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/users")
                .accept(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.FORBIDDEN))
                .andExpect(jsonPath("$.message").value("No permission."))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    @DisplayName("Adding a new user")
    void testCreateNewUserSuccess() throws Exception{
        HogwartsUser user = new HogwartsUser();
        user.setUsername("Andrea");
        user.setPassword("andreiitta");
        user.setEnabled(true);
        user.setRoles("user");

        String json = objectMapper.writeValueAsString(user);

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/users")
                .contentType(MediaType.APPLICATION_JSON).content(json)
                        .header("Authorization", token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Save User Success"))
                .andExpect(jsonPath("$.data.id").value(4))
                .andExpect(jsonPath("$.data.roles").value("user"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    @DisplayName("Creating user without password")
    void testCreateUserNotSuccessDueToLackOfInformation() throws Exception{
        HogwartsUser user = new HogwartsUser();
        user.setUsername("Andrea");
        user.setPassword("");
        user.setEnabled(true);
        user.setRoles("user");

        String json = objectMapper.writeValueAsString(user);

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/users")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .header("Authorization", token).accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message")
                        .value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data.id").doesNotExist())
                .andExpect(jsonPath("$.data.username").doesNotExist())
                .andExpect(jsonPath("$.data.password").value("password is required"));
    }



}
