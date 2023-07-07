package com.edu.hogwartsartifactonline.artifact.dto;

import com.edu.hogwartsartifactonline.wizard.dto.WizardDTO;
import jakarta.validation.constraints.NotEmpty;

public record ArtifactDTO(String id,
                          @NotEmpty(message = "Name is mandatory.")
                          String name,
                          @NotEmpty(message = "Without description, it might be anything.")
                          String description,
                          @NotEmpty(message = "The image url is required.")
                          String imageUrl,
                          WizardDTO owner) {
}
