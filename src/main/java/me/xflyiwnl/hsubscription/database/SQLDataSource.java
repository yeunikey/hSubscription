package me.xflyiwnl.hsubscription.database;

import com.wiring.api.WiringAPI;
import com.wiring.api.entity.Column;
import com.wiring.api.entity.ColumnType;
import com.wiring.api.entity.Database;
import com.wiring.api.entity.Table;
import me.xflyiwnl.hsubscription.SubscriptionConfig;
import me.xflyiwnl.hsubscription.database.sql.SQLSubscriptionController;
import me.xflyiwnl.hsubscription.object.Subscription;
import me.xflyiwnl.hsubscription.util.Settinger;
import me.xflyiwnl.hsubscription.util.Translator;

public class SQLDataSource implements DataSource {

    private SQLSubscriptionController subscriptionController;

    private WiringAPI api;

    @Override
    public void load() {

        System.out.println(Translator.ofString("source-type")
                .replace("%type%", "SQL"));

        try {
            api = new WiringAPI(
                    Settinger.ofString("database.driver"),
                    Settinger.ofString("database.host"),
                    Settinger.ofInt("database.port"),
                    Settinger.ofString("database.username"),
                    Settinger.ofString("database.password"),
                    null
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SubscriptionConfig config = SubscriptionConfig.getInstance();

        if (!api.existsDatabase(config.getDatabase())) {
            api.createDatabase(config.getDatabase());
        }

        Database database = api.getDatabase(config.getDatabase());
        Table table = null;

        if (!database.existsTable(config.getTable())) {
            table = database.createTable(config.getTable())
                    .column(new Column("name", ColumnType.VARCHAR).notNull().primaryKey())
                    .column(new Column("registeredDate", ColumnType.VARCHAR).notNull())
                    .column(new Column("penaltyDate", ColumnType.VARCHAR).notNull())
                    .column(new Column("notify", ColumnType.VARCHAR).notNull())
                    .execute();
        } else {
            table = database.getTable(config.getTable());
        }

        subscriptionController = new SQLSubscriptionController(api, database, table);

        System.out.println(Translator.ofString("sql-created"));
    }

    @Override
    public void unload() {
        api.close();
    }

    @Override
    public DataController<Subscription, String> subscription() {
        return subscriptionController;
    }
}
