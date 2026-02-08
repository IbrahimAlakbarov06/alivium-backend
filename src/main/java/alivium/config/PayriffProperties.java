package alivium.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "payriff")
public class PayriffProperties {
    private String baseUrl;
    private String merchantId;
    private String secretKey;
}
