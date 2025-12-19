package alivium.mapper;

import alivium.domain.entity.Voucher;
import alivium.model.dto.request.VoucherRequest;
import alivium.model.dto.response.VoucherResponse;
import org.springframework.stereotype.Component;

@Component
public class VoucherMapper {

    public VoucherResponse toResponse(Voucher voucher) {
        if(voucher==null) return null;

        return VoucherResponse.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .title(voucher.getTitle())
                .description(voucher.getDescription())
                .type(voucher.getType())
                .discountValue(voucher.getDiscountValue())
                .minOrderAmount(voucher.getMinOrderAmount())
                .maxDiscountAmount(voucher.getMaxDiscountAmount())
                .totalUsageLimit(voucher.getTotalUsageLimit())
                .perUserLimit(voucher.getPerUserLimit())
                .isActive(voucher.getIsActive())
                .expiryDate(voucher.getExpiryDate())
                .createdAt(voucher.getCreatedAt())
                .updatedAt(voucher.getUpdatedAt())
                .build();
    }

    public  Voucher toEntity(VoucherRequest request) {
        if (request == null) return null;

        return Voucher.builder()
                .code(request.getCode())
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .discountValue(request.getDiscountValue())
                .minOrderAmount(request.getMinOrderAmount())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .totalUsageLimit(request.getTotalUsageLimit())
                .perUserLimit(request.getPerUserLimit())
                .expiryDate(request.getExpiryDate())
                .isActive(true)
                .build();
    }

    public void updateEntityFromDto(VoucherRequest request, Voucher voucher) {
        if(request == null || voucher == null) return;

        if (request.getCode() != null) voucher.setCode(request.getCode());
        if (request.getTitle() != null) voucher.setTitle(request.getTitle());
        if (request.getDescription() != null) voucher.setDescription(request.getDescription());
        if (request.getType() != null) voucher.setType(request.getType());
        if (request.getDiscountValue() != null) voucher.setDiscountValue(request.getDiscountValue());
        if (request.getMinOrderAmount() != null) voucher.setMinOrderAmount(request.getMinOrderAmount());
        if (request.getMaxDiscountAmount() != null) voucher.setMaxDiscountAmount(request.getMaxDiscountAmount());
        if (request.getTotalUsageLimit() != null) voucher.setTotalUsageLimit(request.getTotalUsageLimit());
        if (request.getPerUserLimit() != null) voucher.setPerUserLimit(request.getPerUserLimit());
        if (request.getExpiryDate() != null) voucher.setExpiryDate(request.getExpiryDate());
    }

}
