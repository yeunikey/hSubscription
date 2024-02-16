package me.xflyiwnl.hsubscription.database;

import me.xflyiwnl.hsubscription.HSubscription;
import me.xflyiwnl.hsubscription.database.flat.FlatSubscriptionController;
import me.xflyiwnl.hsubscription.object.Subscription;
import me.xflyiwnl.hsubscription.util.Translator;

import java.util.HashMap;
import java.util.Map;

public class FlatDataSource implements DataSource {

    private final FlatSubscriptionController subscriptionController =
            new FlatSubscriptionController(this);
    private final Map<String, Subscription> subscriptions = new HashMap<String, Subscription>();

    @Override
    public void load() {
        System.out.println(Translator.ofString("source-type")
                .replace("%type%", "FLAT"));

        HSubscription.getInstance().getFileManager().createFolder("database");
        subscriptionController.allFromFiles().forEach(subscription -> {
            subscriptions.put(subscription.getName(), subscription);
        });
    }

    @Override
    public void unload() {
        subscriptions.forEach((name, subscription) -> {
            subscriptionController.save(subscription);
        });
    }

    @Override
    public DataController<Subscription, String> subscription() {
        return subscriptionController;
    }

    public Map<String, Subscription> getSubscriptions() {
        return subscriptions;
    }

}
