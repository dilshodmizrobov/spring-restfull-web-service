package tj.abad.srws.service;

import tj.abad.srws.shared.dto.UserDto;

public interface UserService {
    UserDto create(UserDto newUser);
}
