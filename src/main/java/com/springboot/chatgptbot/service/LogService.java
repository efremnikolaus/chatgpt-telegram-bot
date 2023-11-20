package com.springboot.chatgptbot.service;

import com.springboot.chatgptbot.domain.Log;
import com.springboot.chatgptbot.repository.LogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {
    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void saveLog(String username, String message, String botResponse, LocalDateTime timestamp) {
        Log log = new Log(username, message, botResponse, timestamp);
        logRepository.save(log);
    }
}
