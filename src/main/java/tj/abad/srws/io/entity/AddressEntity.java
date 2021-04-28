package tj.abad.srws.io.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="addresses")
public class AddressEntity implements Serializable {

	private static final long serialVersionUID = 7809200551672852690L;
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column(length=30, nullable=false)
	private String addressId;
	
	@Column(length=15, nullable=false)
	private String city;
	
	@Column(length=15, nullable=false)
	private String country;
	
	@Column(length=100, nullable=false)
	private String streetName;
	
	@Column(length=7, nullable=false)
	private String postalCode;
	
	@Column(length=10, nullable=false)
	private String type;
	
	@ManyToOne
	@JoinColumn(name="users_id")
	private UserEntity userDetails;

}
