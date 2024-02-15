package me.xflyiwnl.hsubscription.action;

import me.xflyiwnl.hsubscription.object.Subscription;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public interface Action {

    void start();
    void onChat(PlayerChatEvent event);

    void remove();

    Player getPlayer();
    Subscription getSubscription();

}
