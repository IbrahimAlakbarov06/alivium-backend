package alivium.mapper;

import alivium.domain.entity.Address;
import alivium.model.dto.request.AddressRequest;
import alivium.model.dto.response.AddressResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Address toEntity(AddressRequest request);

    AddressResponse toResponse(Address address);

    List<AddressResponse> toListResponse(List<Address> addressList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateAddress(@MappingTarget Address address, AddressRequest request);
}
