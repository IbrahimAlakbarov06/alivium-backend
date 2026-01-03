package alivium.controller;

import alivium.model.dto.request.CustomNotificationRequest;
import alivium.model.dto.request.NotificationRequest;
import alivium.model.dto.response.MessageResponse;
import alivium.model.dto.response.NotificationResponse;
import alivium.model.enums.NotificationTemplate;
import alivium.service.NotificationService;
import alivium.service.NotificationTemplateService;
import alivium.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final NotificationService notificationService;
    private final NotificationTemplateService notificationTemplateService;
    private final VoucherService voucherService;

    @PostMapping("/send")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<NotificationResponse> sendNotification(
            @Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }

    @PostMapping("/send-voucher-to-all/{voucherId}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<MessageResponse> sendVoucherToAll(@PathVariable Long voucherId) {
        voucherService.sendVoucherToAll(voucherId);
        return ResponseEntity.ok(new MessageResponse("Successfully sent voucher to all"));
    }

    @PostMapping("/send-flash-sale")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<MessageResponse> sendFlashSale(
            @RequestParam String category,
            @RequestParam Integer discount,
            @RequestParam Integer hours,
            @RequestParam(required = false) String imageUrl) {

        Map<String, String> params = new HashMap<>();
        params.put("category", category);
        params.put("discount", String.valueOf(discount));
        params.put("hours", String.valueOf(hours));

        notificationTemplateService.sendNotificationToAll(
                NotificationTemplate.FLASH_SALE,
                params,
                imageUrl
        );

        return ResponseEntity.ok(new MessageResponse("Flash sale notification sent to all users"));
    }

    @PostMapping("/send-seasonal-sale")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<MessageResponse> sendSeasonalSale(
            @RequestParam String season,
            @RequestParam String category,
            @RequestParam Integer discount,
            @RequestParam String endDate,
            @RequestParam(required = false) String imageUrl) {

        Map<String, String> params = new HashMap<>();
        params.put("season", season);
        params.put("category", category);
        params.put("discount", String.valueOf(discount));
        params.put("endDate", endDate);

        notificationTemplateService.sendNotificationToAll(
                NotificationTemplate.SEASONAL_SALE,
                params,
                imageUrl
        );

        return ResponseEntity.ok(new MessageResponse("Seasonal sale notification sent to all users"));
    }

    @PostMapping("/send-new-arrival")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<MessageResponse> sendNewArrival(
            @RequestParam String productName,
            @RequestParam String price,
            @RequestParam(required = false) String imageUrl) {

        Map<String, String> params = new HashMap<>();
        params.put("productName", productName);
        params.put("price", price);

        notificationTemplateService.sendNotificationToAll(
                NotificationTemplate.NEW_ARRIVAL,
                params,
                imageUrl
        );

        return ResponseEntity.ok(new MessageResponse("New arrival notification sent to all users"));
    }

    @PostMapping("/send-free-shipping")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<MessageResponse> sendFreeShipping(
            @RequestParam String minAmount,
            @RequestParam String endDate,
            @RequestParam(required = false) String imageUrl) {

        Map<String, String> params = new HashMap<>();
        params.put("minAmount", minAmount);
        params.put("endDate", endDate);

        notificationTemplateService.sendNotificationToAll(
                NotificationTemplate.FREE_SHIPPING,
                params,
                imageUrl
        );

        return ResponseEntity.ok(new MessageResponse("Free shipping notification sent to all users"));
    }

    @PostMapping("/send-custom")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<MessageResponse> sendCustomNotification(
            @Valid @RequestBody CustomNotificationRequest request) {

        notificationTemplateService.sendNotificationToAll(
                request.getTemplate(),
                request.getParams(),
                request.getImageUrl()
        );

        return ResponseEntity.ok(new MessageResponse("Custom notification sent to all users"));
    }
}