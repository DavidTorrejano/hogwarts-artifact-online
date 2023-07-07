package com.edu.hogwartsartifactonline.artifact.converter;

import com.edu.hogwartsartifactonline.artifact.Artifact;
import com.edu.hogwartsartifactonline.artifact.dto.ArtifactDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArtifactDTOToArtifactConverter implements Converter<ArtifactDTO, Artifact> {


    @Override
    public Artifact convert(ArtifactDTO source) {
        Artifact artifact = new Artifact();
        artifact.setId(source.id());
        artifact.setName(source.name());
        artifact.setDescription(source.description());
        artifact.setImageUrl(source.imageUrl());
        return artifact;
    }
}
