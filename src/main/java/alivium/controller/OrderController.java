package alivium.controller;

import alivium.domain.entity.User;
import alivium.model.dto.request.CancelOrderRequest;
import alivium.model.dto.request.CreateOrderRequest;
import alivium.model.dto.response.OrderResponse;
import alivium.model.dto.response.OrderSummaryResponse;
import alivium.model.enums.OrderStatus;
import alivium.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal User user,
            @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(user.getId(), request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderSummaryResponse>> getUserOrders(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(orderService.getUserOrders(user.getId()));
    }


    @GetMapping("/my/status")
    public ResponseEntity<List<OrderSummaryResponse>> getUserOrdersByStatus(
            @AuthenticationPrincipal User user,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.getUserOrdersByStatus(user.getId(), status));
    }

    @GetMapping("/my/count")
    public ResponseEntity<Long> getUserOrdersCount(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(orderService.getUserOrdersCount(user.getId()));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal User user,
            @RequestBody CancelOrderRequest request) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId, user.getId(), request));
    }


    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<Page<OrderSummaryResponse>> getAllOrders(Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @GetMapping("/admin/status")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<List<OrderSummaryResponse>> getOrdersByStatus(
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @GetMapping("/admin/status/count")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<Long> countByStatus(@RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.countByStatus(status));
    }


    @GetMapping("/admin/date-range")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<List<OrderSummaryResponse>> getOrderByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(orderService.getOrderByDateRange(startDate, endDate));
    }

    @PatchMapping("/admin/{orderId}/status")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }
}