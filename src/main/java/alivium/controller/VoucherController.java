package alivium.controller;

import alivium.model.dto.request.VoucherRequest;
import alivium.model.dto.response.VoucherResponse;
import alivium.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/voucher")
@RequiredArgsConstructor
@Validated
public class VoucherController {

    private final VoucherService voucherService;

    @PostMapping
    public ResponseEntity<VoucherResponse> createVoucher(@Valid @RequestBody VoucherRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(voucherService.createVoucher(request));
    }

    @PutMapping("/{voucherId}")
    public ResponseEntity<VoucherResponse> updateVoucher(@PathVariable Long voucherId, VoucherRequest request) {
        return ResponseEntity.ok(voucherService.updateVoucher(voucherId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable("id") Long id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoucherResponse> getVoucherById(@PathVariable("id") Long id) {
        VoucherResponse response = voucherService.getVoucherById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<VoucherResponse>> getAllVouchers() {
        List<VoucherResponse> vouchers = voucherService.getAllVouchers();
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/expired")
    public ResponseEntity<List<VoucherResponse>> getExpiredVouchers() {
        List<VoucherResponse> expiredVouchers = voucherService.getExpiredVouchers();
        return ResponseEntity.ok(expiredVouchers);
    }

    @GetMapping("/code")
    public ResponseEntity<VoucherResponse> getVoucherByCode(@RequestParam String code) {
        return ResponseEntity.ok(voucherService.getVoucherByCode(code));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<VoucherResponse> toggleVoucherStatus(@PathVariable("id") Long id,
                                                               @RequestParam("active") boolean active) {
        VoucherResponse response = voucherService.toggleVoucherStatus(id, active);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<VoucherResponse> validateVoucher(@RequestParam("code") String code,
                                                           @RequestParam("orderTotal") Double orderTotal) {
        VoucherResponse response = voucherService.validateVoucher(code, orderTotal);
        return ResponseEntity.ok(response);
    }
}
