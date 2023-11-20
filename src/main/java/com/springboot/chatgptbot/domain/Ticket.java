package com.springboot.chatgptbot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    private String username;

    @NotEmpty
    private String question;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime timestamp;

    private String answer;

    private boolean answered;

    private Long chatId;

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public Ticket(String username, String question, LocalDateTime timestamp) {
        this.username = username;
        this.question = question;
        this.timestamp = timestamp;
    }
}
