package fr.maxlego08.stats;

import fr.maxlego08.stats.api.GlobalKey;
import fr.maxlego08.stats.api.GlobalValue;
import fr.maxlego08.stats.placeholder.LocalPlaceholder;
import fr.maxlego08.stats.zcore.utils.ZUtils;
import org.bukkit.event.Listener;

import java.util.EnumMap;

public class StatsManager extends ZUtils implements Listener {

    private final StatsPlugin plugin;
    private EnumMap<GlobalKey, GlobalValue> globalValues;

    public StatsManager(StatsPlugin plugin) {
        this.plugin = plugin;
        this.globalValues = new EnumMap<>(GlobalKey.class);
    }

    public void setGlobalValues(EnumMap<GlobalKey, GlobalValue> globalValues) {
        this.globalValues = globalValues;
    }

    public GlobalValue getValue(GlobalKey globalKey) {
        return this.globalValues.computeIfAbsent(globalKey, i -> new GlobalValue(0));
    }

    public void addOne(GlobalKey globalKey) {
        getValue(globalKey).add(1);
        this.update(globalKey);
    }

    private void update(GlobalKey globalKey) {
        this.plugin.getGlobalStatsTable().upsert(globalKey, getValue(globalKey));
    }

    public void registerGlobalPlaceholders() {

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        for (GlobalKey key : GlobalKey.values()) {
            placeholder.register(key.name().toLowerCase(), player -> getValue(key).asString());
        }
    }
}
