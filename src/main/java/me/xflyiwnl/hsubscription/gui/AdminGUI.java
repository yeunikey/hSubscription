package me.xflyiwnl.hsubscription.gui;

import me.xflyiwnl.colorfulgui.ColorfulGUI;
import me.xflyiwnl.colorfulgui.object.DynamicItem;
import me.xflyiwnl.colorfulgui.object.PaginatedGui;
import me.xflyiwnl.colorfulgui.provider.ColorfulProvider;
import me.xflyiwnl.hsubscription.HSubscription;
import me.xflyiwnl.hsubscription.action.Action;
import me.xflyiwnl.hsubscription.action.SubscriptionAction;
import me.xflyiwnl.hsubscription.object.Subscription;
import me.xflyiwnl.hsubscription.util.SubscriptionUtil;
import me.xflyiwnl.hsubscription.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AdminGUI extends ColorfulProvider<PaginatedGui> {

    public AdminGUI(Player player) {
        super(player, 1);
    }

    @Override
    public void init() {

        ColorfulGUI gui = HSubscription.getInstance().getGuiApi();
        FileConfiguration yaml = HSubscription.getInstance().getFileManager().getAdminGUI().yaml();

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
                    .name(name)
                    .lore(lore).flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS)
                    .action(event -> {
                        action.forEach(a -> {
                            if (a.startsWith("[msg]:")) {
                                getPlayer().sendMessage(TextUtil.colorize(a.replace("[msg]:", "")));
                            } else if (a.startsWith("[cmd]:")) {
                                getPlayer().performCommand(
                                        a.replace("[cmd]:", ""));
                            }
                        });
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

        subscriptons();

    }

    public void subscriptons() {

        HSubscription instance = HSubscription.getInstance();
        ColorfulGUI gui = instance.getGuiApi();
        FileConfiguration yaml = instance.getFileManager().getAdminGUI().yaml();

        if (!yaml.isConfigurationSection("subscription-item")) {
            return;
        }

        for (Subscription subscription : HSubscription.getInstance().getSubscriptions()) {
            String path = "subscription-item.";

            Material material = Material.valueOf(yaml.getString(path + ".material").toUpperCase());

            String name = yaml.getString(path + ".name");
            List<String> lore = yaml.getStringList(path + ".lore");

            Duration duration = Duration.between(LocalDateTime.now(), subscription.getPenaltyDate());

            DynamicItem subscriptionItem = gui.dynamicItem()
                    .material(material)
                    .amount((int) duration.toDaysPart() == 0 ? 1 : (int) duration.toDaysPart())
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
                        if (instance.getActions().containsKey(getPlayer().getUniqueId())) return;
                        Action action = new SubscriptionAction(getPlayer(), subscription);
                        instance.getActions().put(getPlayer().getUniqueId(), action);

                        action.start();
                        getPlayer().closeInventory();
                    })
                    .build();
            getGui().addItem(subscriptionItem);

        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setResult(Event.Result.DENY);
    }

    public static void open(Player player) {
        ColorfulGUI gui = HSubscription.getInstance().getGuiApi();
        FileConfiguration yaml = HSubscription.getInstance().getFileManager().getAdminGUI().yaml();

        gui.paginated()
                .mask(yaml.getStringList("gui.mask"))
                .rows(yaml.getInt("gui.rows"))
                .title(yaml.getString("gui.title"))
                .holder(new AdminGUI(player))
                .build();

    }

}
