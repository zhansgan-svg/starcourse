package com.starcourse.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DeepSeekClientTest {

    @Test
    void constructor_shouldInitializeWithConfig() {
        // Given
        String apiKey = "sk-test-key";
        String baseUrl = "https://api.deepseek.com/v1";
        String model = "xiaomi/mimo-v2.5-pro";

        // When
        DeepSeekClient client = new DeepSeekClient(apiKey, baseUrl, model);

        // Then
        assertNotNull(client);
    }

    @Test
    void chat_shouldHandleNetworkError() {
        // Given
        DeepSeekClient client = new DeepSeekClient("sk-test", "http://localhost:99999", "xiaomi/mimo-v2.5-pro");

        // When & Then
        assertThrows(Exception.class, () -> {
            client.chat("test prompt");
        });
    }
}
