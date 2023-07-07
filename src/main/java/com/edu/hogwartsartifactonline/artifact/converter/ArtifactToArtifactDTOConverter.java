package com.edu.hogwartsartifactonline.artifact.converter;

import com.edu.hogwartsartifactonline.artifact.Artifact;
import com.edu.hogwartsartifactonline.artifact.dto.ArtifactDTO;
import com.edu.hogwartsartifactonline.wizard.Wizard;
import com.edu.hogwartsartifactonline.wizard.converter.WizardToWizardDTOConverter;
import com.edu.hogwartsartifactonline.wizard.dto.WizardDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArtifactToArtifactDTOConverter implements Converter<Artifact, ArtifactDTO> {

    private final WizardToWizardDTOConverter wizardToWizardDTOConverter;

    public ArtifactToArtifactDTOConverter(WizardToWizardDTOConverter wizardToWizardDTOConverter) {
        this.wizardToWizardDTOConverter = wizardToWizardDTOConverter;
    }

    @Override
    public ArtifactDTO convert(Artifact source) {
        ArtifactDTO artifactDTO = new ArtifactDTO(source.getId(), source.getName(), source.getDescription(),
                source.getImageUrl(),
                source.getOwner() != null? wizardToWizardDTOConverter.convert(source.getOwner()):null);
        return artifactDTO;
    }
}
