package alivium.service.impl;

import alivium.domain.entity.Address;
import alivium.domain.entity.User;
import alivium.domain.repository.AddressRepository;
import alivium.exception.NotFoundException;
import alivium.mapper.AddressMapper;
import alivium.model.dto.request.AddressRequest;
import alivium.model.dto.request.AddressTypeRequest;
import alivium.model.dto.response.AddressResponse;
import alivium.service.AddressService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserServiceImpl userService;
    private final AddressMapper addressMapper;


    @Transactional(readOnly=true)
    @Cacheable(value = "addresses", key = "#userId")
    public List<AddressResponse> getAddressesForUser(Long userId) {
        return addressMapper.toListResponse(addressRepository.findByUserId(userId));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "addresses", key = "#userId + '-' + #country")
    public List<AddressResponse> getAddressesByCountry(Long userId, String country) {
        return addressMapper.toListResponse(addressRepository.findByUserIdAndCountry(userId, country));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "addresses", key = "#userId + '-' + #type.addressType")
    public List<AddressResponse> getAddressesByType(Long userId, AddressTypeRequest type) {
        return addressMapper.toListResponse(addressRepository.findByUserIdAndAddressType(userId, type.getAddressType()));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "addresses", key = "'address-' + #addressId")
    public AddressResponse findAddressById(Long addressId) {
        Address address= addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Address not found with id: " + addressId));
        return addressMapper.toResponse(address);
    }

    @Transactional(readOnly = true)
    public Integer countUserAddresses(Long userId) {
        return addressRepository.countByUserId(userId);
    }

    @Transactional
    @CacheEvict(value = "addresses", key = "#userId", allEntries = true)
    public AddressResponse createAddress(Long userId,AddressRequest request){
        User user=userService.findById(userId);

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            resetUserDefaultAddresses(userId);
        }

        Address address = addressMapper.toEntity(request);
        address.setUser(user);

        Address saved = addressRepository.save(address);
        return addressMapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "addresses", key = "#userId", allEntries = true)
    public AddressResponse updateAddress(Long userId,Long addressId, AddressRequest request){
        Address address=findUserAddressOrThrow(userId,addressId);

        if(Boolean.TRUE.equals(request.getIsDefault())) {
            resetUserDefaultAddresses(userId);
        }

        addressMapper.updateAddress(address,request);
        Address updated=addressRepository.save(address);

        return addressMapper.toResponse(updated);
    }

    @Transactional
    @CacheEvict(value = "addresses", key = "#userId", allEntries = true)
    public AddressResponse setDefaultAddress(Long userId, Long addressId) {
        Address address = findUserAddressOrThrow(userId,addressId);

        resetUserDefaultAddresses(userId);
        address.setIsDefault(true);

        addressRepository.save(address);
        return addressMapper.toResponse(address);
    }

    @Transactional
    @CacheEvict(value = "addresses", key = "#userId", allEntries = true)
    public void removeUserAddress(Long userId, Long addressId) {
        Address address = findUserAddressOrThrow(userId,addressId);

        addressRepository.delete(address);
    }

    //helper method
    private void resetUserDefaultAddresses(Long userId) {
        List<Address> userAddresses = addressRepository.findByUserId(userId);
        userAddresses.forEach(a -> a.setIsDefault(false));
        addressRepository.saveAll(userAddresses);
    }
    //helper method
    private Address findUserAddressOrThrow(Long userId, Long addressId) {
        return addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new NotFoundException("Address not found with id: " + addressId));
    }

}
