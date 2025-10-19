package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

import static org.aspectj.weaver.patterns.ISignaturePattern.PATTERN;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private static final Pattern PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final NotificationRepository repository;

    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(NotificationRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            if (update.message() != null && update.message().text() != null) {
                var text = update.message().text();
                Long chatId = update.message().chat().id();
                if ("/start".equals(text)) {
                    telegramBot.execute(new SendMessage(update.message().chat().id(), "Добро пожаловать в бот!"));
                } else {
                    var matcher = PATTERN.matcher(text);
                    if (matcher.matches()) {
                        var dateTime = LocalDateTime.parse(matcher.group(1), DATE_TIME_FORMATTER);
                        var taskText = matcher.group(3);
                        repository.save(new NotificationTask(taskText, chatId, dateTime));
                        telegramBot.execute(new SendMessage(chatId, "Установлено напоминание"));
                    }
                }
            }
            //            update.message().text();
            logger.info("Processing update: {}", update);

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
