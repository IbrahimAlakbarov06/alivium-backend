package alivium.mapper;

import alivium.domain.entity.VoucherUsage;
import alivium.model.dto.response.VoucherUsageResponse;
import org.springframework.stereotype.Component;

@Component
public class VoucherUsageMapper {

    public static VoucherUsageResponse toResponse(VoucherUsage usage) {
        if (usage == null) return null;

        return VoucherUsageResponse.builder()
                .id(usage.getId())
                .voucherCode(usage.getVoucher().getCode())
                .userId(usage.getUser().getId())
                .orderId(usage.getOrder() != null ? usage.getOrder().getId() : null)
                .usedAt(usage.getUsedAt())
                .build();
    }
}
