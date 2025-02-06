package edu.tcu.cs.hogwarts_artifact_online.hogwarts_user;

import edu.tcu.cs.hogwarts_artifact_online.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<HogwartsUser> findAllUsers() {
        return this.userRepository.findAll();
    }

    public HogwartsUser findHogwartsUserById(Integer userId) {
        return this.userRepository
                .findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(
                                HogwartsUser.class.getSimpleName().toLowerCase(), userId
                        )
                );
    }

    public HogwartsUser save(HogwartsUser invokedHogwartsUser) {
        /// Line 38 code was hash the user password using BCrypt
        invokedHogwartsUser.setPassword(this.passwordEncoder.encode(invokedHogwartsUser.getPassword()));
        return this.userRepository.save(invokedHogwartsUser);
    }

    public HogwartsUser update(Integer hogwartsUserId, HogwartsUser newerHogwartsUserData) {
        /// Old Logic.
        /// boolean updatedDataFieldCalledEnabled;
        /// {
        ///     updatedDataFieldCalledEnabled = newerHogwartsUserData.isEnabled()
        ///             != this.findHogwartsUserById(hogwartsUserId).isEnabled()
        ///             ? newerHogwartsUserData.isEnabled()
        ///             : this.findHogwartsUserById(hogwartsUserId).isEnabled();
        /// }
        /// String username;
        /// {
        ///     username = newerHogwartsUserData.getUsername() != null
        ///             && !newerHogwartsUserData.getUsername()
        ///             .equals(this.findHogwartsUserById(hogwartsUserId).getUsername())
        ///             ? newerHogwartsUserData.getUsername()
        ///             : this.findHogwartsUserById(hogwartsUserId).getUsername();
        /// }
        /// String roles;
        /// {
        ///     roles = newerHogwartsUserData.getRoles() != null
        ///             && !newerHogwartsUserData.getRoles()
        ///             .equals(this.findHogwartsUserById(hogwartsUserId).getRoles())
        ///             ? newerHogwartsUserData.getRoles()
        ///             : this.findHogwartsUserById(hogwartsUserId).getRoles();
        /// }
        ///
        /// return this.userRepository.findById(hogwartsUserId)
        ///         .map(updatedUser -> {
        ///             updatedUser.setRoles(roles);
        ///             updatedUser.setUsername(username);
        ///             updatedUser.setEnabled(updatedDataFieldCalledEnabled);
        ///             return this.userRepository.save(updatedUser);
        ///         }).orElseThrow(() -> new ObjectNotFoundException(
        ///                 HogwartsUser.class.getSimpleName().toLowerCase(),
        ///                 hogwartsUserId
        ///         ));
        return this.userRepository.findById(hogwartsUserId)
                .map(updatedUser -> {
                    updatedUser.setRoles(newerHogwartsUserData.getRoles());
                    updatedUser.setUsername(newerHogwartsUserData.getUsername());
                    updatedUser.setEnabled(newerHogwartsUserData.isEnabled());
                    return this.userRepository.save(updatedUser);
                }).orElseThrow(() -> new ObjectNotFoundException(
                        HogwartsUser.class.getSimpleName().toLowerCase(),
                        hogwartsUserId
                ));
    }

    public HogwartsUser delete(Integer userId) {
        HogwartsUser hogwartsUser;
        {
            hogwartsUser = this.userRepository.findById(userId)
                    .orElseThrow(
                            () -> new ObjectNotFoundException(
                                    HogwartsUser.class.getSimpleName().toLowerCase(),
                                    userId
                            )
                    );
        }
        this.userRepository.deleteById(hogwartsUser.getId());
        return hogwartsUser;
    }

    private String errorMessage(String username) {
        return "username " + username + " is not found.";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username) // find user with specific declared username.
                .map(hogwartsUser
                        -> new MyUserPrincipal(hogwartsUser)) // If found,
                // wrap HogwartsUser instance in MyUserPrincipal instance.
                .orElseThrow(()
                        -> new UsernameNotFoundException(this.errorMessage(username))); // Otherwise,
                // throw an Exception.
    }
}
