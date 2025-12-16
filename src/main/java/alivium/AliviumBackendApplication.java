package alivium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AliviumBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AliviumBackendApplication.class, args);
    }

}
