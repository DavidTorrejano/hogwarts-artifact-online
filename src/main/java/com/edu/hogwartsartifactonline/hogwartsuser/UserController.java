package com.edu.hogwartsartifactonline.hogwartsuser;

import com.edu.hogwartsartifactonline.hogwartsuser.converter.UserDTOToUserConverter;
import com.edu.hogwartsartifactonline.hogwartsuser.converter.UserToUserDTOConverter;
import com.edu.hogwartsartifactonline.hogwartsuser.dto.UserDTO;
import com.edu.hogwartsartifactonline.system.Result;
import com.edu.hogwartsartifactonline.system.StatusCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.endpoint.base-url}/users")
public class UserController {

    UserService userService;
    UserToUserDTOConverter userToUserDTOConverter;
    UserDTOToUserConverter userDTOToUserConverter;

    public UserController(UserService userService,
                          UserToUserDTOConverter userToUserDTOConverter,
                          UserDTOToUserConverter userDTOToUserConverter) {
        this.userService = userService;
        this.userToUserDTOConverter = userToUserDTOConverter;
        this.userDTOToUserConverter = userDTOToUserConverter;
    }

    @GetMapping
    public Result findAllUsers(){
        List<UserDTO> userDTOS = userService.findAll().stream()
                .map(userToUserDTOConverter::convert)
                .toList();

        return new Result(true, StatusCode.SUCCESS, "Find All Success", userDTOS);
    }

    @PostMapping
    public Result createNewUser(@Valid @RequestBody HogwartsUser hogwartsUser){
        HogwartsUser savedUser = userService.save(hogwartsUser);
        UserDTO userToBeReturned = userToUserDTOConverter.convert(savedUser);
        return new Result(true, StatusCode.SUCCESS, "Save User Success", userToBeReturned);
    }

    @GetMapping("/{idHogwartsUser}")
    public Result findUserById(@PathVariable Integer idHogwartsUser){
        HogwartsUser foundUser = userService.findById(idHogwartsUser);
        UserDTO userToBeReturned = userToUserDTOConverter.convert(foundUser);
        return new Result(true, StatusCode.SUCCESS, "Find One Success", userToBeReturned);
    }

    @PutMapping("/{idHogwartsUser}")
    public Result updateUser(@Valid @RequestBody UserDTO userDTO,
                             @PathVariable Integer idHogwartsUser){
        HogwartsUser update = userDTOToUserConverter.convert(userDTO);
        HogwartsUser updatedUser = userService.update(idHogwartsUser, update);
        UserDTO userToBeReturned = userToUserDTOConverter.convert(updatedUser);

        return new Result(true, StatusCode.SUCCESS, "Update User Success", userToBeReturned);
    }

    @DeleteMapping("/{userId}")
    public Result deleteUserById(@PathVariable Integer userId){
        userService.deleteById(userId);

        return new Result(true, StatusCode.SUCCESS, "User Delete Success");
    }

}
