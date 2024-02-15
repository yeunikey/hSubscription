package me.xflyiwnl.hsubscription.database.sql;

import com.wiring.api.WiringAPI;
import com.wiring.api.entity.Database;
import com.wiring.api.entity.Table;
import com.wiring.api.entity.WiringResult;
import me.xflyiwnl.hsubscription.database.DataController;
import me.xflyiwnl.hsubscription.object.Subscription;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SQLSubscriptionController extends DataController<Subscription, String> {

    private WiringAPI api;
    private Database database;
    private Table table;

    public SQLSubscriptionController(WiringAPI api, Database database, Table table) {
        this.api = api;
        this.database = database;
        this.table = table;
    }

    @Override
    public List<Subscription> all() {
        List<Subscription> subscriptions = new ArrayList<>();

        List<WiringResult> results = api.selectAll(database)
                .table(table)
                .execute();

        for (WiringResult result : results) {
            Subscription subscription = new Subscription();

            subscription.setName(result.get("name").toString());
            subscription.setNotify(Boolean.getBoolean(result.get("notify").toString()));
            subscription.setRegisteredDate(LocalDateTime.parse(result.get("registeredDate").toString()));
            subscription.setPenaltyDate(LocalDateTime.parse(result.get("penaltyDate").toString()));

            subscriptions.add(subscription);
        }

        return subscriptions;
    }

    @Override
    public Subscription get(String id) {

        WiringResult result = api.select(database)
                .table(table)
                .value(id)
                .execute();

        if (result.getResult().isEmpty())
            return null;

        Subscription subscription = new Subscription();

        subscription.setName(result.get("name").toString());
        subscription.setNotify(Boolean.parseBoolean(result.get("notify").toString()));
        subscription.setRegisteredDate(LocalDateTime.parse(result.get("registeredDate").toString()));
        subscription.setPenaltyDate(LocalDateTime.parse(result.get("penaltyDate").toString()));

        return subscription;
    }

    @Override
    public void create(Subscription subscription) {
        save(subscription);
    }

    @Override
    public void save(Subscription subscription) {
        api.insert(database)
                .table(table)
                .column("name", subscription.getName())
                .column("registeredDate", subscription.getRegisteredDate().toString())
                .column("penaltyDate", subscription.getPenaltyDate().toString())
                .column("notify", subscription.isNotify())
                .execute();
    }

    @Override
    public void remove(Subscription subscription) {
        api.delete(database)
                .table(table)
                .value(subscription.getName())
                .execute();
    }
    
}
