package com.edu.hogwartsartifactonline.wizard.converter;

import com.edu.hogwartsartifactonline.wizard.Wizard;
import com.edu.hogwartsartifactonline.wizard.dto.WizardDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WizardDTOToWizardConverter implements Converter<WizardDTO, Wizard> {
    @Override
    public Wizard convert(WizardDTO source) {
        Wizard wizard = new Wizard();
        wizard.setId(source.id());
        wizard.setName(source.name());
         return wizard;
    }
}
