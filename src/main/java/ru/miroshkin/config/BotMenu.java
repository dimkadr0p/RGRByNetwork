package ru.miroshkin.config;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.ArrayList;
import java.util.List;


@Component
public class BotMenu {

    private final List<BotCommand> commands = new ArrayList<>();

    public BotMenu() {
        addCommands();
    }

    public List<BotCommand> getCommands() {
        return commands;
    }

    private void addCommands() {
        commands.add(new BotCommand("/start", "Запускается начальное состояние бота"));
        commands.add(new BotCommand("/pars", "Парсит данные с сайта"));
    }


    public String getInfoCommands() {
        StringBuilder stringBuffer = new StringBuilder();

        for (BotCommand command: commands) {
            stringBuffer.append(command.getCommand()).append(" - ").append(command.getDescription()).append("\n");
        }

        return stringBuffer.toString();
    }
}