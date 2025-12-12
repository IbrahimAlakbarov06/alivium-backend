package alivium.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistResponse {

    private Long id;
    private Long userId;
    private String username;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Double averageRating;
    private Integer reviewCount;
    private Boolean productActive;
    private String primaryImageUrl;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime addedAt;
}
