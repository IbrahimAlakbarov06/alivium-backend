package alivium.service.impl;

import alivium.model.dto.request.AddPaymentCardRequest;
import alivium.model.dto.response.PaymentCardResponse;
import alivium.service.PaymentCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService {
    @Override
    public PaymentCardResponse addCard(Long userId, AddPaymentCardRequest request) {
        return null;
    }

    @Override
    public List<PaymentCardResponse> getUserCards(Long userId) {
        return List.of();
    }

    @Override
    public PaymentCardResponse setDefaultCard(Long userId, Long cardId) {
        return null;
    }

    @Override
    public void deleteCard(Long userId, Long cardId) {

    }

    @Override
    public PaymentCardResponse getCardById(Long userId, Long cardId) {
        return null;
    }

    @Override
    public PaymentCardResponse getDefaultCard(Long userId) {
        return null;
    }

    @Override
    public PaymentCardResponse toggleCardStatus(Long userId, Long cardId) {
        return null;
    }
}
