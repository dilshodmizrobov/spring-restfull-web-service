package tj.abad.srws.ui.controller;


import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import tj.abad.srws.service.UserService;
import tj.abad.srws.shared.dto.UserDto;
import tj.abad.srws.ui.model.request.UserDetailsRequestModel;
import tj.abad.srws.ui.model.response.UserRest;

@RestController
@RequestMapping("/users") // http://localhost:8080/users
//@CrossOrigin(origins= {"http://localhost:8083", "http://localhost:8084"})
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("")
	public UserRest create(@RequestBody  UserDetailsRequestModel userDetails) {
		var userRest = new UserRest();
		var userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);
		var createdUser = userService.create(userDto);
		BeanUtils.copyProperties(createdUser, userRest);
		return userRest;
	}

	@GetMapping("")
	public String getAll() {
		return "Hello";
	}

}
