package me.xflyiwnl.hsubscription.util;

import me.xflyiwnl.hsubscription.HSubscription;

import java.util.List;

public class Translator {

    public static String ofString(String path) {
        return TextUtil.colorize(HSubscription.getInstance().getFileManager().getLanguage().yaml()
                .getString("language." + path));
    }

    public static List<String> ofStringList(String path) {
        return TextUtil.colorize(HSubscription.getInstance().getFileManager().getLanguage().yaml()
                .getStringList("language." + path));
    }

}
