package alivium.model.dto.response;

import alivium.model.enums.OrderStatus;
import alivium.model.enums.PaymentMethod;
import alivium.model.enums.PaymentStatus;
import alivium.model.enums.ShippingMethod;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private OrderStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;
    private Integer totalItems;

    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal shipping;
    private BigDecimal totalPrice;

    private String trackingNumber;
    private ShippingMethod shippingMethod;
    private String estimatedDelivery;

    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    private String deliveryAddress;

    private String voucherCode;

    private String notes;

    private String cancellationReason;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime cancellationDate;
}