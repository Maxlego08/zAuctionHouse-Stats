package fr.maxlego08.stats;

import fr.maxlego08.stats.api.PlayerItemForSale;
import fr.maxlego08.stats.api.PlayerItemPurchased;
import fr.maxlego08.stats.api.economy.EconomyKey;
import fr.maxlego08.stats.api.global.GlobalKey;
import fr.maxlego08.stats.api.global.GlobalValue;
import fr.maxlego08.stats.api.utils.Pair;
import fr.maxlego08.stats.placeholder.LocalPlaceholder;
import fr.maxlego08.stats.zcore.utils.ZUtils;
import fr.maxlego08.zauctionhouse.api.AuctionItem;
import fr.maxlego08.zauctionhouse.api.economy.AuctionEconomy;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StatsManager extends ZUtils implements Listener {

    private final StatsPlugin plugin;
    private EnumMap<GlobalKey, GlobalValue> globalValues;
    private Map<Pair<EconomyKey, String>, Long> economyValues = new HashMap<>();
    private Map<UUID, List<PlayerItemForSale>> playerSaleItems = new HashMap<>();
    private Map<UUID, List<PlayerItemPurchased>> playerPurchaseItems = new HashMap<>();

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

    public GlobalValue getValue(GlobalKey globalKey) {
        return this.globalValues.computeIfAbsent(globalKey, i -> new GlobalValue(0));
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
}
