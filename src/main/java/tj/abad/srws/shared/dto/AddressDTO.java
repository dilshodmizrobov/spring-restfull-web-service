package tj.abad.srws.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private long id;
    private String addressId;
    private String city;
    private String country;
    private String streetName;
    private String postalCode;
    private String type;
    private UserDto userDetails;
}
