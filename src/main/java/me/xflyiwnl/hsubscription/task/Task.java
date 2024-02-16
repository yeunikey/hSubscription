package me.xflyiwnl.hsubscription.task;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class Task extends BukkitRunnable {

    public abstract void startTask(long time);

    @Override
    public void run() {

    }

}
