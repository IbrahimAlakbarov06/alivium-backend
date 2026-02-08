package alivium.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PayriffFeignConfig {

    private final PayriffProperties properties;

    @Bean
    public RequestInterceptor authInterceptor(){
        return template->{
            template.header("Authorization", properties.getSecretKey());
            template.header("Content-Type", "application/json");
        };
    }
}
