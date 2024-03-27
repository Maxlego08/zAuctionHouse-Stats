package fr.maxlego08.stats.api;

import fr.maxlego08.zauctionhouse.api.AuctionItem;
import fr.maxlego08.zauctionhouse.api.enums.AuctionType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerItemPurchased {
    private final UUID playerId;
    private final String playerName;
    private final String itemStack;
    private final long price;
    private final String economy;
    private final UUID sellerId;
    private final String sellerName;
    private final long purchaseTime;
    private final AuctionType auctionType;
    private long id;

    public PlayerItemPurchased(UUID playerId, String playerName, String itemStack, long price, String economy, UUID sellerId, String sellerName, long purchaseTime, AuctionType auctionType) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.itemStack = itemStack;
        this.price = price;
        this.economy = economy;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.purchaseTime = purchaseTime;
        this.auctionType = auctionType;
    }

    public PlayerItemPurchased(AuctionItem auctionItem, Player player) {
        this.playerId = player.getUniqueId();
        this.playerName = player.getName();
        this.itemStack = auctionItem.getType() == AuctionType.INVENTORY ? String.join(",", auctionItem.serializedItems()) : auctionItem.serializedItem();
        this.price = auctionItem.getPrice();
        this.economy = auctionItem.getEconomyName();
        this.purchaseTime = System.currentTimeMillis();
        this.sellerId = auctionItem.getSellerUniqueId();
        this.sellerName = auctionItem.getSellerName();
        this.auctionType = auctionItem.getType();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getItemStack() {
        return itemStack;
    }

    public long getPrice() {
        return price;
    }

    public String getEconomy() {
        return economy;
    }

    public UUID getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }

    public AuctionType getAuctionType() {
        return auctionType;
    }
}
