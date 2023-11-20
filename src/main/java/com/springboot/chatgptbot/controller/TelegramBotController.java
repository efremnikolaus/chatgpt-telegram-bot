package com.springboot.chatgptbot.controller;

import com.springboot.chatgptbot.domain.Log;
import com.springboot.chatgptbot.domain.Ticket;
import com.springboot.chatgptbot.repository.LogRepository;
import com.springboot.chatgptbot.repository.TicketRepository;
import com.springboot.chatgptbot.service.TicketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/openai")
public class TelegramBotController {
    private final LogRepository logRepository;
    private final TicketRepository ticketRepository;
    private final TicketService ticketService;

    public TelegramBotController(LogRepository logRepository, TicketRepository ticketRepository, TicketService ticketService) {
        this.logRepository = logRepository;
        this.ticketRepository = ticketRepository;
        this.ticketService = ticketService;
    }

    @GetMapping("/list")
    public String showAllLogs(Model model) {
        List<Log> logs = logRepository.findAll();
        model.addAttribute("logs", logs);
        return "list";
    }

    @GetMapping("/tickets")
    public String showTickets(Model model) {
        List<Ticket> tickets = ticketRepository.findAll();
        model.addAttribute("tickets", tickets);
        return "tickets";
    }

    @PostMapping("/tickets")
    public String submitAnswer(@RequestParam Long ticketId, @RequestParam String answer, @RequestParam Long chatId) {
        ticketService.saveAnswer(ticketId, answer, chatId);
        return "redirect:/openai/tickets";
    }
}