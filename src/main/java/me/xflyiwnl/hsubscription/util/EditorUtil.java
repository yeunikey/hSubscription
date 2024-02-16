package me.xflyiwnl.hsubscription.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.xflyiwnl.hsubscription.HSubscription;
import me.xflyiwnl.hsubscription.SubscriptionConfig;
import me.xflyiwnl.hsubscription.object.Subscription;
import me.xflyiwnl.hsubscription.request.GetRequest;
import me.xflyiwnl.hsubscription.request.PostRequest;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EditorUtil {

    public static void generate(CommandSender sender) {

        sender.sendMessage(Translator.ofString("editor-generate"));

        new BukkitRunnable() {

            @Override
            public void run() {

                String server = "null";
                try {
                    server = InetAddress.getLocalHost().getHostAddress() + ":" + Bukkit.getServer().getPort();
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }

                JsonArray jsons = new JsonArray();

                for (Subscription subscription : HSubscription.getInstance().getSubscriptions()) {

                    JsonObject json = new JsonObject();

                    json.addProperty("name", subscription.getName());

                    StringBuilder sb = new StringBuilder();
                    Duration duration = Duration.between(LocalDateTime.now(), subscription.getPenaltyDate());

                    long years = duration.toDaysPart() / 365;
                    long days = years != 0 ? duration.toDaysPart() - (years * 365) : duration.toDaysPart();
                    long months = days != 0 ? (days / 30) : 0;
                    days = days - (months * 30);

                    sb.append(years).append(",");
                    sb.append(months).append(",");
                    sb.append(days).append(",");
                    sb.append(duration.toHoursPart()).append(",");
                    sb.append(duration.toMinutesPart()).append(",");
                    sb.append(duration.toSecondsPart()).append(",");

                    json.addProperty("date", sb.toString());

                    jsons.add(json);
                }

                JsonObject body = new JsonObject();
                body.addProperty("server", server);
                body.add("subscriptions", jsons);

                HttpResponse<String> response = new PostRequest()
                        .body(body.toString())
                        .url(SubscriptionConfig.getInstance().getServer() + "editor/save")
                        .send();

                JsonObject result = new JsonParser().parse(response.body()).getAsJsonObject();
                if (!result.get("status").getAsBoolean()) {
                    sender.sendMessage(Translator.ofString("editor-error"));
                    return;
                }

                String url = result.get("url").getAsString();

                TextComponent textComponent = new TextComponent();

                for (BaseComponent component : TextComponent.fromLegacyText(Translator.ofString("editor-url")
                        .replace("%url%", url))) {
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(url)));
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
                    textComponent.addExtra(component);
                }

                sender.sendMessage(textComponent);

            }

        }.runTaskAsynchronously(HSubscription.getInstance());

    }

    public static void apply(CommandSender sender, String key) {

        new BukkitRunnable() {

            @Override
            public void run() {
                sender.sendMessage(Translator.ofString("editor-apply")
                        .replace("%key%", key));

                HttpResponse<String> response = new GetRequest()
                        .url(SubscriptionConfig.getInstance().getServer() + "editor/get-apply?" + "key=" + key)
                        .send();

                JsonObject result = new JsonParser().parse(response.body()).getAsJsonObject();

                if (!result.get("status").getAsBoolean()) {
                    sender.sendMessage(Translator.ofString("editor-error"));
                    return;
                }

                List<Subscription> subscriptions = HSubscription.getInstance().getSubscriptions();
                List<Subscription> newSubscriptions = new ArrayList<>();

                for (JsonElement jsonElement : result.get("subscriptions").getAsJsonArray()) {

                    JsonObject subJson = jsonElement.getAsJsonObject();

                    String name = subJson.get("name").getAsString();
                    int years = subJson.get("years").getAsInt();
                    int months = subJson.get("months").getAsInt();
                    int days = subJson.get("days").getAsInt();
                    int hours = subJson.get("hours").getAsInt();
                    int minutes = subJson.get("minutes").getAsInt();
                    int seconds = subJson.get("seconds").getAsInt();

                    Subscription subscription = subscriptions.stream()
                            .filter(s -> s.getName().equalsIgnoreCase(name))
                            .findFirst().orElse(null);

                    if (subscription == null) {
                        subscription = new Subscription();
                        subscription.setName(name);
                        subscription.setRegisteredDate(LocalDateTime.now());

                        LocalDateTime penaltyDate = subscription.getRegisteredDate();
                        if (years != 0) penaltyDate = penaltyDate.plusYears(years);
                        if (months != 0) penaltyDate = penaltyDate.plusMonths(months);
                        if (days != 0) penaltyDate = penaltyDate.plusDays(days);
                        if (hours != 0) penaltyDate = penaltyDate.plusHours(hours);
                        if (minutes != 0) penaltyDate = penaltyDate.plusMinutes(minutes);
                        if (seconds != 0) penaltyDate = penaltyDate.plusSeconds(seconds);
                        subscription.setPenaltyDate(penaltyDate);

                        subscription.create(true);
                    } else {

                        LocalDateTime penaltyDate = subscription.getRegisteredDate();
                        if (years != 0) penaltyDate = penaltyDate.plusYears(years);
                        if (months != 0) penaltyDate = penaltyDate.plusMonths(months);
                        if (days != 0) penaltyDate = penaltyDate.plusDays(days);
                        if (hours != 0) penaltyDate = penaltyDate.plusHours(hours);
                        if (minutes != 0) penaltyDate = penaltyDate.plusMinutes(minutes);
                        if (seconds != 0) penaltyDate = penaltyDate.plusSeconds(seconds);
                        subscription.setPenaltyDate(penaltyDate);

                        subscription.save();
                    }

                    newSubscriptions.add(subscription);

                }

                subscriptions.forEach(subscription1 -> {
                    if (!newSubscriptions.contains(subscription1)) {
                        subscription1.remove();
                    }
                });

                sender.sendMessage(Translator.ofString("editor-apply-end"));
            }

        }.runTaskAsynchronously(HSubscription.getInstance());

    }

}
