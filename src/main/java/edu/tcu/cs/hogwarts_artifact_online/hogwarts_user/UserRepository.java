package edu.tcu.cs.hogwarts_artifact_online.hogwarts_user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<HogwartsUser, Integer> {

    Optional<HogwartsUser> findByUsername(String username);

    // List<HogwartsUser> findByEnabled(boolean enabled);
    //
    // Optional<HogwartsUser> findByUsernameAndPassword(String username, String password);
}
