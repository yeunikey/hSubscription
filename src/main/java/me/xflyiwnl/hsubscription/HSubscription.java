package me.xflyiwnl.hsubscription;

import me.xflyiwnl.colorfulgui.ColorfulGUI;
import me.xflyiwnl.hsubscription.action.Action;
import me.xflyiwnl.hsubscription.command.AdminCommand;
import me.xflyiwnl.hsubscription.command.SubscriptionCommand;
import me.xflyiwnl.hsubscription.database.DataSource;
import me.xflyiwnl.hsubscription.database.DataType;
import me.xflyiwnl.hsubscription.database.FlatDataSource;
import me.xflyiwnl.hsubscription.database.SQLDataSource;
import me.xflyiwnl.hsubscription.listener.PlayerListener;
import me.xflyiwnl.hsubscription.object.Subscription;
import me.xflyiwnl.hsubscription.task.SubscriptionTask;
import me.xflyiwnl.hsubscription.task.Task;
import me.xflyiwnl.hsubscription.util.Metrics;
import me.xflyiwnl.hsubscription.util.SubscriptionUtil;
import me.xflyiwnl.hsubscription.util.Updater;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class HSubscription extends JavaPlugin {

    private static HSubscription instance;

    private final FileManager fileManager = new FileManager();
    private final Task task = new SubscriptionTask();
    private final SubscriptionConfig config = new SubscriptionConfig();
    private DataSource dataSource;
    private final SubscriptionLogger logger = new SubscriptionLogger();
    private ColorfulGUI guiApi;
    private Metrics metrics;

    private Map<UUID, Long> cooldowns = new HashMap<>();
    private Map<UUID, Action> actions = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        registerListeners();
        registerCommands();

        fileManager.generate();
        config.setup();

        setupLogger();

        setupDatabase();
        dataSource.load();

        guiApi = new ColorfulGUI(this);
        task.startTask(config.getTaskTime() * 20);

        metrics = new Metrics(this, 21005);

        if (config.isUpdateCheck()) {
            Updater.check();
        }

    }

    @Override
    public void onDisable() {
        dataSource.unload();
    }

    public void setupDatabase() {
        if (config.getDataType() == DataType.FLAT) {
            dataSource = new FlatDataSource();
        } else if (config.getDataType() == DataType.SQL) {
            dataSource = new SQLDataSource();
        }
    }

    public void setupLogger() {
        if (config.isLoggerEnabled()) {
            logger.generate();
        }
    }

    public void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new PlayerListener(), this);
    }

    public void registerCommands() {
        getCommand("subscription").setExecutor(new SubscriptionCommand());
        getCommand("subscription").setTabCompleter(new SubscriptionCommand());

        getCommand("subscriptionadmin").setExecutor(new AdminCommand());
        getCommand("subscriptionadmin").setTabCompleter(new AdminCommand());
    }

    public void writeSubscription(CommandSender sender, Subscription subscription) {
        writeSubscription(sender, subscription, false);
    }

    public void writeSubscription(CommandSender sender, Subscription subscription, boolean delete) {
        if (!config.isLoggerEnabled()) return;
        if (delete) {
            logger.write(SubscriptionUtil.applyPlaceholders(config.getLoggerFormatDelete()
                            .replace("%sender%", sender instanceof Player ? sender.getName() : "CONSOLE"),
                    subscription));
        } else {
            logger.write(SubscriptionUtil.applyPlaceholders(config.getLoggerFormat()
                    .replace("%sender%", sender instanceof Player ? sender.getName() : "CONSOLE"),
                    subscription));
        }
    }

    public Subscription getSubscription(String name) {
        return dataSource.subscription().get(name);
    }

    public List<Subscription> getSubscriptions() {
        return dataSource.subscription().all();
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public Task getTask() {
        return task;
    }

    public ColorfulGUI getGuiApi() {
        return guiApi;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public SubscriptionConfig getSubscriptionConfig() {
        return config;
    }

    public SubscriptionLogger getSubscriptionLogger() {
        return logger;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public Map<UUID, Long> getCooldowns() {
        return cooldowns;
    }

    public Map<UUID, Action> getActions() {
        return actions;
    }


    public static HSubscription getInstance() {
        return instance;
    }

}
