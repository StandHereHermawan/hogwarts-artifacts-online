package edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.converter;

import edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.HogwartsUser;
import edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.data_transfer_object.HogwartsUserDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoToUserConverter implements Converter<HogwartsUserDTO, HogwartsUser> {

    @Override
    public HogwartsUser convert(HogwartsUserDTO source) {
        HogwartsUser hogwartsUser;
        {
            hogwartsUser = new HogwartsUser();
            hogwartsUser.setUsername(source.username());
            hogwartsUser.setEnabled(source.enabled());
            hogwartsUser.setRoles(source.roles());
        }
        return hogwartsUser;
    }
}
