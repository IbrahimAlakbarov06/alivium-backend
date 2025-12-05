package alivium.mapper;

import alivium.domain.entity.Address;
import alivium.model.dto.request.AddressRequest;
import alivium.model.dto.response.AddressResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toEntity(AddressRequest request);

    AddressResponse toResponse(Address address);

    List<AddressResponse> toListResponse(List<Address> addressList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAddress(@MappingTarget Address address, AddressRequest request);
}
