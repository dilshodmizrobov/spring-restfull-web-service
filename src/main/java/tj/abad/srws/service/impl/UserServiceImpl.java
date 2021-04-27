package tj.abad.srws.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tj.abad.srws.io.entity.UserEntity;
import tj.abad.srws.io.repository.UserRepository;
import tj.abad.srws.service.UserService;
import tj.abad.srws.shared.Utils;
import tj.abad.srws.shared.dto.UserDto;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final Utils utils;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, Utils utils, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.utils = utils;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto create(UserDto newUser) {
        if(userRepository.findByEmail(newUser.getEmail()).isPresent()) throw new RuntimeException("User exist");

        var userEntity = new UserEntity();
        BeanUtils.copyProperties(newUser, userEntity);
        userEntity.setEncryptedPassword(passwordEncoder.encode(newUser.getPassword()));
        userEntity.setUserId(utils.generateUserId(30));

        var storedUserDetails = userRepository.save(userEntity);

        var userDto = new UserDto();
        BeanUtils.copyProperties(storedUserDetails, userDto);

        return userDto;

    }

    @Override
    public UserDto getUser(String email) {
        var userEntity=userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not fount"));
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) {
        var user=userRepository.findByEmail(userName).orElseThrow(() -> new UsernameNotFoundException("User not fount"));
        return new User(user.getEmail(), user.getEncryptedPassword(), new ArrayList<>());
    }
}
