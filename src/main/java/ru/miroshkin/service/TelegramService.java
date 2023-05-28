package ru.miroshkin.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.miroshkin.config.BotConfig;
import ru.miroshkin.config.BotMenu;
import ru.miroshkin.entities.Product;
import ru.miroshkin.save.SaveFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class TelegramService extends TelegramLongPollingBot {

    private final BotConfig config;
    private final BotMenu botMenu;
    private final SendMessage message = new SendMessage();
    private final KatalogService katalogService;
    private final SendDocument sendDocument = new SendDocument();


    @Autowired
    public TelegramService(BotConfig config, BotMenu botMenu, KatalogService katalogService) {
        this.config = config;
        this.botMenu = botMenu;
        this.katalogService = katalogService;
        initMenu();
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.startsWith("/pars")) {
                int num1, num2;
                String link;
                String[] parts = messageText.substring(6).trim().split(" ");
                link = getTrueLink(parts[0]);
                if(parts.length == 3) {
                    num1 = Integer.parseInt(parts[1]);
                    num2 = Integer.parseInt(parts[2]);
                } else {
                    num1 = 1;
                    num2 = 1;
                }

                try {
                    sendMessage(chatId, "Идет парсинг данных...");
                    List<Product> productList = katalogService.parsing(link, num1, num2);
                    SaveFile.saveToJson(productList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendFile(chatId);
                log.info("Пользователь {} спарсил и отправил себе файл с сервера", update.getMessage().getChat().getUserName());
                return;
            }

            switch (messageText) {
                case "/start" -> {
                    sendMessage(chatId, """
                            Этот бот будет парсить данные с сайта n-katalog. Пожалуйста вставьте ссылку с сайта и отправьте нам!!!
                            Пример: /pars https://n-katalog.ru/category/obektivy/list/
                            Так же через пробел вы можете указать с какую по какую страницу парсить.
                            Пример: /pars https://n-katalog.ru/category/obektivy/list/ 5 10""");
                    log.info("Запустил бота пользователь: {}, chatId: {}", update.getMessage().getChat().getUserName(), update.getMessage().getChatId());
                }
                case "/help" -> sendMessage(chatId, "Список доступных команд : \n" + botMenu.getInfoCommands());
                default -> {
                    sendMessage(chatId, "Такой команды не существует.\n" + botMenu.getInfoCommands());
                    log.info("Пользователь {} ввел несуществующую команду", update.getMessage().getChat().getUserName());
                }
            }


        }
    }

    private String getTrueLink(String link) {
        char lastChar = link.charAt(link.length() - 1);
        if(lastChar != '/') {
            return link + '/';
        }
        return link;
    }

    private void sendMessage(long chatId, String textToSend) {
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.disableWebPagePreview();
        try {
            execute(message);
        } catch (TelegramApiException ex) {
            log.error("error occurred: {}", ex.getMessage());
        }
    }

    private void sendFile(long chatId) {
        sendDocument.setChatId(chatId);
        File file = new File("Products.json");
        InputFile inputFile = new InputFile(file);
        sendDocument.setDocument(inputFile);
        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void initMenu() {
        try {
            this.execute(new SetMyCommands(botMenu.getCommands(), new BotCommandScopeDefault(), null));
        } catch (TelegramApiException ex) {
            log.error("Ошибка инициализации меню бота: {}", ex.getMessage());
        }
    }

}
