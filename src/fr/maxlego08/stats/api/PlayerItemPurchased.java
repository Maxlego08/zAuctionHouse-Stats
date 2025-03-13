package fr.maxlego08.stats.api;

import fr.maxlego08.stats.dto.PlayerItemPurchasedDTO;
import fr.maxlego08.stats.zcore.logger.Logger;
import fr.maxlego08.zauctionhouse.api.AuctionItem;
import fr.maxlego08.zauctionhouse.api.enums.AuctionType;
import fr.maxlego08.zauctionhouse.zcore.utils.ItemStackUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerItemPurchased {
    private final UUID playerId;
    private final String playerName;
    private final String itemstack;
    private final long price;
    private final String economy;
    private final UUID sellerId;
    private final String sellerName;
    private final long purchaseTime;
    private final AuctionType auctionType;
    private long id;
    private ItemStack itemStackContent = null;

    public PlayerItemPurchased(UUID playerId, String playerName, String itemstack, long price, String economy, UUID sellerId, String sellerName, long purchaseTime, AuctionType auctionType) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.itemstack = itemstack;
        this.price = price;
        this.economy = economy;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.purchaseTime = purchaseTime;
        this.auctionType = auctionType;
    }

    public PlayerItemPurchased(PlayerItemPurchasedDTO dto) {
        this.playerId = dto.player_id();
        this.playerName = dto.player_name();
        this.itemstack = dto.itemstack();
        this.price = dto.price();
        this.economy = dto.economy();
        this.sellerId = dto.seller_id();
        this.sellerName = dto.seller_name();
        this.purchaseTime = dto.purchase_time();
        this.auctionType = dto.auction_type();
    }

    public PlayerItemPurchased(AuctionItem auctionItem, Player player) {
        this.playerId = player.getUniqueId();
        this.playerName = player.getName();
        this.itemstack = auctionItem.getType() == AuctionType.INVENTORY ? String.join(",", auctionItem.serializedItems()) : auctionItem.serializedItem();
        this.price = auctionItem.getPrice();
        this.economy = auctionItem.getEconomyName();
        this.purchaseTime = System.currentTimeMillis();
        this.sellerId = auctionItem.getSellerUniqueId();
        this.sellerName = auctionItem.getSellerName();
        this.auctionType = auctionItem.getType();
        this.itemStackContent = auctionItem.getType() == AuctionType.INVENTORY ? null : auctionItem.getItemStack().clone();
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
        return itemstack;
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

    @Override
    public String toString() {
        return "PlayerItemPurchased{" +
                "playerId=" + playerId +
                ", playerName='" + playerName + '\'' +
                ", itemStack='" + itemstack + '\'' +
                ", price=" + price +
                ", economy='" + economy + '\'' +
                ", sellerId=" + sellerId +
                ", sellerName='" + sellerName + '\'' +
                ", purchaseTime=" + purchaseTime +
                ", auctionType=" + auctionType +
                ", id=" + id +
                ", itemStackContent=" + itemStackContent +
                '}';
    }

    public ItemStack getItemStackContent() {

        if (this.itemstack == null){
            Logger.info("ItemStack is null for: " + this, Logger.LogType.ERROR);
            return null;
        }

        if (this.auctionType != AuctionType.DEFAULT) return null;

        if (this.itemStackContent == null) {
            this.itemStackContent = ItemStackUtils.safeDeserializeItemStack(this.itemstack);
        }
        return this.itemStackContent;
    }

    public String getType() {
        if (this.auctionType == null || this.auctionType != AuctionType.DEFAULT) return "";

        try {
            if (this.itemStackContent == null) {
                this.itemStackContent = ItemStackUtils.safeDeserializeItemStack(this.itemstack);
            }

            return this.itemStackContent.getType().name();
        } catch (Exception exception) {
            return "";
        }
    }
}
