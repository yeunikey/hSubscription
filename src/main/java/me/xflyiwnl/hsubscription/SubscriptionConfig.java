package me.xflyiwnl.hsubscription;

import me.xflyiwnl.hsubscription.database.DataType;
import me.xflyiwnl.hsubscription.util.Settinger;

public class SubscriptionConfig {

    private static SubscriptionConfig instance;

    private String defaultLanguage = "ru_RU";
    private String language = defaultLanguage;
    private long taskTime = 60;
    private DataType dataType = DataType.FLAT;
    private String server;

    private boolean loggerEnabled = true;
    private String loggerFormat = "%date% / %sender% changed %name%'s subscription to %penaltyDate%";
    private String loggerFormatDelete = "%date% / %sender% deleted %name%'s subscription";
    private String loggerPath = "logger.txt";
    private String database = "database";
    private String table = "table";
    private boolean cooldowns = true;
    private int cooldownTime = 3;
    private boolean updateCheck = true;

    public SubscriptionConfig() {
    }

    public void setup() {
        instance = this;

        language = Settinger.ofString("language");
        taskTime = Settinger.ofInt("task.time");
        dataType = DataType.valueOf(Settinger.ofString("database.type").toUpperCase());
        server = Settinger.ofString("editor.server");
        loggerEnabled = Settinger.ofBoolean("logger.enabled");
        loggerFormat = Settinger.ofString("logger.format");
        loggerFormatDelete = Settinger.ofString("logger.format-delete");
        loggerPath = Settinger.ofString("logger.path");
        database = Settinger.ofString("database.database");
        table = Settinger.ofString("database.table");
        cooldowns = Settinger.ofBoolean("cooldown.enabled");
        cooldownTime = Settinger.ofInt("cooldown.time");
        updateCheck = Settinger.ofBoolean("update-check");
    }
    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public long getTaskTime() {
        return taskTime;
    }

    public DataType getDataType() {
        return dataType;
    }

    public String getServer() {
        return server;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isLoggerEnabled() {
        return loggerEnabled;
    }

    public String getLoggerFormat() {
        return loggerFormat;
    }

    public String getLoggerFormatDelete() {
        return loggerFormatDelete;
    }

    public String getLoggerPath() {
        return loggerPath;
    }

    public String getDatabase() {
        return database;
    }

    public String getTable() {
        return table;
    }

    public boolean isCooldowns() {
        return cooldowns;
    }

    public int getCooldownTime() {
        return cooldownTime;
    }

    public boolean isUpdateCheck() {
        return updateCheck;
    }

    public static SubscriptionConfig getInstance() {
        return instance;
    }

}
