package edu.tcu.cs.hogwarts_artifact_online.hogwarts_user;

import edu.tcu.cs.hogwarts_artifact_online.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        /// We need to encode plain password before saving to the DB! TODO
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
}
