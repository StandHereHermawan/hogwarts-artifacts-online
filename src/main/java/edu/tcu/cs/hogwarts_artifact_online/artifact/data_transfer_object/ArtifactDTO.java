package edu.tcu.cs.hogwarts_artifact_online.artifact.data_transfer_object;

import edu.tcu.cs.hogwarts_artifact_online.wizard.data_transfer_object.WizardDTO;
import jakarta.validation.constraints.NotEmpty;

public record ArtifactDTO(String id,
                          @NotEmpty(message = "name is required.") String name,
                          @NotEmpty(message = "description is required.") String description,
                          @NotEmpty(message = "imageUrl is required.") String imageUrl,
                          WizardDTO owner) {
}
