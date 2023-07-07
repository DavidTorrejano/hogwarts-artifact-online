package com.edu.hogwartsartifactonline.artifact;

import com.edu.hogwartsartifactonline.artifact.converter.ArtifactDTOToArtifactConverter;
import com.edu.hogwartsartifactonline.artifact.converter.ArtifactToArtifactDTOConverter;
import com.edu.hogwartsartifactonline.artifact.dto.ArtifactDTO;
import com.edu.hogwartsartifactonline.system.Result;
import com.edu.hogwartsartifactonline.system.StatusCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/artifacts")
public class ArtifactController {

    private final ArtifactService artifactService;
    private final ArtifactToArtifactDTOConverter artifactToArtifactDTOConverter;
    private final ArtifactDTOToArtifactConverter artifactDTOToArtifactConverter;

    public ArtifactController(ArtifactService artifactService,
                              ArtifactToArtifactDTOConverter artifactToArtifactDTOConverter,
                              ArtifactDTOToArtifactConverter artifactDTOToArtifactConverter) {
        this.artifactService = artifactService;
        this.artifactToArtifactDTOConverter = artifactToArtifactDTOConverter;
        this.artifactDTOToArtifactConverter = artifactDTOToArtifactConverter;
    }

    @GetMapping("/{artifactId}")
    public Result findArtifactById(@PathVariable String artifactId){
        Artifact foundArtifact = artifactService.findById(artifactId);
        ArtifactDTO artifactDTO = artifactToArtifactDTOConverter.convert(foundArtifact);
        return new Result(true, StatusCode.SUCCESS, "Find One Success", artifactDTO);
    }

    @GetMapping
    public Result findAllArtifacts(){
        List<Artifact> foundArtifacts = artifactService.findAll();
        List<ArtifactDTO> foundArtifactsDTO = foundArtifacts.stream()
                .map(artifactToArtifactDTOConverter::convert)
                .toList();
        return new Result(true, StatusCode.SUCCESS, "Find All Success", foundArtifactsDTO);
    }

    @PostMapping
    public Result createArtifact(@Valid @RequestBody ArtifactDTO artifactDTO){
        // We need to convert the artifactDTO to Artifact
        Artifact artifactToBeSaved = artifactDTOToArtifactConverter.convert(artifactDTO);
        Artifact savedArtifact = artifactService.save(artifactToBeSaved);
        ArtifactDTO artifactDtoToBeReturned = artifactToArtifactDTOConverter.convert(savedArtifact);

        return new Result(true, StatusCode.SUCCESS, "Add Success", artifactDtoToBeReturned);
    }

    @PutMapping("/{artifactId}")
    public Result updateArtifact(@PathVariable String artifactId, @Valid @RequestBody ArtifactDTO artifactDTO){
        // Convertimos de ArtifactDTO to Artifact
        Artifact artifactToUpdate = artifactDTOToArtifactConverter.convert(artifactDTO);
        ArtifactDTO artifactToBeReturned = artifactToArtifactDTOConverter
                .convert(artifactService.update(artifactId, artifactToUpdate));
        return new Result(true, StatusCode.SUCCESS, "Update Success", artifactToBeReturned);
    }

    @DeleteMapping("/{artifactId}")
    public Result deleteArtifact(@PathVariable String artifactId){
        artifactService.delete(artifactId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success");
    }

}
