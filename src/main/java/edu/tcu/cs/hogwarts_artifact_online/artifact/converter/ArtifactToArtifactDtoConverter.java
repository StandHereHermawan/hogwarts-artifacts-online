package edu.tcu.cs.hogwarts_artifact_online.artifact.converter;

import edu.tcu.cs.hogwarts_artifact_online.artifact.Artifact;
import edu.tcu.cs.hogwarts_artifact_online.artifact.data_transfer_object.ArtifactDTO;
import edu.tcu.cs.hogwarts_artifact_online.wizard.converter.WizardToWizardDtoConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArtifactToArtifactDtoConverter implements Converter<Artifact, ArtifactDTO> {

    private final WizardToWizardDtoConverter wizardDtoConverter;

    public ArtifactToArtifactDtoConverter(WizardToWizardDtoConverter wizardDtoConverter) {
        this.wizardDtoConverter = wizardDtoConverter;
    }

    @Override
    public ArtifactDTO convert(Artifact source) {
        return new ArtifactDTO(source.getId(),
                source.getName(),
                source.getImageUrl(),
                source.getDescription(),
                source.getOwner() != null
                        ? this.wizardDtoConverter.convert(source.getOwner())
                        : null);
    }
}
