package com.edu.hogwartsartifactonline.wizard;

import com.edu.hogwartsartifactonline.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class WizardService {

    WizardRepository wizardRepository;

    public WizardService(WizardRepository wizardRepository) {
        this.wizardRepository = wizardRepository;
    }

    public List<Wizard> findAll(){
        return wizardRepository.findAll();
    }

    public Wizard save(Wizard newWizard){
        return wizardRepository.save(newWizard);
    }

    public Wizard findById(int wizardId){
        return wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("Wizard", wizardId));
    }

    public Wizard update(int wizardId, Wizard update){
        return wizardRepository.findById(wizardId)
                .map(oldWizard -> {
                    oldWizard.setName(update.getName());
                    return wizardRepository.save(oldWizard);
                }).orElseThrow(()-> new ObjectNotFoundException("Wizard", wizardId));
    }

    public void delete(int wizardId){
        Wizard wizard = wizardRepository.findById(wizardId).orElseThrow(
                () -> new ObjectNotFoundException("Wizard", wizardId)
        );
        wizard.removeAllArtifacts();

        wizardRepository.deleteById(wizardId);
    }

}
