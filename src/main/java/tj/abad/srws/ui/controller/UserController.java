package tj.abad.srws.ui.controller;


import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import tj.abad.srws.service.AddressService;
import tj.abad.srws.service.UserService;
import tj.abad.srws.shared.dto.AddressDTO;
import tj.abad.srws.shared.dto.UserDto;
import tj.abad.srws.ui.model.request.UserDetailsRequestModel;
import tj.abad.srws.ui.model.response.AddressesRest;
import tj.abad.srws.ui.model.response.OperationStatusModel;
import tj.abad.srws.ui.model.response.RequestOperationStatus;
import tj.abad.srws.ui.model.response.UserRest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/users") // http://localhost:8080/users
//@CrossOrigin(origins= {"http://localhost:8083", "http://localhost:8084"})
public class UserController {

    private final UserService userService;
    private final AddressService addressService;

    public UserController(UserService userService, AddressService addressService) {
        this.userService = userService;
        this.addressService = addressService;
    }

    @PostMapping(value = "",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest create(@RequestBody UserDetailsRequestModel userDetails) {

        var userDto = new ModelMapper().map(userDetails, UserDto.class);
        var createdUser = userService.create(userDto);
        return new ModelMapper().map(createdUser, UserRest.class);

    }

    @PutMapping(path = "/{id}", consumes = {MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE})
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        userDto = new ModelMapper().map(userDetails, UserDto.class);

        UserDto updateUser = userService.updateUser(id, userDto);
        returnValue = new ModelMapper().map(updateUser, UserRest.class);

        return returnValue;
    }

    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);

        Type listType = new TypeToken<List<UserRest>>() {
        }.getType();
        returnValue = new ModelMapper().map(users, listType);

        return returnValue;
    }

    @GetMapping(value = "/{userId}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUserById(@PathVariable String userId) {
        var userDto = userService.findUserByUserId(userId);
        return new ModelMapper().map(userDto,UserRest.class);
    }

    // http://localhost:8080/mobile-app-ws/users/jfhdjeufhdhdj/addressses

    @GetMapping(path = "/{id}/addresses", produces = {MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
    public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id) {
        List<AddressesRest> addressesListRestModel = new ArrayList<>();

        List<AddressDTO> addressesDTO = addressService.getAddresses(id);

        if (addressesDTO != null && !addressesDTO.isEmpty()) {
            Type listType = new TypeToken<List<AddressesRest>>() {
            }.getType();
            addressesListRestModel = new ModelMapper().map(addressesDTO, listType);

            for (AddressesRest addressRest : addressesListRestModel) {
                Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId()))
                        .withSelfRel();
                addressRest.add(addressLink);
            }
        }

        Link userLink = linkTo(methodOn(UserController.class).getUserById(id)).withRel("user");
        return CollectionModel.of(addressesListRestModel,userLink);
    }


    @GetMapping(path = "/{userId}/addresses/{addressId}", produces = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE, "application/hal+json"})
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

        var addressesDto = addressService.getAddress(addressId);
        var addressDto = new ModelMapper().map(addressesDto, AddressesRest.class);
        var userLink = linkTo(UserController.class).slash(userId).withRel("user");
        var addresses = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
        var self = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
//        addressDto.add(userLink, addresses, self);
//        return addressDto;
        return EntityModel.of(addressDto, Arrays.asList(userLink, addresses, self));
    }

}
