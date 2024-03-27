package fr.maxlego08.stats;

import fr.maxlego08.stats.api.PlayerItemForSale;
import fr.maxlego08.stats.api.PlayerItemPurchased;
import fr.maxlego08.stats.api.PlayerStats;
import fr.maxlego08.stats.api.economy.EconomyKey;
import fr.maxlego08.stats.api.global.GlobalKey;
import fr.maxlego08.stats.api.global.GlobalValue;
import fr.maxlego08.stats.api.utils.Pair;
import fr.maxlego08.stats.placeholder.LocalPlaceholder;
import fr.maxlego08.stats.save.Config;
import fr.maxlego08.stats.zcore.utils.ZUtils;
import fr.maxlego08.zauctionhouse.api.AuctionItem;
import fr.maxlego08.zauctionhouse.api.economy.AuctionEconomy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class StatsManager extends ZUtils implements Listener {

    private final StatsPlugin plugin;
    private EnumMap<GlobalKey, GlobalValue> globalValues;
    private Map<Pair<EconomyKey, String>, Long> economyValues = new HashMap<>();
    private Map<UUID, List<PlayerItemForSale>> playerSaleItems = new HashMap<>();
    private Map<UUID, List<PlayerItemPurchased>> playerPurchaseItems = new HashMap<>();
    private Map<UUID, PlayerStats> playerStats = new HashMap<>();

    public StatsManager(StatsPlugin plugin) {
        this.plugin = plugin;
        this.globalValues = new EnumMap<>(GlobalKey.class);
    }

    public void setGlobalValues(EnumMap<GlobalKey, GlobalValue> globalValues) {
        this.globalValues = globalValues;
    }

    public void setEconomyValues(Map<Pair<EconomyKey, String>, Long> economyValues) {
        this.economyValues = economyValues;
    }

    public void setPlayerSaleItems(Map<UUID, List<PlayerItemForSale>> playerSaleItems) {
        this.playerSaleItems = playerSaleItems;
    }

    public void setPlayerPurchaseItems(Map<UUID, List<PlayerItemPurchased>> playerPurchaseItems) {
        this.playerPurchaseItems = playerPurchaseItems;
    }

    public void setPlayerStats(Map<UUID, PlayerStats> playerStats) {
        this.playerStats = playerStats;
    }

    public GlobalValue getValue(GlobalKey globalKey) {
        return this.globalValues.computeIfAbsent(globalKey, i -> new GlobalValue(0));
    }

    public PlayerStats getPlayerStats(Player player) {
        return this.playerStats.computeIfAbsent(player.getUniqueId(), u -> new PlayerStats(player));
    }

    public void updatePlayerStats(PlayerStats playerStats) {
        this.plugin.getPlayerStatsTable().updatePlayerStats(playerStats);
    }

    public void addOne(GlobalKey globalKey) {
        getValue(globalKey).add(1L);
        this.plugin.getGlobalStatsTable().upsert(globalKey, getValue(globalKey));
    }

    public void storeSellItem(Player player, AuctionItem auctionItem) {
        PlayerItemForSale item = new PlayerItemForSale(auctionItem, player);
        this.playerSaleItems.computeIfAbsent(player.getUniqueId(), u -> new ArrayList<>()).add(item);
        this.plugin.getPlayerItemSaleTable().insertItemForSale(item);
    }

    public void storePurchaseItem(Player player, AuctionItem auctionItem) {
        PlayerItemPurchased item = new PlayerItemPurchased(auctionItem, player);
        this.playerPurchaseItems.computeIfAbsent(player.getUniqueId(), u -> new ArrayList<>()).add(item);
        this.plugin.getPlayerItemPurchasedTable().insertItemPurchased(item);
    }

    public void updateEconomy(EconomyKey economyKey, AuctionEconomy economy, long amount) {
        Pair<EconomyKey, String> pair = new Pair<>(economyKey, economy.getName());
        long result = amount + this.economyValues.getOrDefault(pair, 0L);
        this.economyValues.put(pair, result);
        this.plugin.getGlobalEconomyStatsTable().upsertGlobalStatistic(economyKey, economy.getName(), result);
    }

    public void registerGlobalPlaceholders() {

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        for (GlobalKey key : GlobalKey.values()) {
            placeholder.register(key.name().toLowerCase(), player -> getValue(key).asString());
        }

        for (EconomyKey key : EconomyKey.values()) {
            placeholder.register(key.name().toLowerCase() + "_", (player, economyName) -> String.valueOf(this.economyValues.getOrDefault(new Pair<>(key, economyName), 0L)));
        }
    }

    public void registerPlayerPlaceholders() {

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.register("player_total_sales", player -> String.valueOf(this.playerStats.getOrDefault(player.getUniqueId(), new PlayerStats(player)).getTotalItemsSold()));
        placeholder.register("player_total_purchases", player -> String.valueOf(this.playerStats.getOrDefault(player.getUniqueId(), new PlayerStats(player)).getTotalItemsBought()));

        placeholder.register("player_total_earned_", (player, economyName) -> String.valueOf(this.playerPurchaseItems.values().stream().flatMap(List::stream).filter(item -> item.getSellerId().equals(player.getUniqueId()) && item.getEconomy().equals(economyName)).mapToLong(PlayerItemPurchased::getPrice).sum()));
        placeholder.register("player_total_spent_", (player, economyName) -> String.valueOf(this.playerPurchaseItems.getOrDefault(player.getUniqueId(), new ArrayList<>()).stream().mapToLong(PlayerItemPurchased::getPrice).sum()));
    }

    public void registerRankingPlaceholders() {

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.register("who_spent_most_money_name_", (player, economyName) -> {
            Optional<Map.Entry<UUID, Long>> optional = findTopSpender(economyName);
            if (optional.isPresent()) {
                Map.Entry<UUID, Long> entry = optional.get();
                UUID uuid = entry.getKey();
                return this.playerStats.containsKey(entry.getKey()) ? this.playerStats.get(uuid).getName() : Bukkit.getOfflinePlayer(uuid).getName();
            }
            return Config.whoSpentMostMoneyEmptyMoney;
        });
        placeholder.register("who_spent_most_money_amount_", (player, economyName) -> {
            Optional<Map.Entry<UUID, Long>> optional = findTopSpender(economyName);
            if (optional.isPresent()) {
                Map.Entry<UUID, Long> entry = optional.get();
                return String.valueOf(entry.getValue());
            }
            return Config.whoSpentMostMoneyEmptyMoney;
        });

    }

    public Optional<Map.Entry<UUID, Long>> findTopSpender(String economyName) {
        Map<UUID, Long> spendingPerPlayer = new HashMap<>();

        if (this.playerPurchaseItems.isEmpty()) return Optional.empty();

        for (List<PlayerItemPurchased> purchases : playerPurchaseItems.values()) {
            for (PlayerItemPurchased purchase : purchases) {
                if (purchase.getEconomy().equals(economyName)) {
                    spendingPerPlayer.merge(purchase.getPlayerId(), purchase.getPrice(), Long::sum);
                }
            }
        }

        return Optional.of(Collections.max(spendingPerPlayer.entrySet(), Map.Entry.comparingByValue()));
    }


    public Optional<Map.Entry<UUID, Long>> findTopEarner(String economyName) {
        Map<UUID, Long> earningsPerPlayer = new HashMap<>();

        if (this.playerSaleItems.isEmpty()) return Optional.empty();

        for (List<PlayerItemForSale> sales : playerSaleItems.values()) {
            for (PlayerItemForSale sale : sales) {
                if (sale.getEconomy().equals(economyName)) {
                    earningsPerPlayer.merge(sale.getPlayerId(), sale.getPrice(), Long::sum);
                }
            }
        }

        return Optional.of(Collections.max(earningsPerPlayer.entrySet(), Map.Entry.comparingByValue()));
    }

}
