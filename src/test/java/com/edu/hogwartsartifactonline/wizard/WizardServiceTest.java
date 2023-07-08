package com.edu.hogwartsartifactonline.wizard;

import com.edu.hogwartsartifactonline.artifact.Artifact;
import com.edu.hogwartsartifactonline.system.exception.ObjectNotFoundException;
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

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WizardServiceTest {

    @Mock
    WizardRepository wizardRepository;

    @InjectMocks
    WizardService wizardService;

    List<Wizard> wizards = new ArrayList<>();
    List<Artifact> artifacts = new ArrayList<>();

    @BeforeEach
    void setUp() {
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
    void testFindAllWizardsSuccess() {
        // Given: Arrange inputs and targets. Define the behavior of Mock object wizardRepository.
        given(wizardRepository.findAll()).willReturn(wizards);

        // When: Act on the target behavior. When steps should cover the method to be tested.
        List<Wizard> returnedListOfWizards = wizardService.findAll();

        // Then: Assert expected outcomes
        assertThat(returnedListOfWizards.get(0).getId()).isEqualTo(1);
        assertThat(returnedListOfWizards.get(0).getName()).isEqualTo("Albus Dumbledore");
        assertThat(returnedListOfWizards.get(0).getArtifacts().get(0).getName())
                .isEqualTo("Invisibility cloak");
        assertThat(returnedListOfWizards.get(1).getId()).isEqualTo(2);
        assertThat(returnedListOfWizards.get(1).getName()).isEqualTo("Harry Potter");
    }

    @Test
    void testSaveNewWizardSuccess(){
        // Given
        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Harry Potter");

        given(wizardRepository.save(w2)).willReturn(w2);

        // When
        Wizard savedWizard = wizardService.save(w2);

        // Then
        assertThat(savedWizard.getId()).isEqualTo(2);
        assertThat(savedWizard.getName()).isEqualTo("Harry Potter");
        assertThat(savedWizard.numberOfArtifacts()).isEqualTo(0);
    }

    @Test
    void testFindWizardByIdSuccess(){
        // Given
        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Harry Potter");

        given(wizardRepository.findById(2)).willReturn(Optional.of(w2));

        // When
        Wizard foundWizard = wizardService.findById(2);

        // Then
        assertThat(foundWizard.getName()).isEqualTo("Harry Potter");
        assertThat(foundWizard.getId()).isEqualTo(2);

    }

    @Test
    void testFindWizardByIdNotSuccess(){
        // Given
        given(wizardRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(
                () -> wizardService.findById(2));

        // Then
        assertThat(thrown).isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find Wizard with Id: 2, we are sorry :/");

        Mockito.verify(wizardRepository, times(1)).findById(2);
    }

    @Test
    void testUpdateWizardSuccess(){
        // Given
        Wizard oldWizard = new Wizard();
        oldWizard.setId(1);
        oldWizard.setName("Albus Dumbledore");

        Wizard update = new Wizard();
        update.setId(1);
        update.setName("Lord Voldemort");

        given(wizardRepository.findById(1)).willReturn(Optional.of(oldWizard));
        given(wizardRepository.save(oldWizard)).willReturn(oldWizard);

        // When
        Wizard updatedWizard = wizardService.update(1, update);

        // Then
        assertThat(updatedWizard.getId()).isEqualTo(1);
        assertThat(updatedWizard.getName()).isEqualTo("Lord Voldemort");

        verify(wizardRepository, times(1)).findById(1);
        verify(wizardRepository, times(1)).save(oldWizard);
    }

    @Test
    void testUpdateWizardNotFound(){
        // Given
        Wizard update = new Wizard();
        update.setId(1);
        update.setName("Albus Dumbledore");

        given(wizardRepository.findById(1)).willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class,
                () -> wizardService.update(1, update));

        // Then
        verify(wizardRepository, times(1)).findById(1);

    }

    @Test
    void testDeleteWizardUsingIdSuccess(){
        // Given
        Wizard wizard = new Wizard();
        wizard.setId(1);
        wizard.setName("Albus Dumbledore");

        given(wizardRepository.findById(1)).willReturn(Optional.of(wizard));
        doNothing().when(wizardRepository).deleteById(1);

        // When
        wizardService.delete(1);

        // Then
        verify(wizardRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteWizardUsingIdNotFound(){
        // Given
        given(wizardRepository.findById(1)).willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () ->{
            wizardService.delete(1);
        });

        // Then
        verify(wizardRepository, times(1)).findById(1);
    }


}