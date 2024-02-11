package space.titcsl.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import space.titcsl.auth.entity.DeleteAccountRequest;

import java.util.Optional;


@Repository
public interface DeleteRequestRepository extends JpaRepository<DeleteAccountRequest, String> {

    Optional<DeleteAccountRequest> findByEmail(String email);
}
