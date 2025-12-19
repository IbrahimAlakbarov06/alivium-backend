package alivium.controller;

import alivium.domain.entity.User;
import alivium.domain.entity.Voucher;
import alivium.model.dto.response.VoucherUsageResponse;
import alivium.model.dto.response.VoucherUsageStats;
import alivium.service.VoucherUsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/voucher-usage")
@RequiredArgsConstructor
public class VoucherUsageController {

    private final VoucherUsageService voucherUsageService;

    @PostMapping("/use")
    public ResponseEntity<VoucherUsageResponse> useVoucher(
            @AuthenticationPrincipal User user,
            @RequestParam Long voucherId
    ) {
        Voucher voucher = new Voucher();
        voucher.setId(voucherId);
        return ResponseEntity.ok(voucherUsageService.recordVoucherUsage(user, voucher, null));
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<Void> cancelVoucherUsage(
            @AuthenticationPrincipal User user,
            @RequestParam Long voucherId
    ) {
        voucherUsageService.cancelVoucherUsage(user.getId(), voucherId, null);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/history/user")
    public ResponseEntity<List<VoucherUsageResponse>> getUserHistory(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(voucherUsageService.getUserVoucherHistory(user.getId()));
    }

    @GetMapping("/history/voucher/{voucherId}")
    public ResponseEntity<List<VoucherUsageResponse>> getVoucherHistory(
            @PathVariable Long voucherId
    ) {
        return ResponseEntity.ok(voucherUsageService.getVoucherUsageHistory(voucherId));
    }

    @GetMapping("/stats/{voucherId}")
    public ResponseEntity<VoucherUsageStats> getVoucherStats(@PathVariable Long voucherId) {
        Voucher voucher = new Voucher();
        voucher.setId(voucherId);
        return ResponseEntity.ok(voucherUsageService.getVoucherStats(voucherId, voucher));
    }
}
