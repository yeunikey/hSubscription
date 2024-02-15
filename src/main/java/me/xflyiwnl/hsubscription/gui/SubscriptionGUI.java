package me.xflyiwnl.hsubscription.gui;

import me.xflyiwnl.colorfulgui.ColorfulGUI;
import me.xflyiwnl.colorfulgui.object.DynamicItem;
import me.xflyiwnl.colorfulgui.object.Gui;
import me.xflyiwnl.colorfulgui.provider.ColorfulProvider;
import me.xflyiwnl.hsubscription.HSubscription;
import me.xflyiwnl.hsubscription.object.Subscription;
import me.xflyiwnl.hsubscription.util.SubscriptionUtil;
import me.xflyiwnl.hsubscription.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionGUI extends ColorfulProvider<Gui> {

    private final Subscription subscription;

    public SubscriptionGUI(Player player, Subscription subscription) {
        super(player, 1);
        this.subscription = subscription;
    }

    @Override
    public void init() {
        ColorfulGUI gui = HSubscription.getInstance().getGuiApi();
        FileConfiguration yaml = HSubscription.getInstance().getFileManager().getSubscriptionGUI().yaml();

        if (!yaml.isConfigurationSection("items")) {
            return;
        }

        for (String section : yaml.getConfigurationSection("items").getKeys(false)) {
            String path = "items." + section;

            Material material = Material.valueOf(yaml.getString(path + ".material").toUpperCase());
            int amount = yaml.getInt(path + ".amount");

            String name = yaml.getString(path + ".name");
            List<String> lore = yaml.getStringList(path + ".lore");

            List<String> action = yaml.getStringList(path + ".action");

            DynamicItem staticItem = gui.dynamicItem()
                    .material(material)
                    .amount(amount)
                    .name(SubscriptionUtil
                            .applyPlaceholders(name, subscription)
                            .replace("%date%", SubscriptionUtil.formatTime(subscription)))
                    .lore(SubscriptionUtil
                            .applyPlaceholders(lore, subscription)
                            .stream()
                            .map(s -> s.replace("%date%", SubscriptionUtil.formatTime(subscription)))
                            .collect(Collectors.toList()))
                    .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS)
                    .action(event -> {
                        action.forEach(a -> {
                            if (a.startsWith("[msg]:")) {
                                getPlayer().sendMessage(TextUtil.colorize(SubscriptionUtil
                                                .applyPlaceholders(a.replace("[msg]:", ""), subscription)
                                                .replace("%date%", SubscriptionUtil.formatTime(subscription)))
                                );
                            } else if (a.startsWith("[cmd]:")) {
                                getPlayer().performCommand(
                                        a.replace("[cmd]:", ""));
                            }
                        });
                    })
                    .update(event -> {
                        event.getItem().builder()
                                .name(SubscriptionUtil
                                        .applyPlaceholders(name, subscription)
                                        .replace("%date%", SubscriptionUtil.formatTime(subscription)))
                                .lore(SubscriptionUtil
                                        .applyPlaceholders(lore, subscription)
                                        .stream()
                                        .map(s -> s.replace("%date%", SubscriptionUtil.formatTime(subscription)))
                                        .collect(Collectors.toList()))
                                .build();
                    })
                    .build();

            if (yaml.get(path + ".mask") != null) {
                getGui().addMask(yaml.getString(path + ".mask"), staticItem);
            }

            if (yaml.get(path + ".slot") != null) {
                getGui().setItem(yaml.getInt(path + ".slot"), staticItem);
            }

            if (yaml.get(path + ".slots") != null) {
                yaml.getIntegerList(path + ".slots").forEach(integer -> {
                    getGui().setItem(integer, staticItem);
                });
            }

        }

    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setResult(Event.Result.DENY);
    }

    public static void open(Player player, Subscription subscription) {
        ColorfulGUI gui = HSubscription.getInstance().getGuiApi();
        FileConfiguration yaml = HSubscription.getInstance().getFileManager().getSubscriptionGUI().yaml();

        gui.gui()
                .mask(yaml.getStringList("gui.mask"))
                .rows(yaml.getInt("gui.rows"))
                .title(
                        SubscriptionUtil.applyPlaceholders(yaml.getString("gui.title"), subscription)
                )
                .holder(new SubscriptionGUI(player, subscription))
                .build();

    }

}
