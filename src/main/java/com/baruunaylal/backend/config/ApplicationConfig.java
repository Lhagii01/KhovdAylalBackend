package com.baruunaylal.backend.config;

import com.baruunaylal.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@org.springframework.data.jpa.repository.config.EnableJpaAuditing
public class ApplicationConfig {

    private final UserRepository userRepository;

    // Хэрэглэгчийн мэдээллийг хэрхэн олохыг тодорхойлно (и-мэйлээр хайна)
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Хэрэглэгч олдсонгүй"));
    }

    // Нэвтрэх үед хэрэглэгчийг баталгаажуулах provider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // DaoAuthenticationProvider-г ашиглан хэрэглэгчийн мэдээллийг UserDetailsService-ээр авна.
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder()); // Нууц үгийг шалгах encoder
        return authProvider;
    }

    // Нэвтрэх процессоо удирдах менежер
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Нууц үгийг hash хийхэд ашиглах encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}