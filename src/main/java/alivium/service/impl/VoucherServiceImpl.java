package alivium.service.impl;

import alivium.domain.entity.Voucher;
import alivium.domain.repository.VoucherRepository;
import alivium.exception.AlreadyExistsException;
import alivium.exception.NotFoundException;
import alivium.mapper.VoucherMapper;
import alivium.model.dto.request.VoucherRequest;
import alivium.model.dto.response.VoucherResponse;
import alivium.service.VoucherService;
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
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepo;
    private final VoucherMapper voucherMapper;

    @Override
    @Transactional
    @CacheEvict(value = "vouchers", allEntries = true)
    public VoucherResponse createVoucher(VoucherRequest request) {
        if (voucherRepo.existsByCode(request.getCode())) {
            throw new AlreadyExistsException("Voucher already exists with code: " + request.getCode());
        }

        Voucher voucher = voucherMapper.toEntity(request);
        Voucher saved = voucherRepo.save(voucher);
        return voucherMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "vouchers", allEntries = true)
    public VoucherResponse updateVoucher(Long voucherId, VoucherRequest request) {
        Voucher voucher = findById(voucherId);
        voucherMapper.updateEntityFromDto(request, voucher);
        Voucher saved = voucherRepo.save(voucher);
        return voucherMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "vouchers", allEntries = true)
    public void deleteVoucher(Long voucherId) {
        Voucher voucher = findById(voucherId);
        voucherRepo.delete(voucher);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "vouchers", key = "#voucherId")
    public VoucherResponse getVoucherById(Long voucherId) {
        Voucher voucher = findById(voucherId);
        return voucherMapper.toResponse(voucher);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "vouchers")
    public List<VoucherResponse> getAllVouchers() {
        return voucherRepo.findAll().stream()
                .map(voucherMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "vouchers")
    public List<VoucherResponse> getExpiredVouchers() {
        return voucherRepo.findAll().stream()
                .filter(v -> v.getExpiryDate() != null && v.getExpiryDate().isBefore(LocalDateTime.now()))
                .map(voucherMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "vouchers", key = "#code")
    public VoucherResponse getVoucherByCode(String code) {
        Voucher voucher = voucherRepo.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Voucher not found with code: " + code));
        return voucherMapper.toResponse(voucher);
    }

    @Override
    @Transactional
    @CacheEvict(value = "vouchers", allEntries = true)
    public VoucherResponse toggleVoucherStatus(Long voucherId, boolean isActive) {
        Voucher voucher = findById(voucherId);
        voucher.setIsActive(isActive);
        Voucher saved = voucherRepo.save(voucher);
        return voucherMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherResponse validateVoucher(String code, Double orderTotal) {
        Voucher voucher = voucherRepo.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Voucher not found with code: " + code));

        if (!voucher.getIsActive()) {
            throw new IllegalStateException("Voucher is not active");
        }
        if (voucher.getExpiryDate() != null && voucher.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Voucher has expired");
        }
        if (voucher.getMinOrderAmount() != null && orderTotal < voucher.getMinOrderAmount().doubleValue()) {
            throw new IllegalStateException("Order total does not meet minimum requirement for this voucher");
        }
        return voucherMapper.toResponse(voucher);
    }

    private Voucher findById(Long voucherId) {
        return voucherRepo.findById(voucherId)
                .orElseThrow(() -> new NotFoundException("Voucher not found with id: " + voucherId));
    }
}
