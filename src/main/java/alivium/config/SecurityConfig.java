package alivium.config;

import alivium.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;  // ✅ ƏLAVƏ ET

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))  // ✅ ƏLAVƏ ET
                .authorizeHttpRequests(auth -> auth
                        // AUTH ENDPOINTS (Public)
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/verify-email",
                                "/api/auth/resend-verification",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",
                                "/api/auth/refresh-token",
                                "/api/auth/google-login"
                        ).permitAll()

                        // OAUTH2 (Public)
                        .requestMatchers(
                                "/api/auth/oauth2/**",
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()

                        // PRODUCTS (Public GET only)
                        .requestMatchers(HttpMethod.GET,
                                "/api/products",
                                "/api/products/active",
                                "/api/products/{id}",
                                "/api/products/search",
                                "/api/products/category/{categoryId}",
                                "/api/products/collection/{collectionId}",
                                "/api/products/price-range"
                        ).permitAll()

                        // CATEGORIES (Public GET only)
                        .requestMatchers(HttpMethod.GET,
                                "/api/categories",
                                "/api/categories/active",
                                "/api/categories/main",
                                "/api/categories/*/subcategories",
                                "/api/categories/{id}",
                                "/api/categories/name/**",
                                "/api/categories/parent/{parentId}",
                                "/api/categories/root"
                        ).permitAll()

                        // COLLECTIONS (Public GET only)
                        .requestMatchers(HttpMethod.GET,
                                "/api/collections",
                                "/api/collections/active",
                                "/api/collections/{id}",
                                "/api/collections/name/{name}",
                                "/api/collections/type/{type}",
                                "/api/collections/trending",
                                "/api/collections/new-arrivals",
                                "/api/collections/current"
                        ).permitAll()

                        // PRODUCT VARIANTS (Public GET only)
                        .requestMatchers(HttpMethod.GET,
                                "/api/variants/product/{productId}",
                                "/api/variants/{id}",
                                "/api/variants/available/{productId}",
                                "/api/products/{productId}/variants",
                                "/api/products/{productId}/variants/available",
                                "/api/variants/sku/{sku}"
                        ).permitAll()

                        // SEARCH (Public GET only)
                        .requestMatchers(HttpMethod.GET,
                                "/api/search/products"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/search/products/filter"
                        ).permitAll()

                        // REVIEWS (Public GET only)
                        .requestMatchers(HttpMethod.GET,
                                "/api/reviews/product/{productId}",
                                "/api/reviews/product/{productId}/rating",
                                "/api/reviews/{reviewId}"
                        ).permitAll()

                        // PRODUCT IMAGES (Public GET only)
                        .requestMatchers(HttpMethod.GET,
                                "/api/product-images/product/{productId}",
                                "/api/product-images/{imageId}/download-url",
                                "/api/product-images/{imageId}/download"
                        ).permitAll()

                        // REVIEW IMAGES (Public GET only)
                        .requestMatchers(HttpMethod.GET,
                                "/api/review-images/review/{reviewId}",
                                "/api/review-images/{imageId}/download-url",
                                "/api/review-images/{imageId}/download"
                        ).permitAll()

                        // VOUCHERS (Public validation only)
                        .requestMatchers(HttpMethod.GET,
                                "/api/voucher/validate",
                                "/api/voucher/code"
                        ).permitAll()

                        // STRIPE WEBHOOK (Public - authenticated via signature verification)
                        .requestMatchers(HttpMethod.POST,
                                "/api/payments/webhook"
                        ).permitAll()

                        // SWAGGER/API DOCS (Public)
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        .requestMatchers(
                                "/websocket/**",
                                "/websocket",
                                "/ws/**",
                                "/topic/**",
                                "/app/**"
                        ).permitAll()

                        .requestMatchers(
                                "/chat.html",
                                "/*.html",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/static/**"
                        ).permitAll()

                        // CHAT ENDPOINTS - PROTECTED
                        .requestMatchers("/api/chatRooms/**").authenticated()
                        .requestMatchers("/api/chat-messages/**").authenticated()

                        // ALL OTHER ENDPOINTS - PROTECTED
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}