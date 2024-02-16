package me.xflyiwnl.hsubscription;

import me.xflyiwnl.hsubscription.config.YAML;
import me.xflyiwnl.hsubscription.util.Settinger;
import me.xflyiwnl.hsubscription.util.Translator;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileManager {

    private YAML settings, language, subscriptionGUI, adminGUI;

    private List<String> allLanguages = Arrays.asList(
            "ru_RU",
            "en_EN",
            "kz_KZ"
    );

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

        String selectedLang = Settinger.ofString("language");

        allLanguages.forEach(lang -> {

            if (lang.equals(selectedLang)) {
                subscriptionGUI = new YAML("gui/" + lang + File.separator + Settinger.ofString("gui.subscription"));
                adminGUI = new YAML("gui/" + lang + File.separator + Settinger.ofString("gui.admin"));
            } else {
                new YAML("gui/" + lang + File.separator + Settinger.ofString("gui.subscription"));
                new YAML("gui/" + lang + File.separator + Settinger.ofString("gui.admin"));
            }
        });

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
        allLanguages.forEach(key -> {
            new YAML("language/" + key + ".yml");
        });
    }

    public void checkLanguage() {

        HSubscription instance = HSubscription.getInstance();

        String selectedLanguage = Settinger.ofString("language");

        File dataFolder = instance.getDataFolder();
        File languageFile = new File(dataFolder, "language/" + selectedLanguage + ".yml");

        if (languageFile.exists()) {
            language = new YAML(languageFile, YamlConfiguration.loadConfiguration(languageFile));

            System.out.println(Translator.ofString("selected-language")
                    .replace("%language%", selectedLanguage));
            return;
        }

        selectedLanguage = SubscriptionConfig.getInstance().getDefaultLanguage();
        language = new YAML("language/" + selectedLanguage + ".yml");

        System.out.println(Translator.ofString("selected-language")
                .replace("%language%", selectedLanguage));

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
