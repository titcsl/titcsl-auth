package space.titcsl.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import space.titcsl.auth.entity.LockAccountRequest;
import space.titcsl.auth.entity.User;

import java.util.Optional;

@Repository
public interface LockAccountRepository extends JpaRepository<LockAccountRequest, String> {
    Optional<LockAccountRequest> findByEmail(String email);
}
