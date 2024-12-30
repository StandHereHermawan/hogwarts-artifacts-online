package edu.tcu.cs.hogwarts_artifact_online.artifact;

import edu.tcu.cs.hogwarts_artifact_online.artifact.converter.ArtifactDtoToArtifactConverter;
import edu.tcu.cs.hogwarts_artifact_online.artifact.converter.ArtifactToArtifactDtoConverter;
import edu.tcu.cs.hogwarts_artifact_online.artifact.data_transfer_object.ArtifactDTO;
import edu.tcu.cs.hogwarts_artifact_online.system.Result;
import edu.tcu.cs.hogwarts_artifact_online.system.StatusCode;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ArtifactController {

    private final ArtifactService artifactService;

    private final ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;

    private final ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter;

    public ArtifactController(ArtifactService artifactService,
                              ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter,
                              ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter) {
        this.artifactService = artifactService;
        this.artifactToArtifactDtoConverter = artifactToArtifactDtoConverter;
        this.artifactDtoToArtifactConverter = artifactDtoToArtifactConverter;
    }

    @GetMapping("/api/v1/artifacts/{artifactId}")
    public Result findArtifactById(@PathVariable String artifactId) {
        return new Result(
                true,
                StatusCode.SUCCESS,
                "Find One Success",
                this.artifactToArtifactDtoConverter
                        .convert(this.artifactService
                                .findById(artifactId)));
    }

    @GetMapping("/api/v1/artifacts")
    public Result findAllArtifacts() {
        List<Artifact> listOfArtifactsFromServiceLayer = this.artifactService.findAll();
        /// Convert foundArtifactsFromServiceLayer to a list of artifactDtos
        List<ArtifactDTO> listOfArtifactDtos = listOfArtifactsFromServiceLayer
                .stream()
                .map(oneArtifactFromServiceLayer -> this.artifactToArtifactDtoConverter
                        .convert(oneArtifactFromServiceLayer))
                .collect(Collectors.toList()); /// Convert From
        return new Result(true, StatusCode.SUCCESS, "Find All Artifacts Success", listOfArtifactDtos);
    }

    @PostMapping("/api/v1/artifacts")
    public Result addArtifact(@RequestBody
                              @Valid
                              ArtifactDTO artifactDTO) {
        /// Convert ArtifactDTO to Artifact first.
        Artifact newArtifact = this.artifactDtoToArtifactConverter.convert(artifactDTO);
        Artifact savedArtifact = this.artifactService.save(newArtifact);
        ArtifactDTO savedArtifactDTO = this.artifactToArtifactDtoConverter.convert(savedArtifact);
        return new Result(true, StatusCode.SUCCESS, "Add Artifact Success", savedArtifactDTO);
    }

    @PutMapping("/api/v1/artifacts/{artifactId}")
    public Result updateArtifact(@PathVariable
                                 String artifactId,
                                 @Valid
                                 @RequestBody
                                 ArtifactDTO artifactDTO) {
        Artifact updatingArtifactData = this.artifactDtoToArtifactConverter.convert(artifactDTO);
        Artifact updatedArtifact = this.artifactService.update(artifactId, updatingArtifactData);
        ArtifactDTO updatedArtifactDto = this.artifactToArtifactDtoConverter.convert(updatedArtifact);
        return new Result(true, StatusCode.SUCCESS, "Update Artifact Success", updatedArtifactDto);
    }
}
