package space.titcsl.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import space.titcsl.auth.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByDisplayName(String displayName);
    boolean existsByPhone(String phoneNo);
    boolean existsByEmail(String email);
}
