package fr.maxlego08.stats;

import fr.maxlego08.stats.api.economy.EconomyKey;
import fr.maxlego08.stats.api.global.GlobalKey;
import fr.maxlego08.stats.api.global.GlobalValue;
import fr.maxlego08.stats.api.utils.Pair;
import fr.maxlego08.stats.placeholder.LocalPlaceholder;
import fr.maxlego08.stats.zcore.utils.ZUtils;
import fr.maxlego08.zauctionhouse.api.economy.AuctionEconomy;
import org.bukkit.event.Listener;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class StatsManager extends ZUtils implements Listener {

    private final StatsPlugin plugin;
    private EnumMap<GlobalKey, GlobalValue> globalValues;
    private Map<Pair<EconomyKey, String>, Long> economyValues = new HashMap<>();

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

    public GlobalValue getValue(GlobalKey globalKey) {
        return this.globalValues.computeIfAbsent(globalKey, i -> new GlobalValue(0));
    }

    public void addOne(GlobalKey globalKey) {
        getValue(globalKey).add(1L);
        this.plugin.getGlobalStatsTable().upsert(globalKey, getValue(globalKey));
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
