package com.springboot.chatgptbot.config.bot;

import com.springboot.chatgptbot.domain.Ticket;
import com.springboot.chatgptbot.service.LogService;
import com.springboot.chatgptbot.service.TicketService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    private final TicketService ticketService;
    private final LogService logService;
    private final TelegramBotConfig config;
    private boolean waitingForQuestion = false;
    private long waitingChatId;
    private String waitingUserName;

    public TelegramBot(TelegramBotConfig config, LogService logService, TicketService ticketService) {
        this.config = config;
        this.logService = logService;
        this.ticketService = ticketService;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Nice to meet you."));
        listOfCommands.add(new BotCommand("/help", "Ask questions."));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            if (waitingForQuestion && chatId == waitingChatId) {
                answerAfterQuestion(update);
            } else if (messageText.equals("/help")) {
                sendMessage("What is your question?", chatId);
                waitingForQuestion = true;
                waitingChatId = chatId;
                waitingUserName = update.getMessage().getFrom().getUserName();
            } else {
                handleUserMessage(update);
            }
        }
    }

    private void answerAfterQuestion(Update update) {
        String userQuestion = update.getMessage().getText();
        sendMessage("Wait, they will answer you soon.", waitingChatId);

        LocalDateTime timestamp = LocalDateTime.now();
        saveTicket(waitingUserName, userQuestion, timestamp, waitingChatId);
        waitingForQuestion = false;
    }

    public void sendSyncMessage(String textToSend, long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        if (!textToSend.isEmpty()) {
            message.setText(textToSend);
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending message: {}", e.getMessage());
        }
    }


    public void saveAnswerToTicket(Long ticketId, String answer, Long chatId) {
        ticketService.saveAnswer(ticketId, answer, chatId);
        sendMessageToTicketOwner(ticketId, answer);
    }

    private void sendMessageToTicketOwner(Long ticketId, String answer) {
        Ticket ticket = ticketService.findByTicketId(ticketId);
        if (ticket != null) {
            long userChatId = ticket.getChatId();
            if (answer != null && !answer.isEmpty()) {
                sendSyncMessage(answer, userChatId);
            } else {
                sendSyncMessage("Sorry, the answer to this question is not ready yet.", userChatId);
            }
        } else {
            log.error("Ticket not found for ID: {}", ticketId);
        }
    }

    private void handleUserMessage(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            String userMessage = message.getText();
            long chatId = message.getChatId();

            String botResponse = generateBotResponse(userMessage);
            saveLog(update.getMessage().getFrom().getUserName(), userMessage, botResponse);
            sendMessage(botResponse, chatId);
        }
    }

    private void saveLog(String username, String message, String botResponse) {
        LocalDateTime timestamp = LocalDateTime.now();
        logService.saveLog(username, message, botResponse, timestamp);
    }

    public void saveTicket(String username, String question, LocalDateTime timestamp, Long chatId) {
        Ticket ticket = new Ticket(username, question, timestamp);
        ticket.setChatId(chatId);
        ticketService.saveTicket(ticket);
    }


    private String generateBotResponse(String userMessage) {
        OpenAiService service = new OpenAiService("your-openai-token");
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(userMessage)
                .model("text-davinci-003")
                .maxTokens(255)
                .build();
            return service.createCompletion(completionRequest).getChoices().get(0).getText();
    }

    private void sendMessage(String textToSend, long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        if (!textToSend.isEmpty()) {
            message.setText(textToSend);
        }
        send(message);
    }


    private void send(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }
}