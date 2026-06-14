package com.baruunaylal.backend.config;

import com.baruunaylal.backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // ✅ Энэ import заавал байх ёстой
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    // ✅ application.properties-оос линкүүдийг уншина. Байхгүй бол default линкүүдийг авна.
    @Value("${app.cors.allowed-origins:http://localhost:5173,http://127.0.0.1:5173,http://192.168.1.5:5173,https://khovd-aylal.vercel.app}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. PUBLIC ENDPOINTS
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/uploads/**",
                                "/error",
                                "/favicon.ico",
                                "/api/v1/register-camp",
                                "/api/v1/contact-messages"
                        ).permitAll()

                        // 2. ХЭРЭГЛЭГЧИЙН ХАНДАХ ЗАМУУД (Заавал токен шаардана)
                        .requestMatchers("/api/v1/bookings/my").authenticated()
                        .requestMatchers("/api/v1/bookings/my-list").authenticated()
                        .requestMatchers("/api/v1/users/me").authenticated()
                        .requestMatchers("/api/v1/users/me/avatar").authenticated()
                        .requestMatchers("/api/v1/bookings/user/**").authenticated()
                        .requestMatchers("/api/v1/notes/**").authenticated()

                        // 3. НИЙТЭД НЭЭЛТТЭЙ GET ХҮСЭЛТҮҮД
                        .requestMatchers(HttpMethod.GET, "/api/v1/camps/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/soums/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/aimags/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/tour-camps/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/tour-spots/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/brands/**").permitAll()

                        // 4. SYSTEM ADMIN ЭРХТЭЙ ХЭРЭГЛЭГЧИД
                        .requestMatchers("/api/v1/admin/refunds/**")
                        .hasAnyAuthority(
                                "ADMIN",
                                "SYSTEM_ADMIN",
                                "CAMP_ADMIN",
                                "CAMP_MANAGER",
                                "HOTEL_ADMIN",
                                "ROLE_ADMIN",
                                "ROLE_SYSTEM_ADMIN",
                                "ROLE_CAMP_ADMIN",
                                "ROLE_CAMP_MANAGER",
                                "ROLE_HOTEL_ADMIN"
                        )

                        .requestMatchers("/api/v1/users/**", "/api/v1/camps/pending", "/api/v1/admin/**")
                        .hasAnyAuthority("ADMIN", "SYSTEM_ADMIN", "ROLE_ADMIN", "ROLE_SYSTEM_ADMIN")

                        // 5. CAMP / HOTEL ADMIN - (Stats болон Dashboard харах хэсэг)
                        .requestMatchers(
                                "/api/v1/camp-admin/**",
                                "/api/v1/camp-dashboard/**",
                                "/api/v1/camps/my-camp/**"
                        ).hasAnyAuthority("ADMIN", "CAMP_ADMIN", "HOTEL_ADMIN", "CAMP_MANAGER", "ROLE_ADMIN", "ROLE_CAMP_ADMIN")

                        // 6. БУСАД БҮХ GET ХҮСЭЛТҮҮД
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").permitAll()

                        // 7. БУСАД БҮХ POST, PUT, DELETE (Заавал нэвтрэх)
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ Линкүүдийг таслалаар нь салгаж ухаалгаар жагсаалт (List) болгох хэсэг
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                    .map(String::trim)
                    .toList());
        } else {
            // Энд локал болон онлайн бүх хаягуудыг унах замаар бэлдэж өгсөн
            configuration.setAllowedOrigins(List.of(
                    "http://localhost:5173",
                    "http://127.0.0.1:5173",
                    "http://192.168.1.5:5173",
                    "https://khovd-aylal.vercel.app"
            ));
        }

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}