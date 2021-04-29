package tj.abad.srws.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tj.abad.srws.exceptions.UserServiceException;
import tj.abad.srws.io.entity.UserEntity;
import tj.abad.srws.io.repository.UserRepository;
import tj.abad.srws.service.UserService;
import tj.abad.srws.shared.Utils;
import tj.abad.srws.shared.dto.UserDto;
import tj.abad.srws.ui.model.response.ErrorMessages;

import java.util.ArrayList;
import java.util.List;

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
        if (userRepository.findByEmail(newUser.getEmail())!=null)
            throw new UserServiceException("Record already exists");

        for (int i = 0; i < newUser.getAddresses().size(); i++) {
            var address = newUser.getAddresses().get(i);
            address.setUserDetails(newUser);
            address.setAddressId(utils.generateAddressId(30));
            newUser.getAddresses().set(i, address);
        }

        var userEntity = new ModelMapper().map(newUser, UserEntity.class);
        userEntity.setEncryptedPassword(passwordEncoder.encode(newUser.getPassword()));
        userEntity.setUserId(utils.generateUserId(30));

        var storedUserDetails = userRepository.save(userEntity);

        return new ModelMapper().map(storedUserDetails, UserDto.class);

    }

    @Override
    public UserDto updateUser(String userId, UserDto user) {

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());

        UserEntity updatedUserDetails = userRepository.save(userEntity);

        return new ModelMapper().map(updatedUserDetails, UserDto.class);
    }

    @Transactional
    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

        userRepository.delete(userEntity);

    }

    @Override

    public UserDto getUser(String email) {
        var userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
           throw new UsernameNotFoundException("User not fount");
        }

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    @Override
    public UserDto findUserByUserId(String userId) {
        var userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User with this " + userId + " not found");
        }
        var userDto = new UserDto();
        BeanUtils.copyProperties(userEntity, userDto);
        return userDto;
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();

        if (page > 0) page = page - 1;

        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();

        for (UserEntity userEntity : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(userEntity, userDto);
            returnValue.add(userDto);
        }

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) {
        var user = userRepository.findByEmail(userName);
        if (user == null) {
            throw new UsernameNotFoundException("User not fount");
        }
        return new User(user.getEmail(), user.getEncryptedPassword(), new ArrayList<>());
    }
}
