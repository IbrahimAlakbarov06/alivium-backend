package alivium.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum ShippingMethod {

    FREE(
            "Free delivery",
            new BigDecimal("0.00"),
            7
    ),
    STANDART(
            "Standart delivery",
            new BigDecimal("4.90"),
            5
    ),

    FAST(
            "Fast delivery",
            new BigDecimal("9.90"),
            3
    );

    private final String displayName;
    private final BigDecimal cost;
    private final int estimatedDays;


    public boolean isFree() {
        return this == FREE;
    }

    public String getDescription() {
        if (isFree()) {
            return String.format("%s (%d days) - Free", displayName, estimatedDays);
        }
        return String.format("%s (%d days) - $%.2f", displayName, estimatedDays, cost);
    }
}
