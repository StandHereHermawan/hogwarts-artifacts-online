package edu.tcu.cs.hogwarts_artifact_online.hogwarts_user;

import edu.tcu.cs.hogwarts_artifact_online.system.exception.ObjectNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    List<HogwartsUser> hogwartsUserList;

    @BeforeEach
    void setUp() {
        int index;
        {
            index = 0;
        }

        HogwartsUser hogwartsUser1 = new HogwartsUser();
        {
            index++;
            hogwartsUser1.setId(index);
            hogwartsUser1.setUsername("Agus");
            hogwartsUser1.setPassword("123456");
            hogwartsUser1.setEnabled(true);
            hogwartsUser1.setRoles("admin user");
        }

        HogwartsUser hogwartsUser2 = new HogwartsUser();
        {
            index++;
            hogwartsUser2.setId(index);
            hogwartsUser2.setUsername("Agus");
            hogwartsUser2.setPassword("654321");
            hogwartsUser2.setEnabled(true);
            hogwartsUser2.setRoles("user");
        }

        HogwartsUser hogwartsUser3 = new HogwartsUser();
        {
            index++;
            hogwartsUser3.setId(index);
            hogwartsUser3.setUsername("Tono");
            hogwartsUser3.setPassword("qwerty");
            hogwartsUser3.setEnabled(false);
            hogwartsUser3.setRoles("user");
        }

        this.hogwartsUserList = new ArrayList<>();
        {
            this.hogwartsUserList.add(hogwartsUser1);
            this.hogwartsUserList.add(hogwartsUser2);
            this.hogwartsUserList.add(hogwartsUser3);
        }
    }

    @Test
    void testUserFindAllSuccess() {
        /// Given Section. define the behavior of the findAll() methods in userRepository.
        given(this.userRepository.findAll()).willReturn(this.hogwartsUserList);
        /// End of Given Section.

        /// When Section. Act on the target behavior. Act steps should cover the method.
        List<HogwartsUser> actualUsers = this.userService.findAllUsers();
        /// End of When Section.

        /// Then Section. Assert expected outcomes and Verify.
        assertThat(actualUsers.size()).isEqualTo(this.hogwartsUserList.size());
        ///
        /// Verify findAll method is called exactly once.
        verify(this.userRepository, times(1)).findAll();
        /// End of Then Section.
    }

    @Test
    void testUserFindByIdSuccessScenario() {
        /// Given Section. Define input and mock behavior.
        HogwartsUser user;
        {
            user = new HogwartsUser();
            user.setId(1);
            user.setUsername("Agus");
            user.setPassword("123456");
            user.setEnabled(true);
            user.setRoles("admin user");
        }
        ///
        given(this.userRepository.findById(user.getId()))
                .willReturn(Optional.of(user));
        /// End of Given Section.

        /// When Section. Act on the target behavior.
        HogwartsUser returnedUser = this.userService.findHogwartsUserById(user.getId());
        /// End of When Section.

        /// Then Section. Assert expected output and verify called methods.
        assertThat(returnedUser.getId()).isEqualTo(user.getId());
        assertThat(returnedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(returnedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(returnedUser.isEnabled()).isEqualTo(user.isEnabled());
        assertThat(returnedUser.getRoles()).isEqualTo(user.getRoles());
        /// Verify methods gettin called once.
        verify(this.userRepository, times(1)).findById(user.getId());
        /// End of Then Section.
    }

    @Test
    void testUserFindByIdNotFoundScenario() {
        /// Given Section. Define input and mock behavior.
        HogwartsUser user;
        {
            user = new HogwartsUser();
            user.setId(1);
            user.setUsername("Agus");
            user.setPassword("123456");
            user.setEnabled(true);
            user.setRoles("admin user");
        }
        ///
        given(this.userRepository.findById(user.getId()))
                .willReturn(Optional.empty());
        /// End of Given Section.

        /// When Section. Act on the target behavior.
        Throwable thrown = catchThrowable(() -> {
            this.userService.findHogwartsUserById(user.getId());
        });
        /// End of When Section.

        /// Then Section. Assert expected output and verify called methods.
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage(new ObjectNotFoundException(
                        HogwartsUser.class.getSimpleName().toLowerCase(),
                        user.getId())
                        .getMessage());
        /// Verify methods gettin called once.
        verify(this.userRepository, times(1))
                .findById(user.getId());
        /// End of Then Section.
    }

    @Test
    void testUserSaveSuccessScenario() {
        /// Given Section. Define input and mock behavior.
        HogwartsUser newUser;
        {
            newUser = new HogwartsUser();
            newUser.setId(1);
            newUser.setUsername("Agus");
            newUser.setPassword("123456");
            newUser.setEnabled(true);
            newUser.setRoles("user");
        }
        String encodedPassword;
        {
            /// This is not a encoded password, it just normal defined string.
            /// with purpose as mocking of BCrypt hashed password.
            encodedPassword = "Encoded Password";
        }
        ///
        given(this.passwordEncoder.encode(newUser.getPassword())).willReturn(encodedPassword);
        given(this.userRepository.save(newUser))
                .willReturn(newUser);
        /// End of Given Section.

        /// When Section. Act on the target behavior.
        HogwartsUser returnedUser = this.userService.save(newUser);
        /// End of When Section.

        /// Then Section. Assert expected output and verify called methods.
        assertThat(returnedUser.getId()).isEqualTo(newUser.getId());
        assertThat(returnedUser.getUsername()).isEqualTo(newUser.getUsername());
        assertThat(returnedUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(returnedUser.isEnabled()).isEqualTo(newUser.isEnabled());
        assertThat(returnedUser.getRoles()).isEqualTo(newUser.getRoles());
        /// Verify methods gettin called once.
        verify(this.userRepository, times(1))
                .save(newUser);
        /// End of Then Section.
    }

    @Test
    void testUserUpdateSuccessScenario() {
        /// Given Section. Define input and mock behavior.
        HogwartsUser oldUserData;
        {
            oldUserData = new HogwartsUser();
            oldUserData.setId(1);
            oldUserData.setUsername("Agus");
            oldUserData.setPassword("123456");
            oldUserData.setEnabled(true);
            oldUserData.setRoles("user");
        }
        ///
        HogwartsUser newerUserData;
        {
            newerUserData = new HogwartsUser();
            newerUserData.setId(1);
            newerUserData.setUsername("Agus - Update");
            newerUserData.setPassword("123456");
            newerUserData.setEnabled(true);
            newerUserData.setRoles("user");
        }
        ///
        given(this.userRepository.findById(oldUserData.getId()))
                .willReturn(Optional.of(oldUserData));
        given(this.userRepository.save(oldUserData))
                .willReturn(oldUserData);
        /// End of Given Section.

        /// When Section. Act on the target behavior.
        HogwartsUser returnedUser = this.userService.update(oldUserData.getId(), newerUserData);
        /// End of When Section.

        /// Then Section. Assert expected output and verify called methods.
        assertThat(returnedUser.getId()).isEqualTo(oldUserData.getId());
        assertThat(returnedUser.getUsername()).isEqualTo(newerUserData.getUsername());
        /// Verify methods gettin called once.
        verify(this.userRepository, times(1))
                .findById(oldUserData.getId());
        verify(this.userRepository, times(1))
                .save(oldUserData);
        /// End of Then Section.
    }

    @Test
    void testUserUpdateNotFoundScenario() {
        /// Given Section. Define input and mock behavior.
        HogwartsUser oldUserData;
        {
            oldUserData = new HogwartsUser();
            oldUserData.setId(1);
            oldUserData.setUsername("Agus");
            oldUserData.setPassword("123456");
            oldUserData.setEnabled(true);
            oldUserData.setRoles("user");
        }
        ///
        HogwartsUser newerUserData;
        {
            newerUserData = new HogwartsUser();
            newerUserData.setId(1);
            newerUserData.setUsername("Agus - Update");
            newerUserData.setPassword("123456");
            newerUserData.setEnabled(true);
            newerUserData.setRoles("user");
        }
        ///
        given(this.userRepository.findById(oldUserData.getId()))
                .willReturn(Optional.empty());
        /// End of Given Section.

        /// When Section. Act on the target behavior.
        Throwable thrown = catchThrowable(() -> {
            this.userService.findHogwartsUserById(oldUserData.getId());
        });
        /// End of When Section.

        /// Then Section. Assert expected output and verify called methods.
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage(new ObjectNotFoundException(
                        HogwartsUser.class.getSimpleName().toLowerCase(),
                        oldUserData.getId())
                        .getMessage());
        /// Verify methods gettin called once.
        verify(this.userRepository, times(1))
                .findById(oldUserData.getId());
        /// End of Then Section.
    }

    @Test
    void testUserDeleteSuccessScenario() {
        /// Given Section. Define input and mock behavior.
        HogwartsUser oldUserData;
        {
            oldUserData = new HogwartsUser();
            oldUserData.setId(1);
            oldUserData.setUsername("Agus");
            oldUserData.setPassword("123456");
            oldUserData.setEnabled(true);
            oldUserData.setRoles("user");
        }
        ///
        given(this.userRepository.findById(oldUserData.getId()))
                .willReturn(Optional.of(oldUserData));
        doNothing().when(this.userRepository)
                .deleteById(oldUserData.getId());
        /// End of Given Section.

        /// When Section. Act on the target behavior.
        this.userService.delete(oldUserData.getId());
        /// End of When Section.

        /// Then Section. Assert expected output and verify called methods.
        /// Verify methods gettin called once.
        verify(this.userRepository, times(1))
                .findById(oldUserData.getId());
        verify(this.userRepository, times(1))
                .deleteById(oldUserData.getId());
        /// End of Then Section.
    }

    @Test
    void testUserDeleteNotFoundScenario() {
        /// Given Section. Define input and mock behavior.
        HogwartsUser oldUserData;
        {
            oldUserData = new HogwartsUser();
            oldUserData.setId(1);
            oldUserData.setUsername("Agus");
            oldUserData.setPassword("123456");
            oldUserData.setEnabled(true);
            oldUserData.setRoles("user");
        }
        ///
        given(this.userRepository.findById(oldUserData.getId()))
                .willReturn(Optional.empty());
        /// End of Given Section.

        /// When Section. Act on the target behavior.
        Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
            this.userService.delete(oldUserData.getId());
        });
        /// End of When Section.

        /// Then Section. Assert expected output and verify called methods.
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage(new ObjectNotFoundException(
                        HogwartsUser.class.getSimpleName().toLowerCase(),
                        oldUserData.getId()
                ).getMessage());
        /// Verify methods gettin called once.
        verify(this.userRepository, times(1))
                .findById(oldUserData.getId());
        /// End of Then Section.
    }
}