package alivium.domain.repository;

import alivium.domain.entity.Address;
import alivium.model.enums.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
    Optional<Address> findByIdAndUserId(Long id, Long userId);
    int countByUserId(Long userId);
    List<Address> findByCountry(String country);
    List<Address> findByAddressType(AddressType type);
}
