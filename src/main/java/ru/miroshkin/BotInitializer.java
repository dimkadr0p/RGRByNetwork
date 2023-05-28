package ru.miroshkin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.miroshkin.service.TelegramService;

import javax.annotation.PostConstruct;


@Component
@Slf4j
public class BotInitializer {

    @Autowired
    TelegramService bot;

    @PostConstruct
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException ex) {
            log.error("error occurred: " + ex.getMessage());
        }
    }
}
