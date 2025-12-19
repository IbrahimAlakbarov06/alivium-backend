package alivium.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoucherUsageStats {
    private Long voucherId;
    private Integer totalUsageCount;
    private Integer uniqueUsersCount;
    private Integer totalUsageLimit;
    private Integer perUserLimit;
    private Integer remainingTotalUsage;
    private Boolean unlimitedTotalUsage;
}
