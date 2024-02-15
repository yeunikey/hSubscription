package me.xflyiwnl.hsubscription.command;

import me.xflyiwnl.hsubscription.HSubscription;
import me.xflyiwnl.hsubscription.SubscriptionConfig;
import me.xflyiwnl.hsubscription.gui.AdminGUI;
import me.xflyiwnl.hsubscription.object.Subscription;
import me.xflyiwnl.hsubscription.util.EditorUtil;
import me.xflyiwnl.hsubscription.util.SubscriptionUtil;
import me.xflyiwnl.hsubscription.util.Translator;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AdminCommand implements TabCompleter, CommandExecutor {

    private List<String> subCommands = Arrays.asList(
            "editor",
            "applyedits",
            "give",
            "take",
            "set",
            "reload",
            "remove",
            "gui"
    );

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("hsubscription.admin")) {
            return null;
        }

        switch (args.length) {
            case 1 -> {
                return subCommands;
            }
            case 2 -> {
                switch (args[0].toLowerCase()) {
                    case "applyedits" -> {
                        return List.of("key");
                    }
                    case "give", "take", "set", "remove" -> {
                        return Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .collect(Collectors.toList());
                    }
                    case "editor" -> {
                        return null;
                    }
                }
            }
            case 3 -> {
                switch (args[0].toLowerCase()) {
                    case "give", "take", "set" -> {
                        List<String> format = Arrays.asList(
                                "year",
                                "mon",
                                "day",
                                "hour",
                                "min",
                                "sec"
                        );
                        String arg = args[2];

                        if (arg.isEmpty()) {
                            return format.stream()
                                    .map(s -> "?" + ":" + s)
                                    .collect(Collectors.toList());
                        } else if (arg.endsWith(":")) {
                            return format.stream()
                                    .map(s -> arg + s)
                                    .collect(Collectors.toList());
                        } else if (arg.endsWith(",")) {
                            return format.stream()
                                    .map(s -> arg + "?:" + s)
                                    .collect(Collectors.toList());
                        } else if (isNumber(String.valueOf(arg.toCharArray()[arg.toCharArray().length - 1]))) {
                            return format.stream()
                                    .map(s -> arg + ":" + s)
                                    .collect(Collectors.toList());
                        } else {
                            String[] splitted = arg.split(":");
                            String lastArg = splitted[splitted.length - 1];
                            List<String> result = format.stream()
                                    .filter(s -> s.startsWith(lastArg))
                                    .collect(Collectors.toList());
                            return result.stream()
                                    .map(s -> arg + (s.replace(lastArg, "")))
                                    .collect(Collectors.toList());
                        }

                    }
                }
            }
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        parseCommand(sender, args);
        return true;
    }

    public void parseCommand(CommandSender sender, String[] args) {

        if (!sender.hasPermission("hsubscription.admin")) {
            sender.sendMessage(Translator.ofString("no-permission"));
            return;
        }

        if (args.length == 0) {
            Translator.ofStringList("admin-command-args").forEach(sender::sendMessage);
            return;
        }

        HSubscription instance = HSubscription.getInstance();
        SubscriptionConfig config = SubscriptionConfig.getInstance();

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!instance.getCooldowns().containsKey(player.getUniqueId())) {
                instance.getCooldowns().put(player.getUniqueId(), System.currentTimeMillis());
            } else {
                long time = instance.getCooldowns().get(player.getUniqueId());
                if ((System.currentTimeMillis() - time) / 1000 < config.getCooldownTime()) {
                    sender.sendMessage(Translator.ofString("cooldown-message"));
                    return;
                } else {
                    instance.getCooldowns().remove(player.getUniqueId());
                    instance.getCooldowns().put(player.getUniqueId(), System.currentTimeMillis());
                }
            }
        }

        switch (args[0].toLowerCase()) {
            case "applyedits" -> {

                if (!sender.hasPermission("hsubscription.admin.editor")) {
                    sender.sendMessage(Translator.ofString("no-permission"));
                    return;
                }

                if (args.length < 2) {
                    sender.sendMessage(Translator.ofString("args-error"));
                    return;
                }

                EditorUtil.apply(sender, args[1]);

            }
            case "editor" -> {

                if (!sender.hasPermission("hsubscription.admin.editor")) {
                    sender.sendMessage(Translator.ofString("no-permission"));
                    return;
                }

                EditorUtil.generate(sender);

            }
            case "gui" -> {

                if (!(sender instanceof Player)) {
                    sender.sendMessage(Translator.ofString("console-error"));
                    return;
                }

                if (!sender.hasPermission("hsubscription.admin.gui")) {
                    sender.sendMessage(Translator.ofString("no-permission"));
                    return;
                }

                AdminGUI.open(((Player) sender));

            }
            case "reload" -> {

                if (!sender.hasPermission("hsubscription.admin.reload")) {
                    sender.sendMessage(Translator.ofString("no-permission"));
                    return;
                }

                instance.getFileManager().generate();
                instance.getSubscriptionConfig().setup();

                sender.sendMessage(Translator.ofString("reloaded"));
            }
            case "remove" -> {

                if (!sender.hasPermission("hsubscription.admin.remove")) {
                    sender.sendMessage(Translator.ofString("no-permission"));
                    return;
                }

                if (args.length < 2) {
                    sender.sendMessage(Translator.ofString("args-error"));
                    return;
                }

                Subscription subscription = HSubscription.getInstance().getSubscription(args[1]);

                if (subscription == null) {
                    sender.sendMessage(Translator.ofString("no-subscription"));
                    return;
                }

                sender.sendMessage(Translator.ofString("subscription-remove")
                        .replace("%name%", subscription.getName()));

                HSubscription.getInstance().writeSubscription(sender, subscription, true);
                subscription.remove();

                Player player = Bukkit.getPlayer(subscription.getName());
                SubscriptionUtil.kickPlayer(player);

            }
            case "set" -> {

                if (!sender.hasPermission("hsubscription.admin.set")) {
                    sender.sendMessage(Translator.ofString("no-permission"));
                    return;
                }

                if (args.length < 3) {
                    sender.sendMessage(Translator.ofString("args-error"));
                    return;
                }

                Subscription subscription = HSubscription.getInstance().getSubscription(args[1]);

                LocalDateTime registeredDate = LocalDateTime.now();
                LocalDateTime penaltyDate = registeredDate;

                List<String> formattedDate = new ArrayList<>(List.of(args[2].split(",")));

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
                    sender.sendMessage(Translator.ofString("date-error"));
                    return;
                }

                if (subscription == null) {
                    subscription = new Subscription(
                            args[1],
                            registeredDate,
                            penaltyDate
                    );
                    subscription.create(true);
                } else {
                    subscription.setPenaltyDate(penaltyDate);
                    subscription.save();
                }

                sender.sendMessage(
                        SubscriptionUtil.applyPlaceholders(Translator.ofString("subscription-change"), subscription));

                HSubscription.getInstance().writeSubscription(sender, subscription);

            }
            case "give" -> {

                if (!sender.hasPermission("hsubscription.admin.give")) {
                    sender.sendMessage(Translator.ofString("no-permission"));
                    return;
                }

                if (args.length < 3) {
                    sender.sendMessage(Translator.ofString("args-error"));
                    return;
                }

                Subscription subscription = HSubscription.getInstance().getSubscription(args[1]);

                LocalDateTime registeredDate = subscription == null ? LocalDateTime.now() : subscription.getRegisteredDate();
                LocalDateTime penaltyDate = subscription == null ? registeredDate : subscription.getPenaltyDate();

                List<String> formattedDate = new ArrayList<>(List.of(args[2].split(",")));

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
                    sender.sendMessage(Translator.ofString("date-error"));
                    return;
                }

                if (subscription == null) {
                    subscription = new Subscription(
                            args[1],
                            registeredDate,
                            penaltyDate
                    );
                    subscription.create(true);
                } else {
                    subscription.setPenaltyDate(penaltyDate);
                    subscription.save();
                }

                sender.sendMessage(SubscriptionUtil.applyPlaceholders(Translator.ofString("subscription-change"), subscription));

                HSubscription.getInstance().writeSubscription(sender, subscription);

            }
            case "take" -> {

                if (!sender.hasPermission("hsubscription.admin.take")) {
                    sender.sendMessage(Translator.ofString("no-permission"));
                    return;
                }

                if (args.length < 3) {
                    sender.sendMessage(Translator.ofString("args-error"));
                    return;
                }

                Subscription subscription = HSubscription.getInstance().getSubscription(args[1]);

                LocalDateTime registeredDate = subscription == null ? LocalDateTime.now() : subscription.getRegisteredDate();
                LocalDateTime penaltyDate = subscription == null ? registeredDate : subscription.getPenaltyDate();

                List<String> formattedDate = new ArrayList<>(List.of(args[2].split(",")));

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
                    sender.sendMessage(Translator.ofString("date-error"));
                    return;
                }

                if (subscription == null) {
                    subscription = new Subscription(
                            args[1],
                            registeredDate,
                            penaltyDate
                    );
                    subscription.create(true);
                } else {
                    subscription.setPenaltyDate(penaltyDate);
                    subscription.save();
                }

                sender.sendMessage(SubscriptionUtil.applyPlaceholders(Translator.ofString("subscription-change"), subscription));

                HSubscription.getInstance().writeSubscription(sender, subscription);

            }
            default -> {
                Translator.ofStringList("admin-command-args").forEach(sender::sendMessage);
            }
        }

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

}
