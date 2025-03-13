package fr.maxlego08.stats.api;

import fr.maxlego08.stats.dto.PlayerItemForSaleDTO;
import fr.maxlego08.zauctionhouse.api.AuctionItem;
import fr.maxlego08.zauctionhouse.api.enums.AuctionType;
import fr.maxlego08.zauctionhouse.zcore.utils.ItemStackUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerItemForSale {
    private final UUID playerId;
    private final String playerName;
    private final String itemStack;
    private final long price;
    private final String economy;
    private final AuctionType auctionType;
    private final long expireAt;
    private final long createdAt;
    private long id;
    private ItemStack itemStackContent = null;

    public PlayerItemForSale(long id, UUID playerId, String playerName, String itemStack, long price, String economy, String auctionType, long expireAt, long createdAt) {
        this.id = id;
        this.playerId = playerId;
        this.playerName = playerName;
        this.itemStack = itemStack;
        this.price = price;
        this.economy = economy;
        this.auctionType = AuctionType.valueOf(auctionType);
        this.expireAt = expireAt;
        this.createdAt = createdAt;
    }

    public PlayerItemForSale(PlayerItemForSaleDTO playerItemForSaleDTO) {
        this.id = playerItemForSaleDTO.id();
        this.playerId = playerItemForSaleDTO.player_id();
        this.playerName = playerItemForSaleDTO.player_name();
        this.itemStack = playerItemForSaleDTO.itemStack();
        this.price = playerItemForSaleDTO.price();
        this.economy = playerItemForSaleDTO.economy();
        this.auctionType = playerItemForSaleDTO.auction_type();
        this.expireAt = playerItemForSaleDTO.expire_at();
        this.createdAt = playerItemForSaleDTO.created_at();
    }

    public PlayerItemForSale(AuctionItem auctionItem, Player player) {
        this.playerId = player.getUniqueId();
        this.playerName = player.getName();
        this.itemStack = auctionItem.getType() == AuctionType.INVENTORY ? String.join(",", auctionItem.serializedItems()) : auctionItem.serializedItem();
        this.price = auctionItem.getPrice();
        this.economy = auctionItem.getEconomyName();
        this.auctionType = auctionItem.getType();
        this.expireAt = auctionItem.getExpireAt();
        this.createdAt = System.currentTimeMillis();
        this.itemStackContent = auctionItem.getType() == AuctionType.DEFAULT ? auctionItem.getItemStack().clone() : null;
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

    public AuctionType getAuctionType() {
        return auctionType;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public String getType() {
        if (this.auctionType == null || this.auctionType != AuctionType.DEFAULT) return "";

        try {
            if (this.itemStackContent == null) {
                this.itemStackContent = ItemStackUtils.safeDeserializeItemStack(this.itemStack);
            }

            return this.itemStackContent.getType().name();
        } catch (Exception exception) {
            return "";
        }
    }

}
