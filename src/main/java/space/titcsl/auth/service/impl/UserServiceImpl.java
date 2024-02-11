package space.titcsl.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import space.titcsl.auth.exception.GlobalExceptionHandler;
import space.titcsl.auth.repository.UserRepository;
import space.titcsl.auth.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userRepository.findByEmail(username)
                        .orElseThrow(() -> new GlobalExceptionHandler("User with that email is not found or does not belong to you. Try checking the registered phone for verifying email correctly."));
            }
        };
    }

}
