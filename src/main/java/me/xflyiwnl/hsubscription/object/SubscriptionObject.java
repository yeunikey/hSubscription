package me.xflyiwnl.hsubscription.object;

public abstract class SubscriptionObject implements Nameable {

    private String name;

    public SubscriptionObject() {
    }

    public SubscriptionObject(String name) {
        this.name = name;
    }

    public abstract void create(boolean save);
    public abstract void save();
    public abstract void remove();

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
