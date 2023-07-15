package com.edu.hogwartsartifactonline.hogwartsuser.dto;

import jakarta.validation.constraints.NotEmpty;

public record UserDTO(Integer id,

                      @NotEmpty(message = "username is required")
                      String username,

                      boolean enabled,

                      @NotEmpty(message = "roles are required")
                      String roles) {
}
