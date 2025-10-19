package pro.sky.telegrambot.sheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import pro.sky.telegrambot.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class NotificationNotifier {
    private final TelegramBot bot;
    private final NotificationRepository repository;

    public NotificationNotifier(TelegramBot bot, NotificationRepository repository) {
        this.bot = bot;
        this.repository = repository;
    }
    //   @Scheduled("* */1 * * * *")
    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedDelay = 1)
    public void notifyTask() {
        repository.findAllByDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .forEach(task -> {
                    bot.execute(new SendMessage(task.getChatId(), task.getText()));
                    repository.delete(task);
                });
    }
}
