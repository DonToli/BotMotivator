package com.example.botmotivator.service;

import com.example.botmotivator.config.BotConfig;
import com.example.botmotivator.model.User;
import com.example.botmotivator.model.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    static final String HELP_TEXT = "This bot is created to demonstrate capabilities.\n\n" +
            "You can execute commands from the main menu on the left or the by typing a command: \n\n" +
            "Type /start to see a welcome message\n\n";
    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start","get welcome message"));
        listOfCommands.add(new BotCommand("/mydata","get your data stored"));
        listOfCommands.add(new BotCommand("/deletedata","delete my data"));
        listOfCommands.add(new BotCommand("/help","info how to use this bot"));
        listOfCommands.add(new BotCommand("/settings","set your preferences"));
        listOfCommands.add(new BotCommand("/expressmotivation","Экспресс мотивация на все случаи!"));

        try {
            this.execute(new SetMyCommands(listOfCommands,new BotCommandScopeDefault(),null));
        }catch (TelegramApiException e){
            log.error("Error setting bot's command list: " + e.getMessage());
        }

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
    public void onUpdateReceived(Update update) {  //Основной класс для ответов

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.contains("/send") && config.getOwnerId() == chatId){   //условие есть команда и соответствует ID
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));// добавляем поддержание смайликов и вырезаем из текста все что находится после send и пробела
                var users = userRepository.findAll();//ввыбираем всех пользователей из базы
                for(User user: users){
                    sendMessage(user.getChatId(),textToSend);//Делаем рассылку на всех пользователей
                }

            }
