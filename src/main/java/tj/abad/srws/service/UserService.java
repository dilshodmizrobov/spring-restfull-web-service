package tj.abad.srws.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import tj.abad.srws.shared.dto.UserDto;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto create(UserDto newUser);

    UserDto updateUser(String userId, UserDto user);

    @Transactional
    void deleteUser(String userId);

    UserDto getUser(String email);

    UserDto findUserByUserId(String userId);

    List<UserDto> getUsers(int page, int limit);
}
