package com.springboot.chatgptbot.config.bot;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("classpath:application.properties")
public class TelegramBotConfig {

    @Value("${telegram.botName}")
    private String botUsername;

    @Value("${telegram.botToken}")
    private String botToken;
}
