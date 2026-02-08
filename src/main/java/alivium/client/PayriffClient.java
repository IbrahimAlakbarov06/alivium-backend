package alivium.client;

import alivium.config.PayriffFeignConfig;
import alivium.model.dto.request.PayriffCreateOrderRequest;
import alivium.model.dto.response.PayriffCreateOrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payriffClient"
        ,url = "${payriff.base-url}"
        ,configuration = PayriffFeignConfig.class)
public interface PayriffClient {

    @PostMapping("/api/v3/orders")
    PayriffCreateOrderResponse createOrder(@RequestBody PayriffCreateOrderRequest payriffCreateOrderRequest);
}
