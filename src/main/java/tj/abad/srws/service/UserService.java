package tj.abad.srws.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import tj.abad.srws.shared.dto.UserDto;

public interface UserService extends UserDetailsService {
    UserDto create(UserDto newUser);

    UserDto getUser(String email);
}
