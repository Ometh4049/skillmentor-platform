package com.stemlink.skillmentor.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SchemaPatchConfig {

    private final JdbcTemplate jdbcTemplate;

    @Bean
    public CommandLineRunner ensureMentorProfileImageColumnType() {
        return args -> {
            try {
                jdbcTemplate.execute("ALTER TABLE mentor ALTER COLUMN profile_image_url TYPE TEXT");
                log.info("Schema patch applied: mentor.profile_image_url set to TEXT");
            } catch (Exception exception) {
                log.warn("Schema patch skipped for mentor.profile_image_url: {}", exception.getMessage());
            }
        };
    }
}

