package me.xflyiwnl.hsubscription.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xflyiwnl.hsubscription.HSubscription;
import me.xflyiwnl.hsubscription.object.Subscription;
import me.xflyiwnl.hsubscription.util.StreamUtil;
import me.xflyiwnl.hsubscription.util.SubscriptionUtil;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class SubscriptionExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "hsubscription";
    }

    @Override
    public @NotNull String getAuthor() {
        return "xflyiwnl";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.1";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {

        if (params.equals("time")) {
            Subscription subscription = HSubscription.getInstance().getSubscription(player.getName());

            if (subscription == null) {
                return "";
            }

            return SubscriptionUtil.formatTime(subscription);
        }

        return "NOT FOUND";
    }

}
