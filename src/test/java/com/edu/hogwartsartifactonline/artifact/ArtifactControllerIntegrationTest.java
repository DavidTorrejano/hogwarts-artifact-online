package com.edu.hogwartsartifactonline.artifact;

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
@DisplayName("Integration Tests for Artifact API endpoints")
@Tag("integration")
public class ArtifactControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    String token;

    /**
     *
     * @throws Exception
     * In order to user the "httpBasic" to mock the login, we need to bring to our project the dependency
     * spring-security-test, this will allow us to make use of protected endpoints.
     */
    @BeforeEach
    void setUp() throws Exception{
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/users/login")
                .with(httpBasic("Javier", "abcdf")));

        MvcResult mvcResult = resultActions.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(contentAsString);
        token = "Bearer " + json.getJSONObject("data").getString("token");
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @DisplayName("Find all artifacts success")
    void testFindAllArtifactsSuccess() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/artifacts")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(6)));
    }

    @Test
    @DisplayName("Check add artifact with valid input (Post)")
    void testAddArtifactSuccess() throws Exception {

        Artifact artifact = new Artifact();
        artifact.setName("Remembrall");
        artifact.setDescription("A Remembrall was a magical large marble-sized glass ball that contained smoke");
        artifact.setImageUrl("image.url");

        String json = objectMapper.writeValueAsString(artifact);

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/artifacts")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", token).content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value("Remembrall"))
                .andExpect(jsonPath("$.data.imageUrl").value("image.url"));
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/artifacts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(7)));


    }

    @Test
    @DisplayName("Check add artifact with invalid input (Post)")
    void testAddArtifactNotSuccess() throws Exception {

        Artifact artifact = new Artifact();
        artifact.setName("");
        artifact.setDescription("A Remembrall was a magical large marble-sized glass ball that contained smoke");
        artifact.setImageUrl("image.url");

        String json = objectMapper.writeValueAsString(artifact);

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/artifacts")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", token).content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.INVALID_ARGUMENT))
                .andExpect(jsonPath("$.message")
                        .value("Provided arguments are invalid, see data for details."))
                .andExpect(jsonPath("$.data").isMap());
    }

    @Test
    @DisplayName("Finding one artifact success")
    void testFindOneArtifactSuccess() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/artifacts/1250808601744904191")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.name").value("Deluminator"))
                .andExpect(jsonPath("$.data.id").value("1250808601744904191"));
    }


    @Test
    @DisplayName("Not being able to find the artifact")
    void testFindOneArtifactNotSuccess() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/artifacts/125080860174490419")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message")
                        .value("Could not find Artifact with Id: 125080860174490419, we are sorry :/"))
                .andExpect(jsonPath("$.data").isEmpty());
    }


    @Test
    @DisplayName("Successfully updating an artifact")
    void testUpdateArtifactSuccess() throws Exception{
        Artifact a1 = new Artifact();
        a1.setName("Deluminator");
        a1.setDescription("A Deluminator is a device invented by Albus Dumbledore that does nothing for a muggles");
        a1.setImageUrl("image.url");

        String json = objectMapper.writeValueAsString(a1);

        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/artifacts/1250808601744904191")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", token).content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.description")
                        .value("A Deluminator is a device invented by " +
                                "Albus Dumbledore that does nothing for a muggles"));
    }

    @Test
    @DisplayName("Updating a non existing artifact")
    void testUpdateArtifactNotFound() throws Exception{
        Artifact a1 = new Artifact();
        a1.setName("Deluminator");
        a1.setDescription("A Deluminator is a device invented by Albus Dumbledore that does nothing for a muggles");
        a1.setImageUrl("image.url");

        String json = objectMapper.writeValueAsString(a1);

        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/artifacts/2323232323")
                .contentType(MediaType.APPLICATION_JSON).header("Authorization", token).content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message")
                        .value("Could not find Artifact with Id: 2323232323, we are sorry :/"));
    }

    @Test
    @DisplayName("Deleting an artifact")
    void testDeleteArtifactSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/artifacts/1250808601744904193")
                .accept(MediaType.APPLICATION_JSON).header("Authorization", token))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.data").isEmpty());
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/artifacts")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data", Matchers.hasSize(5)));
    }

}
