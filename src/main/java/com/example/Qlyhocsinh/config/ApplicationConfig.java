package com.example.Qlyhocsinh.config;

import com.example.Qlyhocsinh.entity.User;
import com.example.Qlyhocsinh.enums.Role;
import com.example.Qlyhocsinh.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if(userRepository.findByUsername("admin").isEmpty()){


                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .role("ADMIN")
                        .build();

                userRepository.save(user);
                log.warn("tai khoan admin da duoc tao");
            }

        };
    }
}
