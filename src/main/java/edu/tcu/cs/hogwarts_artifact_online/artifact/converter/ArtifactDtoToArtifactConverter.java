package edu.tcu.cs.hogwarts_artifact_online.artifact.converter;

import edu.tcu.cs.hogwarts_artifact_online.artifact.Artifact;
import edu.tcu.cs.hogwarts_artifact_online.artifact.data_transfer_object.ArtifactDTO;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArtifactDtoToArtifactConverter implements Converter<ArtifactDTO, Artifact> {

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
