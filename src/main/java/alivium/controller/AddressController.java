package alivium.controller;

import alivium.model.dto.request.AddressRequest;
import alivium.model.dto.request.AddressTypeRequest;
import alivium.model.dto.response.AddressResponse;
import alivium.service.impl.AddressServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
@Validated
public class AddressController {

    private final AddressServiceImpl addressService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or #userId == authentication.principal.id")
    public ResponseEntity<List<AddressResponse>> getAddressesForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getAddressesForUser(userId));
    }

    @GetMapping("/user/{userId}/country")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or #userId == authentication.principal.id")
    public ResponseEntity<List<AddressResponse>> getAddressesByCountry(
            @PathVariable Long userId,
            @RequestParam String country) {
        return ResponseEntity.ok(addressService.getAddressesByCountry(userId, country));
    }

    @GetMapping("/user/{userId}/type")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or #userId == authentication.principal.id")
    public ResponseEntity<List<AddressResponse>> getAddressesByType(
            @PathVariable Long userId,
            @Valid @RequestBody AddressTypeRequest type) {
        return ResponseEntity.ok(addressService.getAddressesByType(userId, type));
    }

    @GetMapping("/{addressId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.findAddressById(addressId));
    }

    @GetMapping("/user/{userId}/count")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or #userId == authentication.principal.id")
    public ResponseEntity<Integer> countUserAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.countUserAddresses(userId));
    }

    @PostMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or #userId == authentication.principal.id")
    public ResponseEntity<AddressResponse> createAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createAddress(userId, request));
    }

    @PutMapping("/user/{userId}/{addressId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or #userId == authentication.principal.id")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse updated = addressService.updateAddress(userId, addressId, request);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/user/{userId}/{addressId}/default")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or #userId == authentication.principal.id")
    public ResponseEntity<AddressResponse> setDefaultAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        AddressResponse updated = addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/user/{userId}/{addressId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or #userId == authentication.principal.id")
    public ResponseEntity<Void> removeUserAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        addressService.removeUserAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }
}