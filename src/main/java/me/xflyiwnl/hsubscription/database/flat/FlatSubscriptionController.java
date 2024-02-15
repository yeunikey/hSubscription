package me.xflyiwnl.hsubscription.database.flat;

import me.xflyiwnl.hsubscription.HSubscription;
import me.xflyiwnl.hsubscription.database.DataController;
import me.xflyiwnl.hsubscription.database.FlatDataSource;
import me.xflyiwnl.hsubscription.object.Subscription;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlatSubscriptionController extends DataController<Subscription, String> {

    private final FlatDataSource dataSource;

    public FlatSubscriptionController(FlatDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Subscription> all() {
        return new ArrayList<Subscription>(dataSource.getSubscriptions().values());
    }

    public List<Subscription> allFromFiles() {
        List<Subscription> result = new ArrayList<Subscription>();

        File folder = new File(HSubscription.getInstance().getDataFolder(), "database");
        if (!folder.exists()) return result;
        for (File file : folder.listFiles()) {
            if (file.isDirectory() && !file.isFile()) continue;
            Subscription subscription = getFromFile(file);
            result.add(subscription);
        }

        return result;
    }

    @Override
    public Subscription get(String id) {
        return dataSource.getSubscriptions().get(id);
    }

    public Subscription getFromFile(File file) {

        FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        Subscription subscription = new Subscription();

        if (yaml.contains("name")) {
            subscription.setName(yaml.getString("name"));
        }

        if (yaml.contains("registeredDate")) {
            subscription.setRegisteredDate(LocalDateTime.parse(yaml.getString("registeredDate")));
        }

        if (yaml.contains("penaltyDate")) {
            subscription.setPenaltyDate(LocalDateTime.parse(yaml.getString("penaltyDate")));
        }

        if (yaml.contains("notify")) {
            subscription.setNotify(yaml.getBoolean("notify"));
        }

        return subscription;
    }

    @Override
    public void create(Subscription subscription) {
        dataSource.getSubscriptions().put(subscription.getName(), subscription);
    }

    @Override
    public void save(Subscription subscription) {
        File file = new File(HSubscription.getInstance().getDataFolder(),
                "database/" + subscription.getName() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        yaml.set("name", subscription.getName());
        if (subscription.getRegisteredDate() != null)
            yaml.set("registeredDate", subscription.getRegisteredDate().toString());
        if (subscription.getPenaltyDate() != null)
            yaml.set("penaltyDate", subscription.getPenaltyDate().toString());
        yaml.set("notify", subscription.isNotify());

        try {
            yaml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void remove(Subscription subscription) {
        dataSource.getSubscriptions().remove(subscription.getName());
        File file = new File(HSubscription.getInstance().getDataFolder(),
                "database/" + subscription.getName() + ".yml");
        if (!file.exists()) return;
        file.delete();
    }

}
