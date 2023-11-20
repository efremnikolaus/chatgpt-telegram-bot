package com.springboot.chatgptbot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "logs")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @NotEmpty
    private String message;

    @NotEmpty
    private String botResponse;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime timestamp;

    private Long chatId;

    public Log(String username, String message, String botResponse, LocalDateTime timestamp) {
        this.username = username;
        this.message = message;
        this.botResponse = botResponse;
        this.timestamp = timestamp;
    }
}
