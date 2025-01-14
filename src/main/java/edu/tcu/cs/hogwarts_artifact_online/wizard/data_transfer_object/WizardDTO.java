package edu.tcu.cs.hogwarts_artifact_online.wizard.data_transfer_object;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record WizardDTO(Integer id,
                        @NotEmpty(message = "name is required.")
                        @NotBlank(message = "name can not blank.")
                        String name,
                        Integer numberOfArtifacts) {
}
