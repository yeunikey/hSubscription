package me.xflyiwnl.hsubscription;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SubscriptionLogger {

    private File file;

    public SubscriptionLogger() {
    }

    public void generate() {
        file = new File(HSubscription.getInstance().getDataFolder(), SubscriptionConfig.getInstance().getLoggerPath());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void write(String value) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(value);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}