package alivium.domain.repository;

import alivium.domain.entity.Order;
import alivium.model.enums.OrderStatus;
import alivium.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(Long orderNumber);

    boolean existsByOrderNumber(Long orderNumber);

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus orderStatus);

    List<Order> findByPaymentStatusOrderByCreatedAtDesc(PaymentStatus paymentStatus);

    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId,OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    Long countByUserId(Long userId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :orderId")
    Optional<Order> findByIdWithItems(Long orderId);

}
