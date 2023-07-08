package com.edu.hogwartsartifactonline.artifact;

import com.edu.hogwartsartifactonline.artifact.dto.ArtifactDTO;
import com.edu.hogwartsartifactonline.artifact.utils.IdWorker;
import com.edu.hogwartsartifactonline.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ArtifactService {

    private final ArtifactRepository artifactRepository;
    private final IdWorker idWorker;

    public ArtifactService(ArtifactRepository artifactRepository, IdWorker idWorker) {
        this.artifactRepository = artifactRepository;
        this.idWorker = idWorker;
    }

    public Artifact findById(String artifactId){
        return artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("Artifact", artifactId));
    }

    public List<Artifact> findAll() {
        return artifactRepository.findAll();
    }

    public Artifact save(Artifact newArtifact){
        newArtifact.setId(String.valueOf(idWorker.nextId()));
        return artifactRepository.save(newArtifact);
    }

    public Artifact update(String artifactId, Artifact update){
        return artifactRepository.findById(artifactId)
                .map(oldArtifact ->{
                    oldArtifact.setName(update.getName());
                    oldArtifact.setDescription(update.getDescription());
                    oldArtifact.setImageUrl(update.getImageUrl());
                    return artifactRepository.save(oldArtifact);
                })
                .orElseThrow(() -> new ObjectNotFoundException("Artifact", artifactId));
    }

    public void delete(String artifactId){
        artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("Artifact", artifactId));
        artifactRepository.deleteById(artifactId);
    }

}
