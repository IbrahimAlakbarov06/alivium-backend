package alivium.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads .env.local from the project root into the Spring environment on local runs.
 * Registered in META-INF/spring.factories. Added with the lowest precedence so real
 * OS environment variables always win. Missing file is not an error (prod/CI supply
 * real environment variables instead).
 */
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String ENV_FILE = ".env.local";
    private static final String PROPERTY_SOURCE_NAME = "dotenvLocal";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Path envFile = Path.of(ENV_FILE);
        if (!Files.isReadable(envFile)) {
            return;
        }

        Map<String, Object> properties = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(envFile);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int eq = trimmed.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String key = trimmed.substring(0, eq).trim();
                String value = stripQuotes(trimmed.substring(eq + 1).trim());
                properties.put(key, value);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + ENV_FILE, e);
        }

        if (!properties.isEmpty()) {
            environment.getPropertySources()
                    .addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
        }
    }

    private String stripQuotes(String value) {
        if (value.length() >= 2
                && ((value.startsWith("\"") && value.endsWith("\""))
                || (value.startsWith("'") && value.endsWith("'")))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
