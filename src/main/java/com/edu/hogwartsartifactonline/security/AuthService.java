package com.edu.hogwartsartifactonline.security;

import com.edu.hogwartsartifactonline.hogwartsuser.HogwartsUser;
import com.edu.hogwartsartifactonline.hogwartsuser.MyUserPrincipal;
import com.edu.hogwartsartifactonline.hogwartsuser.converter.UserToUserDTOConverter;
import com.edu.hogwartsartifactonline.hogwartsuser.dto.UserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final JwtProvider jwtProvider;
    private final UserToUserDTOConverter userToUserDTOConverter;

    public AuthService(JwtProvider jwtProvider, UserToUserDTOConverter userToUserDTOConverter) {
        this.jwtProvider = jwtProvider;
        this.userToUserDTOConverter = userToUserDTOConverter;
    }

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        // Create user info
        MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
        HogwartsUser hogwartsUser = principal.getHogwartsUser();
        UserDTO userDTO = userToUserDTOConverter.convert(hogwartsUser);

        // Create the JWT.
        String token = jwtProvider.createToken(authentication);

        Map<String, Object> loginResultMap = new HashMap<>();
        loginResultMap.put("userInfo", userDTO);
        loginResultMap.put("token", token);

        return loginResultMap;
    }
}
