package me.xflyiwnl.hsubscription.util;

import me.xflyiwnl.hsubscription.HSubscription;

import java.util.List;

public class Settinger {

    public static String ofString(String path) {
        return HSubscription.getInstance().getFileManager().getSettings().yaml()
                .getString("settings." + path);
    }

    public static List<String> ofStringList(String path) {
        return HSubscription.getInstance().getFileManager().getSettings().yaml()
                .getStringList("settings." + path);
    }

    public static int ofInt(String path) {
        return HSubscription.getInstance().getFileManager().getSettings().yaml()
                .getInt("settings." + path);
    }

    public static Double ofDouble(String path) {
        return HSubscription.getInstance().getFileManager().getSettings().yaml()
                .getDouble("settings." + path);
    }

    public static boolean ofBoolean(String path) {
        return HSubscription.getInstance().getFileManager().getSettings().yaml()
                .getBoolean("settings." + path);
    }

}
