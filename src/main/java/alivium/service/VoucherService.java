package alivium.service;

import alivium.model.dto.request.VoucherRequest;
import alivium.model.dto.response.VoucherResponse;

import java.util.List;

public interface VoucherService {
    VoucherResponse createVoucher(VoucherRequest request);
    VoucherResponse updateVoucher(Long voucherId, VoucherRequest request);
    void deleteVoucher(Long voucherId);
    VoucherResponse getVoucherById(Long voucherId);
    List<VoucherResponse> getAllVouchers();
    List<VoucherResponse> getExpiredVouchers();
    VoucherResponse getVoucherByCode(String code);
    VoucherResponse toggleVoucherStatus(Long voucherId, boolean isActive);
    VoucherResponse validateVoucher(String code, Double orderTotal);
}

