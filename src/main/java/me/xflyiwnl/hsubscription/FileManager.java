package me.xflyiwnl.hsubscription;

import me.xflyiwnl.hsubscription.config.YAML;
import me.xflyiwnl.hsubscription.util.Settinger;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileManager {

    private YAML settings, language, subscriptionGUI, adminGUI;

    public FileManager() {
    }

    public void generate() {
        createFolders();

        settings = new YAML("settings.yml");

        generateGUI();
        generateLanguages();
        checkLanguage();
    }

    public void generateGUI() {
        subscriptionGUI = new YAML("gui/" + Settinger.ofString("gui.subscription"));
        adminGUI = new YAML("gui/" + Settinger.ofString("gui.admin"));
    }

    public void createFolder(String folder) {
        File file = new File(HSubscription.getInstance().getDataFolder(), folder);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void createFolders() {
        List<String> folders = Arrays.asList(
                "gui",
                "language"
        );
        folders.forEach(this::createFolder);
    }

    public void generateLanguages() {
        List<String> allLanguages = Arrays.asList(
                "ru_RU"
        );
        allLanguages.forEach(key -> {
            new YAML("language/" + key + ".yml");
        });
    }

    public void checkLanguage() {

        HSubscription instance = HSubscription.getInstance();

        File dataFolder = instance.getDataFolder();
        File languageFile = new File(dataFolder, "language/" + Settinger.ofString("language") + ".yml");

        if (languageFile.exists()) {
            language = new YAML(languageFile, YamlConfiguration.loadConfiguration(languageFile));
            return;
        }

        String defaultLanguage = SubscriptionConfig.getInstance().getDefaultLanguage();
        language = new YAML("language/" + defaultLanguage + ".yml");

    }

    public YAML getSettings() {
        return settings;
    }

    public YAML getLanguage() {
        return language;
    }

    public YAML getSubscriptionGUI() {
        return subscriptionGUI;
    }

    public YAML getAdminGUI() {
        return adminGUI;
    }
}
