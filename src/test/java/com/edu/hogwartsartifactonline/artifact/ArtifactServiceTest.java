package com.edu.hogwartsartifactonline.artifact;

import com.edu.hogwartsartifactonline.artifact.utils.IdWorker;
import com.edu.hogwartsartifactonline.wizard.Wizard;
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
import static org.assertj.core.api.Assertions.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtifactServiceTest {

    @Mock
    ArtifactRepository artifactRepository;

    @Mock
    IdWorker idWorker;

    @InjectMocks
    ArtifactService artifactService;

    List<Artifact> artifacts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        Artifact artifact = new Artifact();
        artifact.setId("1250808601744904192");
        artifact.setName("Invisibility cloak");
        artifact.setDescription("An invisibility cloak is used to make the wearer invisible");
        artifact.setImageUrl("image.url");

        Artifact artifact1 = new Artifact();
        artifact1.setId("1250808601744904191");
        artifact1.setName("Deluminator");
        artifact1.setDescription("A Deluminator is a device invented by Albus Dumbledore that resembles");
        artifact1.setImageUrl("image.url");

        Wizard wizard = new Wizard();
        wizard.setId(2);
        wizard.setName("Harry Potter");

        artifact.setOwner(wizard);
        artifacts.add(artifact);
        artifacts.add(artifact1);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess() {

        // Given: Arrange inputs and targets. Define the behavior of Mock object artifactRepository.
        Artifact artifact = new Artifact();
        artifact.setId("1250808601744904192");
        artifact.setName("Invisibility cloak");
        artifact.setDescription("An invisibility cloak is used to make the wearer invisible");
        artifact.setImageUrl("image.url");

        Wizard wizard = new Wizard();
        wizard.setId(2);
        wizard.setName("Harry Potter");

        artifact.setOwner(wizard);

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(artifact));
        // Defines the behavior of the mock object.

        // When: Act on the target behavior. When steps should cover the method to be tested.
        Artifact returnedArtifact = artifactService.findById("1250808601744904192");

        // Then: Assert expected outcomes.
        assertThat(returnedArtifact.getId()).isEqualTo("1250808601744904192");
        assertThat(returnedArtifact.getName()).isEqualTo(artifact.getName());
        assertThat(returnedArtifact.getDescription()).isEqualTo(artifact.getDescription());
        assertThat(returnedArtifact.getOwner()).isEqualTo(artifact.getOwner());


        Mockito.verify(artifactRepository, times(1)).findById("1250808601744904192");
    }

    @Test
    void findByIdNotFound(){
        // Given:
        given(artifactRepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());

        // When:
        Throwable thrown = catchThrowable(
                ()-> artifactService.findById("1250808601744904192"));

        // Then:
        assertThat(thrown).isInstanceOf(ArtifactNotFoundException.class)
                .hasMessage("Could not find artifact with Id: 1250808601744904192, we are sorry :/");

        Mockito.verify(artifactRepository, times(1)).findById("1250808601744904192");

    }

    @Test
    void testFindAllArtifactsSuccess(){

        // Given: Arrange inputs and targets. Define the behavior of Mock object artifactRepository.
        given(artifactRepository.findAll()).willReturn(artifacts);

        // When: Act on the target behavior. When steps should cover the method to be tested.
        List<Artifact> returnedListOfArtifacts = artifactService.findAll();

        // Then: Assert expected outcomes.
        assertThat(returnedListOfArtifacts.get(0).getId()).isEqualTo("1250808601744904192");
        assertThat(returnedListOfArtifacts.get(0).getName()).isEqualTo("Invisibility cloak");
        assertThat(returnedListOfArtifacts.get(1).getId()).isEqualTo("1250808601744904191");
        assertThat(returnedListOfArtifacts.get(1).getName()).isEqualTo("Deluminator");
    }

    @Test
    void testSaveSuccess(){
        // Given
        Artifact newArtifact = new Artifact();
        newArtifact.setName("Artifact 3");
        newArtifact.setDescription("Description");
        newArtifact.setImageUrl("Image.url");

        given(idWorker.nextId()).willReturn(123456L);
        given(artifactRepository.save(newArtifact)).willReturn(newArtifact);

        // When
        Artifact savedArtifact = artifactService.save(newArtifact);

        // Then
        assertThat(savedArtifact.getId()).isEqualTo("123456");
        assertThat(savedArtifact.getName()).isEqualTo(newArtifact.getName());
        assertThat(savedArtifact.getDescription()).isEqualTo(newArtifact.getDescription());
        assertThat(savedArtifact.getImageUrl()).isEqualTo(newArtifact.getImageUrl());

        verify(artifactRepository, times(1)).save(newArtifact);
    }

    @Test
    void testUpdateSuccess(){
        // Given
        Artifact oldArtifact = new Artifact();
        oldArtifact.setId("1250808601744904192");
        oldArtifact.setName("Invisibility cloak");
        oldArtifact.setDescription("An invisibility cloak is used to make the wearer invisible");
        oldArtifact.setImageUrl("image.url");

        Artifact update = new Artifact();
        update.setId("1250808601744904192");
        update.setName("Invisibility gloves");
        update.setDescription("You go invisible brr");
        update.setImageUrl("image.url");

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(oldArtifact));
        given(artifactRepository.save(oldArtifact)).willReturn(oldArtifact);

        // When
        Artifact updatedArtifact = artifactService.update("1250808601744904192", update);

        // Then
        assertThat(updatedArtifact.getId()).isEqualTo("1250808601744904192");
        assertThat(updatedArtifact.getDescription()).isEqualTo(update.getDescription());

        verify(artifactRepository, times(1)).save(oldArtifact);
        verify(artifactRepository, times(1)).findById("1250808601744904192");
    }

    @Test
    void testUpdateNotFound(){

        // Given
        Artifact update = new Artifact();
        update.setName("Invisibility gloves");
        update.setDescription("You go invisible brr");
        update.setImageUrl("image.url");

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());

        // When
        assertThrows(ArtifactNotFoundException.class,
                ()-> artifactService.update("1250808601744904192", update));

        // Then
        verify(artifactRepository, times(1)).findById("1250808601744904192");

    }

    @Test
    void testDeleteSuccess(){
        // Given
        Artifact artifact = new Artifact();
        artifact.setId("1250808601744904192");
        artifact.setName("Invisibility cloak");
        artifact.setDescription("An invisibility cloak is used to make the wearer invisible");
        artifact.setImageUrl("image.url");

        given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(artifact));
        doNothing().when(artifactRepository).deleteById("1250808601744904192");

        // When
        artifactService.delete("1250808601744904192");

        // Then
        verify(artifactRepository, times(1)).deleteById("1250808601744904192");
    }

    @Test
    void testDeleteNotFound(){
        // Given
        given(artifactRepository.findById("1250808601744904192"))
                .willReturn(Optional.empty());

        // When
        assertThrows(ArtifactNotFoundException.class, () -> {
            artifactService.delete("1250808601744904192");
        });

        // Then

        verify(artifactRepository, times(1)).findById("1250808601744904192");
    }
}