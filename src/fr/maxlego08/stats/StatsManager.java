package fr.maxlego08.stats;

import fr.maxlego08.stats.api.PlayerItemForSale;
import fr.maxlego08.stats.api.PlayerItemPurchased;
import fr.maxlego08.stats.api.PlayerStats;
import fr.maxlego08.stats.api.economy.EconomyKey;
import fr.maxlego08.stats.api.global.GlobalKey;
import fr.maxlego08.stats.api.global.GlobalValue;
import fr.maxlego08.stats.api.utils.Pair;
import fr.maxlego08.stats.placeholder.LocalPlaceholder;
import fr.maxlego08.stats.placeholder.ReturnConsumer;
import fr.maxlego08.stats.save.Config;
import fr.maxlego08.stats.zcore.utils.ZUtils;
import fr.maxlego08.zauctionhouse.api.AuctionItem;
import fr.maxlego08.zauctionhouse.api.AuctionManager;
import fr.maxlego08.zauctionhouse.api.economy.AuctionEconomy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatsManager extends ZUtils implements Listener {

    private final StatsPlugin plugin;
    private final AuctionManager auctionManager;
    private final ItemPriceStatistics itemPriceStatistics;
    private EnumMap<GlobalKey, GlobalValue> globalValues;
    private Map<Pair<EconomyKey, String>, Long> economyValues = new HashMap<>();
    private Map<UUID, List<PlayerItemForSale>> playerSaleItems = new HashMap<>();
    private Map<UUID, List<PlayerItemPurchased>> playerPurchaseItems = new HashMap<>();
    private Map<UUID, PlayerStats> playerStats = new HashMap<>();

    public StatsManager(StatsPlugin plugin) {
        this.plugin = plugin;
        this.globalValues = new EnumMap<>(GlobalKey.class);
        this.auctionManager = plugin.getProvider(AuctionManager.class);
        this.itemPriceStatistics = new ItemPriceStatistics(this, 0);
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
            placeholder.register(key.name().toLowerCase() + "_", (player, economyName) -> {
                if (economyName.startsWith("format_")) {
                    return this.auctionManager.getPriceFormat(this.economyValues.getOrDefault(new Pair<>(key, economyName.replace("format_", "")), 0L));
                }
                return String.valueOf(this.economyValues.getOrDefault(new Pair<>(key, economyName), 0L));
            });
        }

        placeholder.register("who_purchased_most_items_name", player -> {
            Optional<Map.Entry<UUID, List<PlayerItemPurchased>>> optional = findTopBuyerByItemCount();
            return optional.isPresent() ? returnPlayerName(optional.get()) : Config.noName;
        });
        placeholder.register("who_purchased_most_items_amount", player -> {
            Optional<Map.Entry<UUID, List<PlayerItemPurchased>>> optional = findTopBuyerByItemCount();
            return optional.map(uuidListEntry -> String.valueOf(uuidListEntry.getValue().size())).orElseGet(() -> Config.noAmount);
        });

        placeholder.register("who_sales_most_items_name", player -> {
            Optional<Map.Entry<UUID, List<PlayerItemForSale>>> optional = findTopSellerByItemCount();
            return optional.isPresent() ? returnPlayerName(optional.get()) : Config.noName;
        });
        placeholder.register("who_sales_most_items_amount", player -> {
            Optional<Map.Entry<UUID, List<PlayerItemForSale>>> optional = findTopSellerByItemCount();
            return optional.map(uuidListEntry -> String.valueOf(uuidListEntry.getValue().size())).orElseGet(() -> Config.noAmount);
        });
    }

    public void registerPlayerPlaceholders() {

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        registerPlayerStats("player_total_sales", player -> String.valueOf(player.getTotalItemsSold()));
        registerPlayerStats("player_total_purchases", player -> String.valueOf(player.getTotalItemsBought()));
        registerPlayerStats("player_total_sales_format", player -> auctionManager.getPriceFormat(player.getTotalItemsSold()));
        registerPlayerStats("player_total_purchases_format", player -> auctionManager.getPriceFormat(player.getTotalItemsBought()));

        placeholder.register("player_total_earned_", (player, economyName) -> String.valueOf(getPlayerTotalEarned(player, economyName)));
        placeholder.register("player_total_spent_", (player, economyName) -> String.valueOf(getPlayerTotalSpent(player, economyName)));
        placeholder.register("player_total_format_earned_", (player, economyName) -> auctionManager.getPriceFormat(getPlayerTotalEarned(player, economyName)));
        placeholder.register("player_total_format_spent_", (player, economyName) -> auctionManager.getPriceFormat(getPlayerTotalSpent(player, economyName)));
    }

    private void registerPlayerStats(String key, ReturnConsumer<PlayerStats, String> consumer) {
        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.register(key, player -> consumer.accept(this.playerStats.getOrDefault(player.getUniqueId(), new PlayerStats(player))));
    }

    private long getPlayerTotalEarned(Player player, String economyName) {
        return this.playerPurchaseItems.values().stream().flatMap(List::stream).filter(item -> item.getSellerId().equals(player.getUniqueId()) && item.getEconomy().equals(economyName)).mapToLong(PlayerItemPurchased::getPrice).sum();
    }

    private long getPlayerTotalSpent(Player player, String economyName) {
        return this.playerPurchaseItems.getOrDefault(player.getUniqueId(), new ArrayList<>()).stream().filter(item -> item.getEconomy().equals(economyName)).mapToLong(PlayerItemPurchased::getPrice).sum();
    }

    public void registerRankingPlaceholders() {

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.register("who_spent_most_money_name_", (player, economyName) -> {
            Optional<Map.Entry<UUID, Long>> optional = findTopSpender(economyName);
            return optional.isPresent() ? returnPlayerName(optional.get()) : Config.noName;
        });
        placeholder.register("who_spent_most_money_amount_", (player, economyName) -> {
            Optional<Map.Entry<UUID, Long>> optional = findTopSpender(economyName);
            return optional.isPresent() ? String.valueOf(optional.get().getValue()) : Config.noAmount;
        });
        placeholder.register("format_who_spent_most_money_amount_", (player, economyName) -> {
            Optional<Map.Entry<UUID, Long>> optional = findTopSpender(economyName);
            return optional.isPresent() ? this.auctionManager.getPriceFormat(optional.get().getValue()) : Config.noAmount;
        });

        placeholder.register("who_earned_most_money_name_", (player, economyName) -> {
            Optional<Map.Entry<UUID, Long>> optional = findTopEarner(economyName);
            return optional.isPresent() ? returnPlayerName(optional.get()) : Config.noName;
        });
        placeholder.register("who_earned_most_money_amount_", (player, economyName) -> {
            Optional<Map.Entry<UUID, Long>> optional = findTopEarner(economyName);
            return optional.isPresent() ? String.valueOf(optional.get().getValue()) : Config.noAmount;
        });
        placeholder.register("format_who_earned_most_money_amount_", (player, economyName) -> {
            Optional<Map.Entry<UUID, Long>> optional = findTopEarner(economyName);
            return optional.isPresent() ? this.auctionManager.getPriceFormat(optional.get().getValue()) : Config.noAmount;
        });

        placeholder.register("who_purchased_most_items_name_", (player, economyName) -> {
            Optional<Map.Entry<UUID, List<PlayerItemPurchased>>> optional = findTopBuyerByItemCount(economyName);
            return optional.isPresent() ? returnPlayerName(optional.get()) : Config.noName;
        });
        placeholder.register("who_purchased_most_items_amount_", (player, economyName) -> {
            Optional<Map.Entry<UUID, List<PlayerItemPurchased>>> optional = findTopBuyerByItemCount(economyName);
            return optional.map(uuidListEntry -> String.valueOf(uuidListEntry.getValue().size())).orElseGet(() -> Config.noAmount);
        });

        placeholder.register("who_sales_most_items_name_", (player, economyName) -> {
            Optional<Map.Entry<UUID, List<PlayerItemForSale>>> optional = findTopSellerByItemCount(economyName);
            return optional.isPresent() ? returnPlayerName(optional.get()) : Config.noName;
        });
        placeholder.register("who_sales_most_items_amount_", (player, economyName) -> {
            Optional<Map.Entry<UUID, List<PlayerItemForSale>>> optional = findTopSellerByItemCount(economyName);
            return optional.map(uuidListEntry -> String.valueOf(uuidListEntry.getValue().size())).orElseGet(() -> Config.noAmount);
        });

    }

    private String returnPlayerName(Map.Entry<UUID, ?> entry) {
        UUID uuid = entry.getKey();
        return this.playerStats.containsKey(entry.getKey()) ? this.playerStats.get(uuid).getName() : Bukkit.getOfflinePlayer(uuid).getName();
    }

    public Optional<Map.Entry<UUID, Long>> findTopSpender(String economyName) {
        return this.playerPurchaseItems.values().stream().flatMap(List::stream).filter(purchase -> purchase.getEconomy().equals(economyName)).collect(Collectors.groupingBy(PlayerItemPurchased::getPlayerId, Collectors.summingLong(PlayerItemPurchased::getPrice))).entrySet().stream().max(Map.Entry.comparingByValue());
    }

    public Optional<Map.Entry<UUID, Long>> findTopEarner(String economyName) {
        return this.playerPurchaseItems.values().stream().flatMap(List::stream).filter(sale -> sale.getEconomy().equals(economyName)).collect(Collectors.groupingBy(PlayerItemPurchased::getSellerId, Collectors.summingLong(PlayerItemPurchased::getPrice))).entrySet().stream().max(Map.Entry.comparingByValue());
    }

    public Optional<Map.Entry<UUID, List<PlayerItemForSale>>> findTopSellerByItemCount(String economyName) {
        return playerSaleItems.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .filter(sale -> sale.getEconomy().equals(economyName)).collect(Collectors.toList())))
                .entrySet().stream()
                .max(Comparator.comparingInt(entry -> entry.getValue().size()));
    }

    public Optional<Map.Entry<UUID, List<PlayerItemPurchased>>> findTopBuyerByItemCount(String economyName) {
        return playerPurchaseItems.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .filter(purchase -> purchase.getEconomy().equals(economyName)).collect(Collectors.toList())))
                .entrySet().stream()
                .max(Comparator.comparingInt(entry -> entry.getValue().size()));
    }

    public Optional<Map.Entry<UUID, List<PlayerItemForSale>>> findTopSellerByItemCount() {
        return playerSaleItems.entrySet().stream().max(Comparator.comparingInt(entry -> entry.getValue().size()));
    }

    public Optional<Map.Entry<UUID, List<PlayerItemPurchased>>> findTopBuyerByItemCount() {
        return playerPurchaseItems.entrySet().stream().max(Comparator.comparingInt(entry -> entry.getValue().size()));
    }

    public Stream<PlayerItemPurchased> getPlayerPurchaseItems() {
        return playerPurchaseItems.values().stream().flatMap(List::stream);
    }

    public void setPlayerPurchaseItems(Map<UUID, List<PlayerItemPurchased>> playerPurchaseItems) {
        this.playerPurchaseItems = playerPurchaseItems;
    }

    public ItemPriceStatistics getItemPriceStatistics() {
        return itemPriceStatistics;
    }

    public void clearCaches() {
        this.itemPriceStatistics.clear();
    }

    public void registerItemPlaceholders() {
        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();

        registerPlaceholder(placeholder, "material_average_", (economy, material) -> returnValueOrNotANumber((int) this.itemPriceStatistics.getAverage(economy, material)));
        registerPlaceholder(placeholder, "material_median_", (economy, material) -> returnValueOrNotANumber((int) this.itemPriceStatistics.getMedian(economy, material)));
        registerPlaceholder(placeholder, "material_amount_", (economy, material) -> returnValueOrNotANumber(this.itemPriceStatistics.getItemCount(economy, material)));
        registerPlaceholder(placeholder, "material_format_average_", (economy, material) -> returnValueFormatOrNotANumber((int) this.itemPriceStatistics.getAverage(economy, material)));
        registerPlaceholder(placeholder, "material_format_median_", (economy, material) -> returnValueFormatOrNotANumber((int) this.itemPriceStatistics.getMedian(economy, material)));
        registerPlaceholder(placeholder, "material_format_amount_", (economy, material) -> returnValueFormatOrNotANumber(this.itemPriceStatistics.getItemCount(economy, material)));
    }

    private String returnValueOrNotANumber(int value) {
        return value == 0 && Config.enableNonApplicable ? Config.nonApplicable : String.valueOf(value);
    }

    private String returnValueFormatOrNotANumber(int value) {
        return value == 0 && Config.enableNonApplicable ? Config.nonApplicable : this.auctionManager.getPriceFormat(value);
    }

    private void registerPlaceholder(LocalPlaceholder placeholder, String prefix, BiFunction<String, String, String> function) {
        placeholder.register(prefix, (player, args) -> {
            String[] values = args.split("_", 2);
            if (values.length == 2) {
                String economyName = values[0];
                String materialName = values[1];
                return function.apply(economyName, materialName);
            }
            return "0";
        });
    }


}
