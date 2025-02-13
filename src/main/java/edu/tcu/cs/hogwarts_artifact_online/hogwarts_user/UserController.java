package edu.tcu.cs.hogwarts_artifact_online.hogwarts_user;

import edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.converter.UserDtoToUserConverter;
import edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.converter.UserToUserDtoConverter;
import edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.data_transfer_object.HogwartsUserDTO;
import edu.tcu.cs.hogwarts_artifact_online.system.Result;
import edu.tcu.cs.hogwarts_artifact_online.system.StatusCode;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.endpoint.base-url}/users")
public class UserController {

    private final UserService userService;

    private final UserDtoToUserConverter userDtoToUserConverter;

    private final UserToUserDtoConverter userToUserDtoConverter;

    public UserController(UserService userService,
                          UserDtoToUserConverter userDtoToUserConverter,
                          UserToUserDtoConverter userToUserDtoConverter) {
        this.userService = userService;
        this.userDtoToUserConverter = userDtoToUserConverter;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    @GetMapping
    public Result findAllUsers() {
        List<HogwartsUser> foundHogwartsUsers = this.userService.findAllUsers();

        /// Menggunakan Java's Lambda.
        /// List<HogwartsUserDTO> hogwartsUserDto = foundHogwartsUsers
        ///         .stream()
        ///         .map(userDto -> this.userToUserDtoConverter.convert(userDto))
        ///         .toList();

        /// Menggunakan Java's method reference
        List<HogwartsUserDTO> hogwartsUserDto = foundHogwartsUsers
                .stream()
                .map(this.userToUserDtoConverter::convert)
                .toList();

        return new Result(
                true,
                StatusCode.SUCCESS,
                "Find All Hogwarts User Success.",
                hogwartsUserDto);
    }

    @GetMapping("/{userId}")
    public Result findUserById(@PathVariable
                               Integer userId) {
        HogwartsUser foundHogwartsUserById = this.userService.findHogwartsUserById(userId);
        HogwartsUserDTO hogwartsUserDTO;
        hogwartsUserDTO = this.userToUserDtoConverter
                .convert(foundHogwartsUserById);
        return new Result(
                true,
                StatusCode.SUCCESS,
                "Find One Hogwarts User Success.",
                hogwartsUserDTO);
    }

    /**
     * We are not using UserDto object, but HogwartsUser object.
     * since we require password.
     *
     * @param newHogwartsUser
     * @return
     */
    @PostMapping
    public Result addUser(@Valid
                          @RequestBody
                          HogwartsUser newHogwartsUser) {
        HogwartsUser hogwartsUser = this.userService.save(newHogwartsUser);
        HogwartsUserDTO hogwartsUserDTO;
        {
            hogwartsUserDTO = this.userToUserDtoConverter.convert(hogwartsUser);
        }
        return new Result(
                true,
                StatusCode.SUCCESS,
                "Add Hogwarts User Success.",
                hogwartsUserDTO);
    }

    @PutMapping("/{userId}")
    public Result updateUser(@PathVariable
                             Integer userId,
                             @Valid
                             @RequestBody
                             HogwartsUserDTO updatedUserData) {
        HogwartsUser newerHogwartsUserData;
        {
            newerHogwartsUserData = this.userDtoToUserConverter.convert(updatedUserData);
        }
        HogwartsUser updatedHogwartsUser;
        {
            updatedHogwartsUser = this.userService.update(userId, newerHogwartsUserData);
        }
        HogwartsUserDTO updatedHogwartsUserDto;
        {
            updatedHogwartsUserDto = this.userToUserDtoConverter.convert(updatedHogwartsUser);
        }
        return new Result(
                true,
                StatusCode.SUCCESS,
                "Update Hogwarts User Success.",
                updatedHogwartsUserDto);
    }

    @DeleteMapping("/{userId}")
    public Result deleteUser(@PathVariable
                             Integer userId) {
        HogwartsUser deletedHogwartsUser = this.userService.delete(userId);
        HogwartsUserDTO hogwartsUserDTO = this.userToUserDtoConverter.convert(deletedHogwartsUser);
        return new Result(
                true,
                StatusCode.SUCCESS,
                "Delete Hogwarts User Success.",
                hogwartsUserDTO);
    }
}
