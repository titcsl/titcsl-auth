package space.titcsl.auth.service;

import org.springframework.http.ResponseEntity;
import space.titcsl.auth.dto.JustDataDto;

public interface AdminService {
    ResponseEntity<?> lockAccount(JustDataDto data, String email);
    ResponseEntity<?> unlockAccount(JustDataDto data, String email);
    ResponseEntity<?> deleteAccount(JustDataDto data, String email);
}
