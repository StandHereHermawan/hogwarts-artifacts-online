package edu.tcu.cs.hogwarts_artifact_online.wizard.data_transfer_object;

import jakarta.validation.constraints.NotEmpty;

public record UpdateWizardDto(@NotEmpty(message = "name is required")
                              String name) {
}
