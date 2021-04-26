package tj.abad.srws.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tj.abad.srws.io.entity.UserEntity;
import tj.abad.srws.io.repository.UserRepository;
import tj.abad.srws.service.UserService;
import tj.abad.srws.shared.Utils;
import tj.abad.srws.shared.dto.UserDto;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final Utils utils;

    public UserServiceImpl(UserRepository userRepository, Utils utils) {
        this.userRepository = userRepository;
        this.utils = utils;
    }

    @Override
    public UserDto create(UserDto newUser) {
        if(userRepository.findUserByEmail(newUser.getEmail()).isPresent()) throw new RuntimeException("User exist");

        var userEntity = new UserEntity();
        BeanUtils.copyProperties(newUser, userEntity);
        userEntity.setEncryptedPassword("test");
        userEntity.setUserId(utils.generateUserId(30));

        var storedUserDetails = userRepository.save(userEntity);

        var userDto = new UserDto();
        BeanUtils.copyProperties(storedUserDetails, userDto);

        return userDto;

    }
}
