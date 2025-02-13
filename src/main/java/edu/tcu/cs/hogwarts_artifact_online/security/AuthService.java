package edu.tcu.cs.hogwarts_artifact_online.security;

import edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.HogwartsUser;
import edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.MyUserPrincipal;
import edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.converter.UserToUserDtoConverter;
import edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.data_transfer_object.HogwartsUserDTO;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final JwtProvider jwtProvider;

    private final UserToUserDtoConverter userToUserDtoConverter;

    public AuthService(JwtProvider jwtProvider, UserToUserDtoConverter userToUserDtoConverter) {
        this.userToUserDtoConverter = userToUserDtoConverter;
        this.jwtProvider = jwtProvider;
    }

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        /// Create user info.
        MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
        HogwartsUser hogwartsUser = principal.getHogwartsUser();
        HogwartsUserDTO hogwartsUserDTO = this.userToUserDtoConverter.convert(hogwartsUser);

        /// Create a JSON Web Token.
        String token = this.jwtProvider.createToken(authentication);

        Map<String, Object> loginResultMap = new HashMap<>();

        loginResultMap.put("userInfo", hogwartsUserDTO);
        loginResultMap.put("token", token);

        return loginResultMap;
    }
}
