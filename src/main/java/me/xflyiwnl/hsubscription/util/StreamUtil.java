package me.xflyiwnl.hsubscription.util;

import me.xflyiwnl.hsubscription.HSubscription;
import org.bukkit.Bukkit;

public class StreamUtil {

    public static void runAsync(Runnable code) {
        Bukkit.getScheduler().runTaskAsynchronously(HSubscription.getInstance(), code);
    }

    public static void runSync(Runnable code) {
        Bukkit.getScheduler().runTask(HSubscription.getInstance(), code);
    }

}
