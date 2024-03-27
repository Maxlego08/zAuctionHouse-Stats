package fr.maxlego08.stats;

import fr.maxlego08.stats.api.economy.EconomyKey;
import fr.maxlego08.stats.api.global.GlobalKey;
import fr.maxlego08.stats.zcore.utils.ZUtils;
import fr.maxlego08.zauctionhouse.api.enums.StorageType;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionAdminRemoveEvent;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionItemExpireEvent;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionOpenEvent;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionPostBuyEvent;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionRemoveEvent;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionRetrieveEvent;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionSellEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class StatsListener extends ZUtils implements Listener {

    private final StatsManager manager;


    public StatsListener(StatsManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSell(AuctionSellEvent event) {
        if (event.isCancelled()) return;

        this.manager.addOne(GlobalKey.ITEM_SOLD_AMOUNT);
        this.manager.updateEconomy(EconomyKey.TOTAL_PRICE_OF_ALL_ITEMS_SOLD, event.getEconomy(), event.getPrice());
        this.manager.storeSellItem(event.getPlayer(), event.getAuctionItem());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPurchase(AuctionPostBuyEvent event) {
        if (event.isCancelled()) return;

        this.manager.addOne(GlobalKey.ITEM_PURCHASE_AMOUNT);
        this.manager.updateEconomy(EconomyKey.MONEY_SPEND, event.getAuctionItem().getEconomy(), event.getTransaction().getPrice());
        this.manager.storePurchaseItem(event.getPlayer(), event.getAuctionItem());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRemove(AuctionAdminRemoveEvent event) {
        if (event.isCancelled()) return;

        this.manager.addOne(GlobalKey.NUMBER_OF_ITEMS_NOT_SOLD);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRemove(AuctionItemExpireEvent event) {

        if (event.getType() == StorageType.STORAGE) { // L'item n'a pas été vendu, il a expiré

            this.manager.addOne(GlobalKey.NUMBER_OF_ITEMS_NOT_SOLD);
        } else { // L'item n'a pas été récupéré, il a été détruit

            this.manager.addOne(GlobalKey.NUMBER_OF_ITEMS_DESTROY);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRemove(AuctionRemoveEvent event) {
        if (event.isCancelled()) return;

        switch (event.getType()) {
            case STORAGE:
                this.manager.addOne(GlobalKey.NUMBER_OF_ITEMS_NOT_SOLD);
                this.manager.addOne(GlobalKey.NUMBER_OF_ITEMS_REMOVE_FROM_STORAGE);
                break;
            case BUY:
                this.manager.addOne(GlobalKey.NUMBER_OF_ITEMS_REMOVE_FROM_BOUGHT);
                break;
            case EXPIRE:
                this.manager.addOne(GlobalKey.NUMBER_OF_ITEMS_REMOVE_FROM_EXPIRE);
                break;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRemove(AuctionRetrieveEvent event) {
        if (event.isCancelled()) return;

        this.manager.addOne(GlobalKey.NUMBER_OF_ITEMS_NOT_SOLD);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOpen(AuctionOpenEvent event) {
        if (event.isCancelled()) return;

        this.manager.addOne(GlobalKey.AUCTION_OPENED);
    }
}
