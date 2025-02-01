package edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.data_transfer_object;

import jakarta.validation.constraints.NotEmpty;

public record HogwartsUserDTO(Integer id,
                              @NotEmpty(message = "username is required.")
                              String username,

                              boolean enabled,

                              @NotEmpty(message = "role is required.")
                              String roles
) {
}
