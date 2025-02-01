package edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.converter;

import edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.HogwartsUser;
import edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.data_transfer_object.HogwartsUserDTO;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDtoConverter implements Converter<HogwartsUser, HogwartsUserDTO> {

    @Override
    public HogwartsUserDTO convert(HogwartsUser source) {
        final HogwartsUserDTO hogwartsUserDTO;
        {
            hogwartsUserDTO = new HogwartsUserDTO(
                    source.getId(),
                    source.getUsername(),
                    source.isEnabled(),
                    source.getRoles()
            );
        }
        return hogwartsUserDTO;
    }
}
