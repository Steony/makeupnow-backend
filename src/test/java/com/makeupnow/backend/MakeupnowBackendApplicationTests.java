package com.makeupnow.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@SpringBootTest
@ActiveProfiles("test")
class MakeupnowBackendApplicationTests {

    @DynamicPropertySource
    static void loadEnvVariables(DynamicPropertyRegistry registry) throws IOException {
        Properties properties = new Properties();
        properties.load(Files.newInputStream(Paths.get(".env")));
        properties.forEach((key, value) -> registry.add((String) key, () -> value));
    }

    @Test
    void contextLoads() {
    }
}
