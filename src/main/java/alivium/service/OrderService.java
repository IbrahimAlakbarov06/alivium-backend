package alivium.service;

import alivium.model.dto.request.CancelOrderRequest;
import alivium.model.dto.request.CreateOrderRequest;
import alivium.model.dto.response.OrderResponse;
import alivium.model.dto.response.OrderSummaryResponse;
import alivium.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(Long userId, CreateOrderRequest request);

    OrderResponse getOrderById(Long orderId);

    List<OrderSummaryResponse> getUserOrders(Long userId);

    List<OrderSummaryResponse> getUserOrdersByStatus(Long userId, OrderStatus status);

    Long getUserOrdersCount(Long userId);

    OrderResponse cancelOrder(Long orderId, Long userId, CancelOrderRequest request);

    Page<OrderSummaryResponse> getAllOrders(Pageable pageable);

    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);

    List<OrderSummaryResponse> getOrderByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Long countByStatus(OrderStatus status);

    List<OrderSummaryResponse> getOrdersByStatus(OrderStatus status);

}
