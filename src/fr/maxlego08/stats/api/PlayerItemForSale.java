package fr.maxlego08.stats.api;

import fr.maxlego08.zauctionhouse.api.AuctionItem;
import fr.maxlego08.zauctionhouse.api.enums.AuctionType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerItemForSale {
    private final UUID playerId;
    private final String playerName;
    private final String itemStack;
    private final long price;
    private final String economy;
    private final String auctionType;
    private final long expireAt;
    private final long createdAt;
    private long id;

    public PlayerItemForSale(long id, UUID playerId, String playerName, String itemStack, long price, String economy, String auctionType, long expireAt, long createdAt) {
        this.id = id;
        this.playerId = playerId;
        this.playerName = playerName;
        this.itemStack = itemStack;
        this.price = price;
        this.economy = economy;
        this.auctionType = auctionType;
        this.expireAt = expireAt;
        this.createdAt = expireAt;
    }

    public PlayerItemForSale(AuctionItem auctionItem, Player player) {
        this.playerId = player.getUniqueId();
        this.playerName = player.getName();
        this.itemStack = auctionItem.getType() == AuctionType.INVENTORY ? String.join(",", auctionItem.serializedItems()) : auctionItem.serializedItem();
        this.price = auctionItem.getPrice();
        this.economy = auctionItem.getEconomyName();
        this.auctionType = auctionItem.getType().name();
        this.expireAt = auctionItem.getExpireAt();
        this.createdAt = System.currentTimeMillis();
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

    public String getAuctionType() {
        return auctionType;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getExpireAt() {
        return expireAt;
    }
}
