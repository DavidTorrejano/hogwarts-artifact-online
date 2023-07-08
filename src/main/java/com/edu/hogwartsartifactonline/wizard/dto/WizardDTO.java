package com.edu.hogwartsartifactonline.wizard.dto;

import jakarta.validation.constraints.NotEmpty;

public record WizardDTO(Integer id,
                        @NotEmpty(message = "Can't save a wizard without name.")
                        String name,
                        Integer numberOfArtifacts) {
}
