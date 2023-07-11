package com.edu.hogwartsartifactonline.wizard;

import com.edu.hogwartsartifactonline.artifact.Artifact;
import com.edu.hogwartsartifactonline.artifact.ArtifactRepository;
import com.edu.hogwartsartifactonline.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class WizardService {

    private final ArtifactRepository artifactRepository;
    private final WizardRepository wizardRepository;

    public WizardService(ArtifactRepository artifactRepository,
                         WizardRepository wizardRepository) {
        this.artifactRepository = artifactRepository;
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

    public void assignArtifact(Integer wizardId, String artifactId){
        // Find this artifact and wizard by id from db
        Artifact artifactToBeAssign = artifactRepository.findById(artifactId).orElseThrow(
                () -> new ObjectNotFoundException("Artifact", artifactId)
        );
        Wizard wizard = wizardRepository.findById(wizardId).orElseThrow(
                () -> new ObjectNotFoundException("Wizard", wizardId)
        );

        // We need to verify if the artifact has an owner
        if (artifactToBeAssign.getOwner() != null){
            artifactToBeAssign.getOwner().removeArtifact(artifactToBeAssign);
        }
        wizard.addArtifact(artifactToBeAssign);
    }

}
