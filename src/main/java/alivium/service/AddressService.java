package alivium.service;

import alivium.model.dto.request.AddressRequest;
import alivium.model.dto.response.AddressResponse;
import alivium.model.enums.AddressType;

import java.util.List;

public interface AddressService {
    List<AddressResponse> getAddressesForUser(Long userId);
    List<AddressResponse> getAddressesByCountry(String country);
    List<AddressResponse> getAddressesByType(AddressType type);
    AddressResponse findAddressById(Long addressId);
    Integer countUserAddresses(Long userId);
    AddressResponse createAddress(Long userId, AddressRequest request);
    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);
    AddressResponse setDefaultAddress(Long userId, Long addressId);
    void removeUserAddress(Long userId, Long addressId);
}
