package fr.maxlego08.stats.api;

import fr.maxlego08.stats.dto.PlayerStatsDTO;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerStats {
    private final UUID uuid;
    private final String name;
    private long totalItemsSold;
    private long totalItemsBought;
    private long firstItemBoughtAt;
    private long lastItemBoughtAt;
    private long firstItemSoldAt;
    private long lastItemSoldAt;

    public PlayerStats(UUID uuid, String name, long totalItemsSold, long totalItemsBought, long firstItemBoughtAt, long lastItemBoughtAt, long firstItemSoldAt, long lastItemSoldAt) {
        this.uuid = uuid;
        this.name = name;
        this.totalItemsSold = totalItemsSold;
        this.totalItemsBought = totalItemsBought;
        this.firstItemBoughtAt = firstItemBoughtAt;
        this.lastItemBoughtAt = lastItemBoughtAt;
        this.firstItemSoldAt = firstItemSoldAt;
        this.lastItemSoldAt = lastItemSoldAt;
    }

    public PlayerStats(PlayerStatsDTO dto) {
        this.uuid = dto.uuid();
        this.name = dto.name();
        this.totalItemsSold = dto.total_items_sold();
        this.totalItemsBought = dto.total_items_bought();
        this.firstItemBoughtAt = dto.first_item_bought_at();
        this.lastItemBoughtAt = dto.last_item_bought_at();
        this.firstItemSoldAt = dto.first_item_sold_at();
        this.lastItemSoldAt = dto.last_item_sold_at();
    }

    public PlayerStats(Player player) {
        this(player.getUniqueId(), player.getName(), 0, 0, 0, 0, 0, 0);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public long getTotalItemsSold() {
        return totalItemsSold;
    }

    public void setTotalItemsSold(long totalItemsSold) {
        this.totalItemsSold = totalItemsSold;
    }

    public long getTotalItemsBought() {
        return totalItemsBought;
    }

    public void setTotalItemsBought(long totalItemsBought) {
        this.totalItemsBought = totalItemsBought;
    }

    public long getFirstItemBoughtAt() {
        return firstItemBoughtAt;
    }

    public void setFirstItemBoughtAt(long firstItemBoughtAt) {
        this.firstItemBoughtAt = firstItemBoughtAt;
    }

    public long getLastItemBoughtAt() {
        return lastItemBoughtAt;
    }

    public void setLastItemBoughtAt(long lastItemBoughtAt) {
        this.lastItemBoughtAt = lastItemBoughtAt;
    }

    public long getFirstItemSoldAt() {
        return firstItemSoldAt;
    }

    public void setFirstItemSoldAt(long firstItemSoldAt) {
        this.firstItemSoldAt = firstItemSoldAt;
    }

    public long getLastItemSoldAt() {
        return lastItemSoldAt;
    }

    public void setLastItemSoldAt(long lastItemSoldAt) {
        this.lastItemSoldAt = lastItemSoldAt;
    }

    public void soldItem() {
        this.totalItemsSold += 1;
        if (this.firstItemSoldAt == 0) this.firstItemSoldAt = System.currentTimeMillis();
        this.lastItemSoldAt = System.currentTimeMillis();
    }

    public void purchaseItem() {
        this.totalItemsBought += 1;
        if (this.firstItemBoughtAt == 0) this.firstItemBoughtAt = System.currentTimeMillis();
        this.lastItemBoughtAt = System.currentTimeMillis();
    }
}
