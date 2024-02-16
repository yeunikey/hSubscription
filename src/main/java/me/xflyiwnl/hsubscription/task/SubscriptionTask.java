package me.xflyiwnl.hsubscription.task;

import me.xflyiwnl.hsubscription.HSubscription;
import me.xflyiwnl.hsubscription.object.Subscription;
import me.xflyiwnl.hsubscription.util.SubscriptionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class SubscriptionTask extends Task {

    public SubscriptionTask() {
    }

    @Override
    public void startTask(long time) {
        this.runTaskTimer(HSubscription.getInstance(), 0, time);
    }

    @Override
    public void run() {
        List<Subscription> subscriptions = HSubscription.getInstance().getSubscriptions();

        for (Subscription subscription : subscriptions) {
            long duration = Duration.between(LocalDateTime.now(), subscription.getPenaltyDate()).toSeconds();
            if (duration > 0) continue;

            Player player = Bukkit.getPlayer(subscription.getName());
            SubscriptionUtil.kickPlayer(player);

            subscription.remove();

        }

    }

}
