package com.edu.hogwartsartifactonline.artifact;

import com.edu.hogwartsartifactonline.artifact.dto.ArtifactDTO;
import com.edu.hogwartsartifactonline.system.StatusCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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
class ArtifactControllerTest {

    List<Artifact> artifacts;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ArtifactService artifactService;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        artifacts = new ArrayList<>();

        Artifact a1 = new Artifact();
        a1.setId("1250808601744904191");
        a1.setName("Deluminator");
        a1.setDescription("A Deluminator is a device invented by Albus Dumbledore that resembles");
        a1.setImageUrl("image.url");
        artifacts.add(a1);

        Artifact a2 = new Artifact();
        a2.setId("1250808601744904192");
        a2.setName("Invisibility cloak");
        a2.setDescription("An invisibility cloak is used to make the wearer invisible");
        a2.setImageUrl("image.url");
        artifacts.add(a2);

        Artifact a3 = new Artifact();
        a3.setId("1250808601744904193");
        a3.setName("The Marauder's Map");
        a3.setDescription("A magical map of Hogwarts created by Remus Lupin , Peter Pettigrew, Sirius Black III and" +
                " James Potter I.");
        a3.setImageUrl("image.url");
        artifacts.add(a3);

        Artifact a4 = new Artifact();
        a4.setId("1250808601744904194");
        a4.setName("The Marauder's Map");
        a4.setDescription("A magical map of Hogwarts created by Remus Lupin , Peter Pettigrew, Sirius Black III and" +
                " James Potter I.");
        a4.setImageUrl("image.url");
        artifacts.add(a4);

        Artifact a5 = new Artifact();
        a5.setId("1250808601744904195");
        a5.setName("The Sword of Gryffindor");
        a5.setDescription("A goblin-made sword adorned with large rubies on the pommel.");
        a5.setImageUrl("image.url");
        artifacts.add(a5);

        Artifact a6 = new Artifact();
        a6.setId("1250808601744904196");
        a6.setName("Resurrection Stone");
        a6.setDescription("The Resurrection Stone allows the holder to bring back deceased loved ones");
        a6.setImageUrl("image.url");
        artifacts.add(a6);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindArtifactByIdSuccess() throws Exception {

        // Given:
        given(this.artifactService.findById("1250808601744904191")).willReturn(this.artifacts.get(0));

        // When and then:
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/artifacts/1250808601744904191")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find One Success"))
                .andExpect(jsonPath("$.data.id").value("1250808601744904191"))
                .andExpect(jsonPath("$.data.name").value("Deluminator"));
    }

    @Test
    void testFindArtifactByIdNotFound() throws Exception {

        // Given:
        given(this.artifactService.findById("1250808601744904191"))
                .willThrow(new ArtifactNotFoundException("1250808601744904191"));

        // When and then:
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/artifacts/1250808601744904191")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message")
                        .value("Could not find artifact with Id: 1250808601744904191, we are sorry :/"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testFindAllArtifactsSuccess() throws Exception {

        // Given:
        given(this.artifactService.findAll()).willReturn(this.artifacts);

        // When and then:
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/artifacts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Find All Success"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(artifacts.size())))
                .andExpect(jsonPath("$.data[0].id").value("1250808601744904191"))
                .andExpect(jsonPath("$.data[0].imageUrl").value("image.url"))
                .andExpect(jsonPath("$.data[1].id").value("1250808601744904192"))
                .andExpect(jsonPath("$.data[1].imageUrl").value("image.url"));
    }

    @Test
    void testCrateNewArtifactSuccess() throws Exception{
        // Given
        ArtifactDTO artifactDTO = new ArtifactDTO(null, "Remembrall",
                "A Remembrall was a magical large marble-sized glass ball that contained smoke, which" +
                        "turned red when its owner or user had forgotten something, it turned clear once whatever" +
                        "was forgotten was remembered", "image.url", null);

        String json = objectMapper.writeValueAsString(artifactDTO);

        Artifact savedArtifact = new Artifact();
        savedArtifact.setId("1250808601744904197");
        savedArtifact.setName("Remembrall");
        savedArtifact.setDescription("A Remembrall was a magical large marble-sized glass ball that contained smoke, which" +
                "turned red when its owner or user had forgotten something, it turned clear once whatever" +
                "was forgotten was remembered");
        savedArtifact.setImageUrl("image.url");

        given(artifactService.save(Mockito.any(Artifact.class))).willReturn(savedArtifact);


        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/artifacts")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Add Success"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.name").value(savedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(savedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(savedArtifact.getImageUrl()));
    }

    @Test
    void testUpdateArtifactSuccess() throws Exception {
        // Given
        ArtifactDTO artifactDTO = new ArtifactDTO("1250808601744904192", "Invisibility Cloak",
                "You go invisible brr", "image.url", null);

        String json = objectMapper.writeValueAsString(artifactDTO);

        Artifact updatedArtifact = new Artifact();
        updatedArtifact.setId("1250808601744904192");
        updatedArtifact.setName("Invisibility Cloak");
        updatedArtifact.setDescription("You go invisible brr");
        updatedArtifact.setImageUrl("image.url");

        given(artifactService.update(eq("1250808601744904192"), Mockito.any(Artifact.class))).willReturn(updatedArtifact);


        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/artifacts/1250808601744904192")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Update Success"))
                .andExpect(jsonPath("$.data.id").value("1250808601744904192"))
                .andExpect(jsonPath("$.data.name").value(updatedArtifact.getName()))
                .andExpect(jsonPath("$.data.description").value(updatedArtifact.getDescription()))
                .andExpect(jsonPath("$.data.imageUrl").value(updatedArtifact.getImageUrl()));
    }

    @Test
    void testUpdateArtifactErrorWithNonExistentId() throws Exception{
        // Given
        ArtifactDTO artifactDTO = new ArtifactDTO("1250808601744904192", "Invisibility Cloak",
                "You go invisible brr", "image.url", null);

        String json = objectMapper.writeValueAsString(artifactDTO);

        given(artifactService.update(eq("1250808601744904192"), Mockito.any(Artifact.class)))
                .willThrow(new ArtifactNotFoundException(artifactDTO.id()));


        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/artifacts/1250808601744904192")
                        .contentType(MediaType.APPLICATION_JSON).content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message")
                        .value("Could not find artifact with Id: 1250808601744904192, we are sorry :/"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void testDeleteArtifactSuccess() throws Exception{
        // Given
        doNothing().when(artifactService).delete("1250808601744904192");

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/artifacts/1250808601744904192")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(jsonPath("$.message").value("Delete Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testDeleteArtifactNotFound() throws Exception{
        // Given
        doThrow(new ArtifactNotFoundException("1250808601744904192")).when(artifactService)
                .delete("1250808601744904192");

        // When and Then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/artifacts/1250808601744904192")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
                .andExpect(jsonPath("$.message")
                        .value("Could not find artifact with Id: 1250808601744904192, we are sorry :/"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

}