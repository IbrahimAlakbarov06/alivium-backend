package alivium.controller;

import alivium.domain.entity.User;
import alivium.model.dto.request.AddressRequest;
import alivium.model.dto.response.AddressResponse;
import alivium.model.enums.AddressType;
import alivium.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
@Validated
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/my")
    public ResponseEntity<List<AddressResponse>> getMyAddresses(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                addressService.getAddressesForUser(user.getId())
        );
    }

    @GetMapping("/by-country")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<List<AddressResponse>> getAddressesByCountry(
            @RequestParam String country) {
        return ResponseEntity.ok(
                addressService.getAddressesByCountry(country)
        );
    }

    @GetMapping("/by-type")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<List<AddressResponse>> getAddressesByType(
            @RequestParam AddressType type) {
        return ResponseEntity.ok(
                addressService.getAddressesByType(type)
        );
    }


    @GetMapping("/{addressId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<AddressResponse> getAddressById(
            @PathVariable Long addressId) {
        return ResponseEntity.ok(
                addressService.findAddressById(addressId)
        );
    }

    @GetMapping("/my/count")
    public ResponseEntity<Integer> countMyAddresses(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                addressService.countUserAddresses(user.getId())
        );
    }

    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.createAddress(user.getId(), request));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @AuthenticationPrincipal User user,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request) {

        return ResponseEntity.ok(
                addressService.updateAddress(user.getId(), addressId, request)
        );
    }

    @PutMapping("/{addressId}/default")
    public ResponseEntity<AddressResponse> setDefaultAddress(
            @AuthenticationPrincipal User user,
            @PathVariable Long addressId) {
        return ResponseEntity.ok(
                addressService.setDefaultAddress(user.getId(), addressId)
        );
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> removeAddress(
            @AuthenticationPrincipal User user,
            @PathVariable Long addressId) {
        addressService.removeUserAddress(user.getId(), addressId);
        return ResponseEntity.noContent().build();
    }
}