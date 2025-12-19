package alivium.service.impl;

import alivium.domain.entity.Order;
import alivium.domain.entity.User;
import alivium.domain.entity.Voucher;
import alivium.domain.entity.VoucherUsage;
import alivium.domain.repository.VoucherUsageRepository;
import alivium.exception.BusinessException;
import alivium.mapper.VoucherUsageMapper;
import alivium.model.dto.response.VoucherUsageResponse;
import alivium.model.dto.response.VoucherUsageStats;
import alivium.service.VoucherUsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherUsageServiceImpl implements VoucherUsageService {

    private final VoucherUsageRepository voucherUsageRepository;
    private final VoucherUsageMapper mapper;

    @Override
    @Transactional
    @CacheEvict(value = {"voucherUsage", "voucherStats"}, allEntries = true)
    public VoucherUsageResponse recordVoucherUsage(User user, Voucher voucher, Order order) {
        if(!canUserUseVoucher(user, voucher)) {
            throw new BusinessException("User cannot use this voucher");
        }
        if (order != null && voucherUsageRepository.existsByUserIdAndVoucherIdAndOrderId(
                user.getId(), voucher.getId(), order.getId())) {
            throw new BusinessException("Voucher already used for this order");
        }

        VoucherUsage usage = VoucherUsage.builder()
                .user(user)
                .voucher(voucher)
                .order(order)
                .build();
        VoucherUsage saved = voucherUsageRepository.save(usage);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"voucherUsage", "voucherStats"}, allEntries = true)
    public void cancelVoucherUsage(Long userId, Long voucherId, Long orderId) {
        List<VoucherUsage> usages =
                voucherUsageRepository.findByUserIdAndVoucherIdAndOrderId(
                        userId, voucherId, orderId);

        voucherUsageRepository.deleteAll(usages);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalUsageCount(Long voucherId) {
        return (int) voucherUsageRepository.countByVoucherId(voucherId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getUserUsageCount(Long userId, Long voucherId) {
        return voucherUsageRepository.countByUserIdAndVoucherId(userId, voucherId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getRemainingUsageCount(Long voucherId, Voucher voucher) {
        if (voucher.getTotalUsageLimit() == null) {
            return Integer.MAX_VALUE;
        }

        int used = getTotalUsageCount(voucherId);
        return Math.max(0, voucher.getTotalUsageLimit() - used);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserUseVoucher(User user, Voucher voucher) {
        if (!Boolean.TRUE.equals(voucher.getIsActive())) {
            return false;
        }
        if (voucher.getExpiryDate() != null && LocalDateTime.now().isAfter(voucher.getExpiryDate())) {
            return false;
        }

        if (voucher.getTotalUsageLimit() != null &&
                getTotalUsageCount(voucher.getId()) >= voucher.getTotalUsageLimit()) {
            return false;
        }

        int userUsage = getUserUsageCount(user.getId(), voucher.getId());
        return userUsage < voucher.getPerUserLimit();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "voucherUsage", key = "#userId")
    public List<VoucherUsageResponse> getUserVoucherHistory(Long userId) {
        return voucherUsageRepository.findByUserId(userId).stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "voucherUsage", key = "#voucherId")
    public List<VoucherUsageResponse> getVoucherUsageHistory(Long voucherId) {
        return voucherUsageRepository.findByVoucherId(voucherId).stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "voucherStats", key = "#voucherId")
    public VoucherUsageStats getVoucherStats(Long voucherId, Voucher voucher) {
        int totalUsage = getTotalUsageCount(voucherId);
        int uniqueUsers = voucherUsageRepository.countUniqueUsersByVoucherId(voucherId);
        Integer totalLimit = voucher.getTotalUsageLimit();

        return VoucherUsageStats.builder()
                .voucherId(voucherId)
                .totalUsageCount(totalUsage)
                .uniqueUsersCount(uniqueUsers)
                .totalUsageLimit(totalLimit)
                .perUserLimit(voucher.getPerUserLimit())
                .remainingTotalUsage(
                        totalLimit == null ? null : Math.max(0, totalLimit - totalUsage)
                )
                .unlimitedTotalUsage(totalLimit == null)
                .build();
    }
}
