package fr.maxlego08.stats.zmenu.buttons;

import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.stats.ItemPriceStatistics;
import fr.maxlego08.stats.StatsPlugin;
import fr.maxlego08.stats.save.Config;
import fr.maxlego08.stats.zcore.ZPlugin;
import fr.maxlego08.stats.zcore.utils.ElapsedTime;
import fr.maxlego08.zauctionhouse.api.AuctionManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ButtonItemStatistics extends ZButton {

    private final AuctionManager auctionManager;
    private final StatsPlugin plugin;
    private final String economyName;

    public ButtonItemStatistics(AuctionManager auctionManager, StatsPlugin plugin, String economyName) {
        this.auctionManager = auctionManager;
        this.plugin = plugin;
        this.economyName = economyName;
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryDefault inventory) {

        ItemPriceStatistics manager = this.plugin.getManager().getItemPriceStatistics();

        MenuItemStack menuItemStack = this.getItemStack();
        String materialName = menuItemStack.getMaterial();

        inventory.addItem(this.getSlot(), getItemStack(player));

        if (!manager.isCache(materialName, economyName)) {
            ZPlugin.service.execute(() -> {
                ElapsedTime elapsedTime = new ElapsedTime("update-" + economyName + "-" + materialName);
                elapsedTime.start();
                manager.updateCacheIfNeeded(economyName, materialName);
                elapsedTime.endDisplay();
                inventory.addItem(this.getSlot(), getItemStack(player));
            });
        }
    }

    private ItemStack getItemStack(Player player) {
        ItemPriceStatistics manager = this.plugin.getManager().getItemPriceStatistics();

        MenuItemStack menuItemStack = this.getItemStack();
        String materialName = menuItemStack.getMaterial();

        Placeholders placeholders = new Placeholders();

        if (manager.isCache(materialName, economyName)) {

            placeholders.register("amount", this.auctionManager.getPriceFormat(manager.getItemCount(economyName, materialName)));
            placeholders.register("average", this.auctionManager.getPriceFormat((long) manager.getAverage(economyName, materialName)));
            placeholders.register("median", this.auctionManager.getPriceFormat((long) manager.getMedian(economyName, materialName)));
        } else {

            placeholders.register("amount", Config.loading);
            placeholders.register("average", Config.loading);
            placeholders.register("median", Config.loading);
        }

        return menuItemStack.build(player, false, placeholders);
    }

}
