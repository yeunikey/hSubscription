package me.xflyiwnl.hsubscription.command;

import me.xflyiwnl.hsubscription.HSubscription;
import me.xflyiwnl.hsubscription.SubscriptionConfig;
import me.xflyiwnl.hsubscription.gui.SubscriptionGUI;
import me.xflyiwnl.hsubscription.object.Subscription;
import me.xflyiwnl.hsubscription.util.SubscriptionUtil;
import me.xflyiwnl.hsubscription.util.Translator;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionCommand implements TabCompleter, CommandExecutor {

    private List<String> subCommands = Arrays.asList(
            "show",
            "notify"
    );

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            return subCommands;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("show")) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(HumanEntity::getName)
                        .collect(Collectors.toCollection(ArrayList::new));
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

        if (args.length == 0) {
            if (sender instanceof Player) {
                showSubscription((Player) sender);
            } else {
                Translator.ofStringList("command-args").forEach(sender::sendMessage);
            }
            return;
        }

        HSubscription instance = HSubscription.getInstance();
        SubscriptionConfig config = SubscriptionConfig.getInstance();

        if (sender instanceof Player && !sender.isOp() && config.isCooldowns()) {
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
            case "show" -> {
                if (args.length == 1) {
                    if (sender instanceof Player) {
                        showSubscription((Player) sender);
                    } else {
                        Translator.ofStringList("command-args").forEach(sender::sendMessage);
                    }
                    return;
                }

                if (!sender.hasPermission("hsubscription.show")) {
                    sender.sendMessage(Translator.ofString("no-permission"));
                    return;
                }

                String showName = args[1];
                Subscription subscription = HSubscription.getInstance().getSubscription(showName);

                showSubscription(sender, subscription);
            }
            case "notify" -> {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Translator.ofString("console-error"));
                    return;
                }

                Subscription subscription = HSubscription.getInstance().getSubscription(sender.getName());

                if (subscription == null) {
                    sender.sendMessage(Translator.ofString("no-subscription"));
                    return;
                }

                subscription.setNotify(!subscription.isNotify());
                subscription.save();

                if (subscription.isNotify()) {
                    sender.sendMessage(Translator.ofString("notify-change-on"));
                } else {
                    sender.sendMessage(Translator.ofString("notify-change-off"));
                }

            }
            default -> {
                if (sender instanceof Player) {
                    showSubscription((Player) sender);
                } else {
                    Translator.ofStringList("command-args").forEach(sender::sendMessage);
                }
            }
        }

    }

    public void showSubscription(CommandSender sender, Subscription subscription) {

        if (subscription == null) {
            sender.sendMessage(Translator.ofString("no-subscription"));
            return;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            showSubscription(player, subscription);
            return;
        }

        Translator.ofStringList("console-show").forEach(msg -> {
            sender.sendMessage(SubscriptionUtil.applyPlaceholders(
                    msg.replace("%date%", SubscriptionUtil.formatTime(subscription)), subscription));
        });

    }

    public void showSubscription(Player player, Subscription subscription) {

        if (subscription == null) {
            player.sendMessage(Translator.ofString("no-subscription"));
            return;
        }

        SubscriptionGUI.open(player, subscription);

    }

    public void showSubscription(Player player) {
        showSubscription(player, HSubscription.getInstance().getSubscription(player.getName()));
    }

}
