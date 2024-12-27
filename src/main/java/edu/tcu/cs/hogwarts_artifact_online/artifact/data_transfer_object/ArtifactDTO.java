package edu.tcu.cs.hogwarts_artifact_online.artifact.data_transfer_object;

import edu.tcu.cs.hogwarts_artifact_online.wizard.data_transfer_object.WizardDTO;

public record ArtifactDTO(String id,
                          String name,
                          String description,
                          String imageUrl,
                          WizardDTO owner) {
}
