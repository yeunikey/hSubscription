package me.xflyiwnl.hsubscription.listener;

import me.xflyiwnl.hsubscription.HSubscription;
import me.xflyiwnl.hsubscription.object.Subscription;
import me.xflyiwnl.hsubscription.util.SubscriptionUtil;
import me.xflyiwnl.hsubscription.util.Translator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerListener implements Listener {

    @EventHandler
    public void onLoad(AsyncPlayerPreLoginEvent event) {

        String name = event.getName();
        Subscription subscription = HSubscription.getInstance().getSubscription(name);

        if (subscription == null) {
            if (Bukkit.getOfflinePlayer(event.getUniqueId()) != null &&
                    Bukkit.getOfflinePlayer(event.getUniqueId()).isOp()) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            List<String> messages = Translator.ofStringList("kick-message");
            for (int i = 0; i < messages.size(); i++) {
                sb.append(i != 0 ? "\n" : "").append(messages.get(i));
            }

            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, sb.toString());
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        Subscription subscription = HSubscription.getInstance().getSubscription(player.getName());

        if (subscription == null || !subscription.isNotify()) {
            return;
        }

        Translator.ofStringList("notification-message").stream()
                .map(s -> SubscriptionUtil.applyPlaceholders(
                        s, subscription
                        ).replace("%date%", SubscriptionUtil.formatTime(subscription)))
                .collect(Collectors.toList())
                .forEach(player::sendMessage);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        HSubscription instance = HSubscription.getInstance();

        if (instance.getCooldowns().containsKey(player.getUniqueId())) {
            instance.getCooldowns().remove(player.getUniqueId());
        }

        if (instance.getActions().containsKey(player.getUniqueId())) {
            instance.getActions().remove(player.getUniqueId());
        }

    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {

        Player player = event.getPlayer();
        HSubscription instance = HSubscription.getInstance();

        if (instance.getActions().containsKey(player.getUniqueId())) {
            instance.getActions().get(player.getUniqueId()).onChat(event);
        }
    }

}
