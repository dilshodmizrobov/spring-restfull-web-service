package tj.abad.srws.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tj.abad.srws.exceptions.UserServiceException;
import tj.abad.srws.io.entity.AddressEntity;
import tj.abad.srws.io.entity.UserEntity;
import tj.abad.srws.io.repository.UserRepository;
import tj.abad.srws.service.UserService;
import tj.abad.srws.shared.Utils;
import tj.abad.srws.shared.dto.AddressDTO;
import tj.abad.srws.shared.dto.UserDto;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class UserServiceImplTest {

    String userId = "hhty57ehfy";
    String encryptedPassword = "74hghd8474jf";
    UserEntity userEntity;
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private Utils utils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("Sergey");
        userEntity.setLastName("Kargopolov");
        userEntity.setUserId(userId);
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setEmail("test@test.com");
        userEntity.setEmailVerificationToken("7htnfhr758");
        userEntity.setAddresses(getAddressesEntity());
    }

    @Test
    void testGetUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
        var userDto=userService.getUser("test@test.com");
        assertNotNull(userDto);
        assertEquals("Sergey", userDto.getFirstName());
    }

    @Test
    void testGetUser_UsernameNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUser("test@test.com");
        });
    }
    @Test
    void testCreate(){
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn("sadfsdfsd");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(passwordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName("Sergey");
        userDto.setLastName("Kargopolov");
        userDto.setPassword("12345678");
        userDto.setEmail("test@test.com");
        var storedUserDetails = userService.create(userDto);
        assertNotNull(storedUserDetails);
        assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
        assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
        assertNotNull(storedUserDetails.getUserId());
        assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
        verify(utils,times(storedUserDetails.getAddresses().size())).generateAddressId(30);
        verify(utils,times(1)).generateUserId(30);
        verify(passwordEncoder, times(1)).encode("12345678");
        verify(userRepository,times(1)).save(any(UserEntity.class));

    }

    @Test
    void testCreate_UsernameNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
        assertThrows(UserServiceException.class, () -> {
            UserDto userDto = new UserDto();
            userDto.setAddresses(getAddressesDto());
            userDto.setFirstName("Sergey");
            userDto.setLastName("Kargopolov");
            userDto.setPassword("12345678");
            userDto.setEmail("test@test.com");
            userService.create(userDto);
        });
    }

    private List<AddressDTO> getAddressesDto() {
        AddressDTO addressDto = new AddressDTO();
        addressDto.setType("shipping");
        addressDto.setCity("Vancouver");
        addressDto.setCountry("Canada");
        addressDto.setPostalCode("ABC123");
        addressDto.setStreetName("123 Street name");

        AddressDTO billingAddressDto = new AddressDTO();
        billingAddressDto.setType("billling");
        billingAddressDto.setCity("Vancouver");
        billingAddressDto.setCountry("Canada");
        billingAddressDto.setPostalCode("ABC123");
        billingAddressDto.setStreetName("123 Street name");

        List<AddressDTO> addresses = new ArrayList<>();
        addresses.add(addressDto);
        addresses.add(billingAddressDto);

        return addresses;

    }

    private List<AddressEntity> getAddressesEntity()
    {
        List<AddressDTO> addresses = getAddressesDto();

        Type listType = new TypeToken<List<AddressEntity>>() {}.getType();

        return new ModelMapper().map(addresses, listType);
    }
}