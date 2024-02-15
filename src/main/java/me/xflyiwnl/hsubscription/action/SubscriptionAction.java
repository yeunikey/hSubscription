package me.xflyiwnl.hsubscription.action;

import me.xflyiwnl.hsubscription.HSubscription;
import me.xflyiwnl.hsubscription.gui.AdminGUI;
import me.xflyiwnl.hsubscription.object.Subscription;
import me.xflyiwnl.hsubscription.util.SubscriptionUtil;
import me.xflyiwnl.hsubscription.util.Translator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubscriptionAction implements Action {

    private List<String> modes = Arrays.asList(
            "remove",
            "set",
            "give",
            "take"
    );
    private String mode = null;
    private String format = null;

    private Player player;
    private Subscription subscription;

    public SubscriptionAction(Player player, Subscription subscription) {
        this.player = player;
        this.subscription = subscription;
    }

    @Override
    public void start() {
        player.sendMessage(Translator.ofString("action-type"));
    }

    @Override
    public void remove() {
        HSubscription.getInstance().getActions().remove(player.getUniqueId());
    }

    @Override
    public void onChat(PlayerChatEvent event) {
        event.setCancelled(true);
        if (mode == null) {
            if (!modes.contains(event.getMessage())) {
                player.sendMessage(Translator.ofString("action-error"));
                return;
            }
            mode = event.getMessage();
            onChange();
        } else {
            format = event.getMessage();
            onChange();
        }

    }

    public void onChange() {
        if (mode.equalsIgnoreCase("remove")) {

            if (subscription == null) {
                getPlayer().sendMessage(Translator.ofString("no-subscription"));
                return;
            }

            getPlayer().sendMessage(Translator.ofString("subscription-remove")
                    .replace("%name%", subscription.getName()));

            HSubscription.getInstance().writeSubscription(getPlayer(), subscription, true);
            subscription.remove();

            Player player = Bukkit.getPlayer(subscription.getName());
            SubscriptionUtil.kickPlayer(player);

            remove();
            return;
        }

        if (format == null) {
            player.sendMessage(Translator.ofString("action-format"));
            return;
        }

        switch (mode.toLowerCase()) {
            case "set" -> {

                LocalDateTime registeredDate = LocalDateTime.now();
                LocalDateTime penaltyDate = registeredDate;

                List<String> formattedDate = new ArrayList<>(List.of(format.split(",")));

                boolean error = false;
                for (String date : formattedDate) {
                    String[] splitted = date.split(":");

                    if (splitted.length != 2) {
                        error = true;
                        break;
                    }
                    if (!isNumber(splitted[0])) {
                        error = true;
                        break;
                    }

                    penaltyDate = unformatDate(penaltyDate, splitted, true);

                }

                if (error) {
                    getPlayer().sendMessage(Translator.ofString("date-error"));
                    return;
                }

                subscription.setPenaltyDate(penaltyDate);
                subscription.save();

                getPlayer().sendMessage(
                        SubscriptionUtil.applyPlaceholders(Translator.ofString("subscription-change"), subscription));

                HSubscription.getInstance().writeSubscription(getPlayer(), subscription);

                remove();
            }
            case "give" -> {

                LocalDateTime registeredDate = subscription == null ? LocalDateTime.now() : subscription.getRegisteredDate();
                LocalDateTime penaltyDate = subscription == null ? registeredDate : subscription.getPenaltyDate();

                List<String> formattedDate = new ArrayList<>(List.of(format.split(",")));

                boolean error = false;
                for (String date : formattedDate) {
                    String[] splitted = date.split(":");

                    if (splitted.length != 2) {
                        error = true;
                        break;
                    }
                    if (!isNumber(splitted[0])) {
                        error = true;
                        break;
                    }

                    penaltyDate = unformatDate(penaltyDate, splitted, true);

                }

                if (error) {
                    getPlayer().sendMessage(Translator.ofString("date-error"));
                    return;
                }

                subscription.setPenaltyDate(penaltyDate);
                subscription.save();

                getPlayer().sendMessage(SubscriptionUtil.applyPlaceholders(Translator.ofString("subscription-change"), subscription));

                HSubscription.getInstance().writeSubscription(getPlayer(), subscription);

                remove();
            }

            case "take" -> {

                LocalDateTime registeredDate = subscription == null ? LocalDateTime.now() : subscription.getRegisteredDate();
                LocalDateTime penaltyDate = subscription == null ? registeredDate : subscription.getPenaltyDate();

                List<String> formattedDate = new ArrayList<>(List.of(format.split(",")));

                boolean error = false;
                for (String date : formattedDate) {
                    String[] splitted = date.split(":");

                    if (splitted.length != 2) {
                        error = true;
                        break;
                    }
                    if (!isNumber(splitted[0])) {
                        error = true;
                        break;
                    }

                    penaltyDate = unformatDate(penaltyDate, splitted, false);

                }

                if (error) {
                    getPlayer().sendMessage(Translator.ofString("date-error"));
                    return;
                }

                subscription.setPenaltyDate(penaltyDate);
                subscription.save();

                getPlayer().sendMessage(SubscriptionUtil.applyPlaceholders(Translator.ofString("subscription-change"), subscription));

                HSubscription.getInstance().writeSubscription(getPlayer(), subscription);

                remove();
            }
        }

        AdminGUI.open(getPlayer());

    }

    public LocalDateTime unformatDate(LocalDateTime date, String[] splitted, boolean plus) {

        LocalDateTime penaltyDate = date;

        int num = Integer.parseInt(splitted[0]);
        String type = splitted[1];

        if (type.equalsIgnoreCase("year"))
            penaltyDate = plus ? penaltyDate.plusYears(num) : penaltyDate.minusYears(num);

        if (type.equalsIgnoreCase("mon"))
            penaltyDate = plus ? penaltyDate.plusMonths(num) : penaltyDate.minusMonths(num);

        if (type.equalsIgnoreCase("day"))
            penaltyDate = plus ? penaltyDate.plusDays(num) : penaltyDate.minusDays(num);

        if (type.equalsIgnoreCase("hour"))
            penaltyDate = plus ? penaltyDate.plusHours(num) : penaltyDate.minusHours(num);

        if (type.equalsIgnoreCase("min"))
            penaltyDate = plus ? penaltyDate.plusMinutes(num) : penaltyDate.minusMinutes(num);

        if (type.equalsIgnoreCase("sec"))
            penaltyDate = plus ? penaltyDate.plusSeconds(num) : penaltyDate.minusSeconds(num);

        return penaltyDate;
    }

    public boolean isNumber(String value) {
        try {
            int num = Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Subscription getSubscription() {
        return subscription;
    }

}
