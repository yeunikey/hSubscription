package me.xflyiwnl.hsubscription.database;

import me.xflyiwnl.hsubscription.object.SubscriptionObject;

import java.util.List;

public abstract class DataController<T extends SubscriptionObject, K> {

    public abstract List<T> all();
    public abstract T get(K id);

    public abstract void create(T t);
    public abstract void save(T t);

    public abstract void remove(T t);

}
