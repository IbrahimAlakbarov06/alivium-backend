package alivium.client;

import alivium.config.PayriffFeignConfig;
import alivium.model.dto.request.PayriffAutoPayRequest;
import alivium.model.dto.request.PayriffCreateOrderRequest;
import alivium.model.dto.response.PayriffAutoPayResponse;
import alivium.model.dto.response.PayriffCreateOrderResponse;
import alivium.model.dto.response.PayriffOrderInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payriffClient"
        ,url = "${payriff.base-url}"
        ,configuration = PayriffFeignConfig.class)
public interface PayriffClient {

    @PostMapping("/orders")
    PayriffCreateOrderResponse createOrder(@RequestBody PayriffCreateOrderRequest payriffCreateOrderRequest);

    @PostMapping("/autoPay")
    PayriffAutoPayResponse autoPay(@RequestBody PayriffAutoPayRequest payRequest);

    @GetMapping("/orders/{orderId}")
    PayriffOrderInfoResponse orderInfo(@PathVariable String orderId);
}
