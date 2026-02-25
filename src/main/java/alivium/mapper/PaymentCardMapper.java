package alivium.mapper;

import alivium.domain.entity.PaymentCard;
import alivium.model.dto.response.PaymentCardResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentCardMapper {

    public PaymentCardResponse toResponse(PaymentCard paymentCard) {
        if (paymentCard == null) return null;

        return PaymentCardResponse.builder()
                .id(paymentCard.getId())
                .last4Digits(paymentCard.getLast4Digits())
                .cardType(paymentCard.getCardType())
                .isDefault(paymentCard.getIsDefault())
                .expiryMonth(paymentCard.getExpiryMonth())
                .expiryYear(paymentCard.getExpiryYear())
                .build();
    }
}
