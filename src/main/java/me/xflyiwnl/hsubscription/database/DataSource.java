package me.xflyiwnl.hsubscription.database;

import me.xflyiwnl.hsubscription.object.Subscription;

public interface DataSource {

    void load();
    void unload();

    DataController<Subscription, String> subscription();

}
