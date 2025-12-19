package alivium.service;

import alivium.domain.entity.Order;
import alivium.domain.entity.User;
import alivium.domain.entity.Voucher;
import alivium.model.dto.response.VoucherUsageResponse;
import alivium.model.dto.response.VoucherUsageStats;

import java.util.List;

public interface VoucherUsageService {

    VoucherUsageResponse recordVoucherUsage(User user, Voucher voucher, Order order);
    void cancelVoucherUsage(Long userId, Long voucherId, Long orderId);

    int getTotalUsageCount(Long voucherId);
    int getUserUsageCount(Long userId, Long voucherId);
    int getRemainingUsageCount(Long voucherId, Voucher voucher);

    boolean canUserUseVoucher(User user, Voucher voucher);

    List<VoucherUsageResponse> getUserVoucherHistory(Long userId);
    List<VoucherUsageResponse> getVoucherUsageHistory(Long voucherId);
    VoucherUsageStats getVoucherStats(Long voucherId, Voucher voucher);
}
