package com.baruunaylal.backend;

import com.baruunaylal.backend.entity.User;
import com.baruunaylal.backend.enums.Role;
import com.baruunaylal.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EntityScan(basePackages = "com.baruunaylal.backend.entity")
@EnableJpaRepositories(basePackages = "com.baruunaylal.backend.repository")
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BackendApplication.class);
        app.setWebApplicationType(WebApplicationType.SERVLET);
        app.run(args);
    }

    /**
     * Систем анх эхлэхэд 'admin@baruun.mn' байхгүй бол үүсгэнэ.
     */
    @Bean
    public CommandLineRunner createInitialAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            final String ADMIN_EMAIL = "admin@baruun.mn";

            if (userRepository.findByEmail(ADMIN_EMAIL).isEmpty()) {
                // builder() ашиглахдаа entity-ийн талбаруудыг зөв бөглөх
                User admin = User.builder()
                        .firstName("System")
                        .lastName("Admin")
                        .email(ADMIN_EMAIL)
                        .password(passwordEncoder.encode("Admin123!"))
                        .role(Role.ADMIN)
                        .enabled(true) // 👈 Анхаар: Энэ true байх ёстой, эсвэл Login дээр алдаа өгнө
                        .build();

                userRepository.save(admin);
                System.out.println("✅ СИСТЕМИЙН АДМИН ҮҮСЛЭЭ: " + ADMIN_EMAIL + " (Нууц үг: Admin123!)");
            } else {
                System.out.println("ℹ️ ADMIN хэрэглэгч аль хэдийн бүртгэлтэй байна.");
            }
        };
    }
}