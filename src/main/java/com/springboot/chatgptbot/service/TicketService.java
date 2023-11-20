package com.springboot.chatgptbot.service;

import com.springboot.chatgptbot.domain.Ticket;
import com.springboot.chatgptbot.repository.TicketRepository;
import org.springframework.stereotype.Service;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket findByTicketId(Long ticketId) {
        return ticketRepository
                .findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
    }

    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public void saveAnswer(Long ticketId, String answer, Long chatId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setChatId(chatId);
        ticket.setAnswer(answer);
        ticket.setAnswered(true);
        ticketRepository.save(ticket);
    }
}
