package tj.abad.srws.ui.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRest {
	private String userId;
	private String firstName;
	private String lastName;
	private String email;
//	private List<AddressesRest> addresses;
}
