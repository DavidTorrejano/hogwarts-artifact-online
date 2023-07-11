package com.edu.hogwartsartifactonline.wizard;

import com.edu.hogwartsartifactonline.artifact.Artifact;
import com.edu.hogwartsartifactonline.system.StatusCode;
import com.edu.hogwartsartifactonline.system.exception.ObjectNotFoundException;
import com.edu.hogwartsartifactonline.wizard.dto.WizardDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class WizardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    WizardService wizardService;

    @Autowired
    ObjectMapper objectMapper;

    List<Wizard> wizards;
    List<Artifact> artifacts;
    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @BeforeEach
    void setUp() {

        wizards = new ArrayList<>();
        artifacts = new ArrayList<>();

        Artifact artifact = new Artifact();
        artifact.setId("1250808601744904192");
        artifact.setName("Invisibility cloak");
        artifact.setDescription("An invisibility cloak is used to make the wearer invisible");
        artifact.setImageUrl("image.url");

        artifacts.add(artifact);

        Wizard w1 = new Wizard();
        w1.setId(1);
        w1.setName("Albus Dumbledore");
        w1.setArtifacts(artifacts);

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Harry Potter");

        Wizard w3 = new Wizard();
        w3.setId(3);
        w3.setName("Neville Longbottom");

        wizards.add(w1);
        wizards.add(w2);
        wizards.add(w3);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindAllWizardsSuccess() throws Exception {

        // Given:
        given(this.wizardService.findAll()).willReturn(this.wizards);

        // When and Then:
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/wizards")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data[0].name").value("Albus Dumbledore"))
                .andExpect(jsonPath("$.data[0].numberOfArtifacts").value(1))
                .andExpect(jsonPath("$.data[2].name").value("Neville Longbottom"));

    }

    @Test
    void testSaveNewWizardSuccess() throws Exception{
        // Given
        WizardDTO wizardDTO = new WizardDTO(null, "Harry Potter", 0);

        String json = objectMapper.writeValueAsString(wizardDTO);

        Wizard savedWizard = new Wizard();
        savedWizard.setId(2);
        savedWizard.setName("Harry Potter");

        given(wizardService.save(Mockito.any(Wizard.class))).willReturn(savedWizard);

        // Then and When

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/wizards")
                .contentType(MediaType.APPLICATION_JSON).content(json)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Save Success"))
                .andExpect(jsonPath("$.data.name").value("Harry Potter"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.numberOfArtifacts").value(0));

    }

    @Test
    void testFindWizardByIdSuccess() throws Exception{
        // Given
        given(wizardService.findById(1)).willReturn(wizards.get(0));

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/wizards/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.name").value("Albus Dumbledore"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.numberOfArtifacts").value(1));
    }

    @Test
    void testFindWizardByIdNotSuccess() throws Exception {
        // Given
        given(wizardService.findById(1))
                .willThrow(new ObjectNotFoundException("Wizard", 1));

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/wizards/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message")
                        .value("Could not find Wizard with Id: 1, we are sorry :/"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testUpdateWizardSuccess() throws Exception{
        // Given
        WizardDTO wizardDTO = new WizardDTO(1, "Albus Dumbledore", 2);

        String json = objectMapper.writeValueAsString(wizardDTO);

        Wizard updatedWizard = new Wizard();
        updatedWizard.setId(1);
        updatedWizard.setName("Albus Dumbledore");

        given(wizardService.update(eq(1), Mockito.any(Wizard.class))).willReturn(updatedWizard);

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/wizards/1")
                .contentType(MediaType.APPLICATION_JSON).content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.name").value("Albus Dumbledore"));


    }

    @Test
    void testUpdateWizardNotSuccess() throws Exception{
        // Given
        WizardDTO wizardDTO = new WizardDTO(1, "Harry Potter", 1);

        String json = objectMapper.writeValueAsString(wizardDTO);

        given(wizardService.update(eq(1), Mockito.any(Wizard.class)))
                .willThrow(new ObjectNotFoundException("Wizard", 1));

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/wizards/1")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message")
                        .value("Could not find Wizard with Id: 1, we are sorry :/"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteWizardSuccess() throws Exception{
        // Given
        doNothing().when(wizardService).delete(1);

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/wizards/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void testDeleteWizardNotFound() throws Exception{
        // Given
        doThrow(new ObjectNotFoundException("Wizard", 1)).when(wizardService).delete(1);

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/wizards/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message")
                        .value("Could not find Wizard with Id: 1, we are sorry :/"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAssignArtifactSuccess() throws Exception{
        // Given
        doNothing().when(wizardService).assignArtifact(2, "1250808601744904192");

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/wizards/2/artifacts/1250808601744904192")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message")
                        .value("Artifact Assignment Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAssignArtifactErrorWithNonExistentWizardId() throws Exception{
        // Given
        doThrow(new ObjectNotFoundException("Wizard", 2)).when(wizardService)
                        .assignArtifact(2, "1250808601744904192");

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/wizards/2/artifacts/1250808601744904192")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message")
                        .value("Could not find Wizard with Id: 2, we are sorry :/"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAssignArtifactErrorWithNonExistentArtifactId() throws Exception{
        // Given
        doThrow(new ObjectNotFoundException("Artifact", "1250808601744904192")).when(wizardService)
                .assignArtifact(2, "1250808601744904192");

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/wizards/2/artifacts/1250808601744904192")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message")
                        .value("Could not find Artifact with Id: 1250808601744904192, we are sorry :/"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

}