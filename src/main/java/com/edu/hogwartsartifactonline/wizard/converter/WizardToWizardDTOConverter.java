package com.edu.hogwartsartifactonline.wizard.converter;

import com.edu.hogwartsartifactonline.wizard.Wizard;
import com.edu.hogwartsartifactonline.wizard.dto.WizardDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WizardToWizardDTOConverter implements Converter<Wizard, WizardDTO> {

    @Override
    public WizardDTO convert(Wizard source) {
        WizardDTO wizardDTO = new WizardDTO(source.getId(), source.getName(), source.numberOfArtifacts());
        return wizardDTO;
    }
}
