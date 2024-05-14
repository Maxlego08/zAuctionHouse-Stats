package fr.maxlego08.stats;

import fr.maxlego08.stats.api.PlayerItemPurchased;
import fr.maxlego08.stats.save.Config;
import fr.maxlego08.stats.zcore.enums.Message;
import fr.maxlego08.stats.zcore.utils.ZUtils;
import fr.maxlego08.zauctionhouse.ZAuctionPlugin;
import fr.maxlego08.zauctionhouse.api.AuctionManager;
import fr.maxlego08.zauctionhouse.api.economy.AuctionEconomy;
import fr.maxlego08.zauctionhouse.api.enums.AuctionType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ItemPriceStatistics extends ZUtils {

    private final StatsManager manager;
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final long periodStart;

    public ItemPriceStatistics(StatsManager manager, long periodStart) {
        this.manager = manager;
        this.periodStart = periodStart;
    }

    private String buildCacheKey(String economyName, String materialName) {
        return economyName + "|" + materialName;
    }

    private boolean isCacheValid(String cacheKey) {
        CacheEntry entry = cache.get(cacheKey);
        return entry != null && System.currentTimeMillis() - entry.lastUpdateTime < Config.cacheDurationMaterial;
    }

    public void updateCacheIfNeeded(String economyName, String materialName) {
        String cacheKey = buildCacheKey(economyName, materialName);
        if (!isCacheValid(cacheKey)) {
            calculateAverageAndMedianPrice(economyName, materialName, cacheKey);
        }
    }

    private boolean checkItems(ItemStack itemStack, String value) {

        if (itemStack == null) return false;

        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasCustomModelData()) {
            try {
                return itemStack.getItemMeta().getCustomModelData() == Integer.parseInt(value);
            } catch (Exception ignored) {
            }
        }
        return itemStack.getType().name().equalsIgnoreCase(value);
    }

    private void calculateAverageAndMedianPrice(String economyName, String materialName, String cacheKey) {
        List<Long> prices = this.manager.getPlayerPurchaseItems()
                .filter(item -> item.getPurchaseTime() >= periodStart)
                .filter(item -> item.getEconomy().equalsIgnoreCase(economyName))
                .filter(item -> item.getAuctionType() == AuctionType.DEFAULT)
                .filter(item -> checkItems(item.getItemStackContent(), materialName))
                .map(PlayerItemPurchased::getPrice).sorted().toList();

        CacheEntry entry = new CacheEntry();

        if (!prices.isEmpty()) {
            entry.average = prices.stream().mapToDouble(Long::doubleValue).average().orElse(0);

            int size = prices.size();
            entry.median = size % 2 == 0 ? (prices.get(size / 2 - 1) + prices.get(size / 2)) / 2.0 : prices.get(size / 2);
            entry.itemCount = prices.size();
        } else {
            entry.average = 0;
            entry.median = 0;
            entry.itemCount = 0;
        }

        entry.lastUpdateTime = System.currentTimeMillis();
        cache.put(cacheKey, entry);
    }

    public double getMedian(String economyName, String materialName) {
        updateCacheIfNeeded(economyName, materialName);
        CacheEntry entry = cache.get(buildCacheKey(economyName, materialName));
        return entry != null ? entry.median : 0;
    }

    public double getAverage(String economyName, String materialName) {
        updateCacheIfNeeded(economyName, materialName);
        CacheEntry entry = cache.get(buildCacheKey(economyName, materialName));
        return entry != null ? entry.average : 0;
    }

    public int getItemCount(String economyName, String materialName) {
        updateCacheIfNeeded(economyName, materialName);
        CacheEntry entry = cache.get(buildCacheKey(economyName, materialName));
        return entry != null ? entry.itemCount : 0;
    }

    private String getItemStackInformation(ItemStack itemStack) {

        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasCustomModelData()) {
            return String.valueOf(itemStack.getItemMeta().getCustomModelData());
        }

        return itemStack.getType().name();
    }

    public void sendInformations(ZAuctionPlugin plugin, Player player, String economyName) {
        runAsync(plugin, () -> {

            ItemStack itemStack = player.getItemInHand();
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                message(player, Message.PRICE_ERROR);
                return;
            }

            Optional<AuctionEconomy> optional = plugin.getEconomyManager().getEconomy(economyName);
            if (optional.isEmpty()) {
                message(player, Message.PRICE_ECONOMY, "%economy%", economyName);
                return;
            }
            AuctionEconomy economy = optional.get();

            String materialName = getItemStackInformation(itemStack);
            updateCacheIfNeeded(economyName, materialName);
            CacheEntry entry = cache.get(buildCacheKey(economyName, materialName));

            AuctionManager auctionManager = plugin.getAuctionManager();
            messageWO(player, Message.PRICE_INFORMATIONS,
                    "%amount%", auctionManager.getPriceFormat(entry.itemCount),
                    "%average%", economy.format(auctionManager.getPriceFormat((long) entry.average), (long) entry.average),
                    "%median%", economy.format(auctionManager.getPriceFormat((long) entry.median), (long) entry.median),
                    "%economy%", name(economyName), "%itemName%", getItemName(itemStack));
        });
    }

    public void clear() {
        this.cache.clear();
    }

    public boolean isCache(String materialName, String economyName) {
        return this.isCacheValid(buildCacheKey(economyName, materialName));
    }

    private static class CacheEntry {
        double median;
        double average;
        int itemCount;
        long lastUpdateTime;
    }
}