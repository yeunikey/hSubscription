package me.xflyiwnl.hsubscription.object;

import me.xflyiwnl.hsubscription.HSubscription;

import java.time.LocalDateTime;

public class Subscription extends SubscriptionObject {

    private LocalDateTime registeredDate;
    private LocalDateTime penaltyDate;

    private boolean notify = true;

    public Subscription() {
    }

    public Subscription(String name, LocalDateTime registeredDate, LocalDateTime penaltyDate) {
        super(name);
        this.registeredDate = registeredDate;
        this.penaltyDate = penaltyDate;
    }

    public Subscription(String name, LocalDateTime registeredDate, LocalDateTime penaltyDate, boolean notify) {
        super(name);
        this.registeredDate = registeredDate;
        this.penaltyDate = penaltyDate;
        this.notify = notify;
    }

    @Override
    public void create(boolean save) {
        HSubscription.getInstance().getDataSource().subscription().create(this);
        if (save) save();
    }

    @Override
    public void save() {
        HSubscription.getInstance().getDataSource().subscription().save(this);
    }

    @Override
    public void remove() {
        HSubscription.getInstance().getDataSource().subscription().remove(this);
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "name=" + getName() +
                ", registeredDate=" + registeredDate +
                ", penaltyDate=" + penaltyDate +
                ", notify=" + notify +
                '}';
    }

    public LocalDateTime getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(LocalDateTime registeredDate) {
        this.registeredDate = registeredDate;
    }

    public LocalDateTime getPenaltyDate() {
        return penaltyDate;
    }

    public void setPenaltyDate(LocalDateTime penaltyDate) {
        this.penaltyDate = penaltyDate;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }
}
