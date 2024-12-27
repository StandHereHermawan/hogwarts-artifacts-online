package edu.tcu.cs.hogwarts_artifact_online.artifact;

import edu.tcu.cs.hogwarts_artifact_online.artifact.converter.ArtifactToArtifactDtoConverter;
import edu.tcu.cs.hogwarts_artifact_online.artifact.data_transfer_object.ArtifactDTO;
import edu.tcu.cs.hogwarts_artifact_online.system.Result;
import edu.tcu.cs.hogwarts_artifact_online.system.StatusCode;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ArtifactController {

    private final ArtifactService artifactService;

    private final ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;

    public ArtifactController(ArtifactService artifactService,
                              ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter) {
        this.artifactService = artifactService;
        this.artifactToArtifactDtoConverter = artifactToArtifactDtoConverter;
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
}
