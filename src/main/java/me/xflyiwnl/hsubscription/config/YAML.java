package me.xflyiwnl.hsubscription.config;

import me.xflyiwnl.hsubscription.HSubscription;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YAML {

    private File file;
    private FileConfiguration yaml;

    public YAML(String name) {
        init(name);
    }

    public YAML(File file, FileConfiguration yaml) {
        this.file = file;
        this.yaml = yaml;
    }

    public void init(String name) {

        file = new File(HSubscription.getInstance().getDataFolder(), name);
        if (!file.exists()) {
            HSubscription.getInstance().saveResource(name, true);
        }
        yaml = YamlConfiguration.loadConfiguration(file);

    }

    public void save() {
        try {
            yaml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File file() {
        return file;
    }

    public FileConfiguration yaml() {
        return yaml;
    }

}
