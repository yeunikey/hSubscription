package me.xflyiwnl.hsubscription.util;

import me.xflyiwnl.hsubscription.object.Subscription;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionUtil {

    public static String applyPlaceholders(String text, Subscription subscription) {
        return text.replace("%name%", subscription.getName())
                .replace("%registeredDate%", DateTimeFormatter
                        .ofPattern(Settinger.ofString("date-format"))
                        .format(subscription.getRegisteredDate()))
                .replace("%penaltyDate%", DateTimeFormatter
                        .ofPattern(Settinger.ofString("date-format"))
                        .format(subscription.getPenaltyDate()));
    }

    public static List<String> applyPlaceholders(List<String> text, Subscription subscription) {
        return text.stream()
                .map(s -> applyPlaceholders(s, subscription))
                .collect(Collectors.toList());
    }

    public static void kickPlayer(Player player) {
        if (player != null && player.isOnline() && !player.isOp()) {
            player.kickPlayer(SubscriptionUtil.formattedReason());
        }
    }

    public static String formattedReason() {
        StringBuilder sb = new StringBuilder();
        List<String> messages = Translator.ofStringList("kick-message");
        for (int i = 0; i < messages.size(); i++) {
            sb.append(i != 0 ? "\n" : "").append(messages.get(i));
        }
        return sb.toString();
    }

    public static String formatTime(Subscription subscription) {
        StringBuilder sb = new StringBuilder();
        Duration duration = Duration.between(LocalDateTime.now(), subscription.getPenaltyDate());

        long years = duration.toDaysPart() / 365;
        long days = years != 0 ? duration.toDaysPart() - (years * 365) : duration.toDaysPart();
        long months = days != 0 ? (days / 30) : 0;
        days = days - (months * 30);

        if (years != 0)
            sb.append(years).append(Translator.ofString("years")).append(" ");

        if (months != 0)
            sb.append(months).append(Translator.ofString("months")).append(" ");

        if (days != 0)
            sb.append(days).append(Translator.ofString("days")).append(" ");

        if (duration.toHoursPart() != 0)
            sb.append(duration.toHoursPart()).append(Translator.ofString("hours")).append(" ");

        if (duration.toMinutesPart() != 0)
            sb.append(duration.toMinutesPart()).append(Translator.ofString("minutes")).append(" ");

        if (duration.toSecondsPart() != 0)
            sb.append(duration.toSecondsPart()).append(Translator.ofString("seconds")).append(" ");

        return sb.toString();
    }

}
