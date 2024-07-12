package fr.maxlego08.stats.zmenu.loader;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.stats.StatsPlugin;
import fr.maxlego08.stats.zmenu.buttons.ButtonItemStatistics;
import fr.maxlego08.zauctionhouse.api.AuctionManager;
import fr.maxlego08.zauctionhouse.api.utils.AuctionConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class StatisticLoader implements ButtonLoader {

    private final AuctionManager auctionManager;
    private final StatsPlugin plugin;

    public StatisticLoader(AuctionManager auctionManager, StatsPlugin plugin) {
        this.auctionManager = auctionManager;
        this.plugin = plugin;
    }


    @Override
    public Class<? extends Button> getButton() {
        return ButtonItemStatistics.class;
    }

    @Override
    public String getName() {
        return "ZAUCTIONHOUSESTATS_ITEM";
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public Button load(YamlConfiguration yamlConfiguration, String s, DefaultButtonValue defaultButtonValue) {
        String economyName = yamlConfiguration.getString(s + "economy", AuctionConfiguration.defaultEconomy);
        return new ButtonItemStatistics(this.auctionManager, this.plugin, economyName);
    }
}
