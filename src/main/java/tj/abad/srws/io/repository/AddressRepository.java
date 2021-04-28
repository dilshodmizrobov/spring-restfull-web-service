package tj.abad.srws.io.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tj.abad.srws.io.entity.AddressEntity;
import tj.abad.srws.io.entity.UserEntity;

import java.util.List;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
	List<AddressEntity> findAllByUserDetails(UserEntity userEntity);
	AddressEntity findByAddressId(String addressId);
}
