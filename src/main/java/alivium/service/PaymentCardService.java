package alivium.service;

import alivium.model.dto.request.AddPaymentCardRequest;
import alivium.model.dto.response.PaymentCardResponse;

import java.util.List;

public interface PaymentCardService {
    PaymentCardResponse addCard(Long userId, AddPaymentCardRequest request);
    List<PaymentCardResponse> getUserCards(Long userId);
    PaymentCardResponse setDefaultCard(Long userId, Long cardId);
    void deleteCard(Long userId, Long cardId);
    PaymentCardResponse getCardById(Long userId, Long cardId);
    PaymentCardResponse getDefaultCard(Long userId);
    PaymentCardResponse toggleCardStatus(Long userId, Long cardId);
}