//тест
            switch (messageText) {
                case "/start" -> {
                    registerUser(update.getMessage());
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                }
                case "/expressmotivation" -> express(chatId);
                case "/help" -> sendMessage(chatId, HELP_TEXT);
                default -> sendMessage(chatId, "Sorry bro, command was not recognized!");
            }
        }else if(update.hasCallbackQuery()){
            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callBackData) {
                case "SPORT_BUTTON" -> {
                    String text = "Ответ принят. Вы выбрали мотивацию на спорт!!\n\n" +
                            "В каком формате хотите получать мотивашки?\n\n";
                    EditMessageText message = new EditMessageText();
                    message.setChatId(String.valueOf(chatId));
                    message.setText(text);
                    message.setMessageId((int) messageId);

                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
                    List<InlineKeyboardButton> rowInLine = new ArrayList<>();

                    var textButton = new InlineKeyboardButton();
                    textButton.setText("Текст");
                    textButton.setCallbackData("SPORT_BUTTON");

                    var audioButton = new InlineKeyboardButton();
                    audioButton.setText("Аудио");
                    audioButton.setCallbackData("AUDIO_BUTTON");

                    var videoButton = new InlineKeyboardButton();
                    videoButton.setText("Видео");
                    videoButton.setCallbackData("VIDEO_BUTTON");

                    var imageButton = new InlineKeyboardButton();
                    imageButton.setText("Картинки");
                    imageButton.setCallbackData("IMAGE_BUTTON");

                    rowInLine.add(textButton);
                    rowInLine.add(audioButton);
                    rowInLine.add(videoButton);
                    rowInLine.add(imageButton);

                    rowsInLine.add(rowInLine);

                    markupInline.setKeyboard(rowsInLine);
                    message.setReplyMarkup(markupInline);

                    try {
                        execute(message);
                    } catch (TelegramApiException e) {

                        log.error("Error occurred: " + e.getMessage());
                    }
                    if(update.hasCallbackQuery()){
                        String callBackData1 = update.getCallbackQuery().getData();
                        long messageId1 = update.getCallbackQuery().getMessage().getMessageId();
                        long chatId1 = update.getCallbackQuery().getMessage().getChatId();


                    }
                }
                case "TEACH_BUTTON" -> {
                    String text = "Ответ принят. Вы выбрали мотивацию на учебу!!\n\n" +
                            "В каком формате хотите получать мотивашки?\n\n";
                    EditMessageText message = new EditMessageText();
                    message.setChatId(String.valueOf(chatId));
                    message.setText(text);
                    message.setMessageId((int) messageId);

                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
                    List<InlineKeyboardButton> rowInLine = new ArrayList<>();

                    var textButton = new InlineKeyboardButton();
                    textButton.setText("Текст");
                    textButton.setCallbackData("SPORT_BUTTON");

                    var audioButton = new InlineKeyboardButton();
                    audioButton.setText("Аудио");
                    audioButton.setCallbackData("AUDIO_BUTTON");

                    var videoButton = new InlineKeyboardButton();
                    videoButton.setText("Видео");
                    videoButton.setCallbackData("VIDEO_BUTTON");

                    var imageButton = new InlineKeyboardButton();
                    imageButton.setText("Картинки");
                    imageButton.setCallbackData("IMAGE_BUTTON");

                    rowInLine.add(textButton);
                    rowInLine.add(audioButton);
                    rowInLine.add(videoButton);
                    rowInLine.add(imageButton);

                    rowsInLine.add(rowInLine);

                    markupInline.setKeyboard(rowsInLine);
                    message.setReplyMarkup(markupInline);

                    try {
                        execute(message);
                    } catch (TelegramApiException e) {

                        log.error("Error occurred: " + e.getMessage());
                    }

                    break;
                }
                case "BUSINESS_BUTTON" -> {
                    String text = "Ответ принят. Вы выбрали мотивацию на бизнес!!\n\n" +
                            "В каком формате хотите получать мотивашки?\n\n";
                    EditMessageText message = new EditMessageText();
                    message.setChatId(String.valueOf(chatId));
                    message.setText(text);
                    message.setMessageId((int) messageId);

                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
                    List<InlineKeyboardButton> rowInLine = new ArrayList<>();

                    var textButton = new InlineKeyboardButton();
                    textButton.setText("Текст");
                    textButton.setCallbackData("SPORT_BUTTON");

                    var audioButton = new InlineKeyboardButton();
                    audioButton.setText("Аудио");
                    audioButton.setCallbackData("AUDIO_BUTTON");

                    var videoButton = new InlineKeyboardButton();
                    videoButton.setText("Видео");
                    videoButton.setCallbackData("VIDEO_BUTTON");

                    var imageButton = new InlineKeyboardButton();
                    imageButton.setText("Картинки");
                    imageButton.setCallbackData("IMAGE_BUTTON");

                    rowInLine.add(textButton);
                    rowInLine.add(audioButton);
                    rowInLine.add(videoButton);
                    rowInLine.add(imageButton);

                    rowsInLine.add(rowInLine);

                    markupInline.setKeyboard(rowsInLine);
                    message.setReplyMarkup(markupInline);

                    try {
                        execute(message);
                    } catch (TelegramApiException e) {

                        log.error("Error occurred: " + e.getMessage());
                    }
                    break;
                }
            }
        }
    }

    private void express(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Мотивация - это то, что заставляет вас начать. Привычка - это то, что заставляет вас продолжать. | Джим Рюн");

        /*InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        var sportButton = new InlineKeyboardButton();
        sportButton.setText("Спорт");
        sportButton.setCallbackData("SPORT_BUTTON");

        var uchebaButton = new InlineKeyboardButton();
        uchebaButton.setText("Учеба");
        uchebaButton.setCallbackData("UCHEBA_BUTTON");

        var businessButton = new InlineKeyboardButton();
        businessButton.setText("Бизнес");
        businessButton.setCallbackData("BUSINESS_BUTTON");

        rowInLine.add(sportButton);
        rowInLine.add(uchebaButton);
        rowInLine.add(businessButton);

        rowsInLine.add(rowInLine);

        markupInline.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInline);*/

        try {
            execute(message);
        } catch (TelegramApiException e) {

            log.error("Error occurred: " + e.getMessage());
        }

    }

    private void registerUser(Message message) {
        if (userRepository.findById(message.getChatId()).isEmpty()){
            var chatId = message.getChatId();
            var chat = message.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegistredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("user saved: "+user);
        }
    }

    private void startCommandReceived(long chatId, String name) {

        String answer = EmojiParser.parseToUnicode ("Привет, " + name + ", для правильной настройки твоего бота мне надо задать тебе 3 вопроса :blush: !\n\n " +
                " На какую цель не хватает мотивации?\n\n");
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(answer);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        var sportButton = new InlineKeyboardButton();
        sportButton.setText("Спорт");
        sportButton.setCallbackData("SPORT_BUTTON");

        var uchebaButton = new InlineKeyboardButton();
        uchebaButton.setText("Учеба");
        uchebaButton.setCallbackData("TEACH_BUTTON");

        var businessButton = new InlineKeyboardButton();
        businessButton.setText("Бизнес");
        businessButton.setCallbackData("BUSINESS_BUTTON");

        rowInLine.add(sportButton);
        rowInLine.add(uchebaButton);
        rowInLine.add(businessButton);

        rowsInLine.add(rowInLine);

        markupInline.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {

            log.error("Error occurred: " + e.getMessage());
        }

        log.info("Replied to user " + name);

    }

    private void sendMessage(long chatId, String textToSent) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSent);

        try {
            execute(message);
        } catch (TelegramApiException e) {

            log.error("Error occurred: " + e.getMessage());
        }

    }

   /* private static void keyboardForStart(SendMessage message) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(); // разметка для клавиатуры

        List<KeyboardRow> keyboardRows = new ArrayList<>();  //список рядов для добавления кнопок
        KeyboardRow row = new KeyboardRow(); //создаем ряд
        row.add("Тема мотивации"); // добавляем
        row.add("Время показа"); // добавляем
        keyboardRows.add(row);// добавляем в список рядов
        row = new KeyboardRow();//создаём новый ряд
        row.add("Количество");
        row.add("Формат крнтента");
        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);//Добавили к клавиатуре ряды

        message.setReplyMarkup(keyboardMarkup); // Привязали к сообщению ряды
    }*/
}
