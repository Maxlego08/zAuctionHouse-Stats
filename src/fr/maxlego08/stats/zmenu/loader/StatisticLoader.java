package fr.maxlego08.stats.zmenu.loader;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.stats.StatsPlugin;
import fr.maxlego08.stats.zmenu.buttons.ButtonItemStatistics;
import fr.maxlego08.zauctionhouse.api.AuctionManager;
import fr.maxlego08.zauctionhouse.api.utils.AuctionConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class StatisticLoader extends ButtonLoader {

    private final AuctionManager auctionManager;
    private final StatsPlugin plugin;

    public StatisticLoader(AuctionManager auctionManager, StatsPlugin plugin) {
        super(plugin, "ZAUCTIONHOUSESTATS_ITEM");
        this.auctionManager = auctionManager;
        this.plugin = plugin;
    }

    @Override
    public Button load(YamlConfiguration yamlConfiguration, String s, DefaultButtonValue defaultButtonValue) {
        String economyName = yamlConfiguration.getString(s + "economy", AuctionConfiguration.defaultEconomy);
        return new ButtonItemStatistics(this.auctionManager, this.plugin, economyName);
    }
}
