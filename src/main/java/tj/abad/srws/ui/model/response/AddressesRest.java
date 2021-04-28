package tj.abad.srws.ui.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressesRest extends RepresentationModel<AddressesRest> {
	private String addressId;
	private String city;
	private String country;
	private String streetName;
	private String postalCode;
	private String type;
}
