package tech.justjava.process_manager.keycloak;

import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class KeycloakService {
    public Optional<UserDTO> getCurrentUser() {
        return Optional.empty();
    }

    public Optional<UserDTO> getCurrentUserWithGroup() {
        return Optional.empty();
    }

    public List<UserDTO> getAllUsers() {
        return Collections.emptyList();
    }
}
