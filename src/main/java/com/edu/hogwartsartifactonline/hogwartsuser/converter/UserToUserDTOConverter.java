package com.edu.hogwartsartifactonline.hogwartsuser.converter;

import com.edu.hogwartsartifactonline.hogwartsuser.HogwartsUser;
import com.edu.hogwartsartifactonline.hogwartsuser.dto.UserDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDTOConverter implements Converter<HogwartsUser, UserDTO> {
    @Override
    public UserDTO convert(HogwartsUser source) {
        return new UserDTO(source.getId(),
                source.getUsername(),
                source.isEnabled(),
                source.getRoles());
    }
}
