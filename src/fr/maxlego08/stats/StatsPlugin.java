package fr.maxlego08.stats;

import fr.maxlego08.stats.command.commands.CommandAuctionPrice;
import fr.maxlego08.stats.command.commands.CommandStats;
import fr.maxlego08.stats.placeholder.LocalPlaceholder;
import fr.maxlego08.stats.save.Config;
import fr.maxlego08.stats.save.MessageLoader;
import fr.maxlego08.stats.storage.GlobalEconomyStatsTable;
import fr.maxlego08.stats.storage.GlobalStatsTable;
import fr.maxlego08.stats.storage.PlayerItemPurchasedTable;
import fr.maxlego08.stats.storage.PlayerItemSaleTable;
import fr.maxlego08.stats.storage.PlayerStatsTable;
import fr.maxlego08.stats.storage.SqlConnection;
import fr.maxlego08.stats.zcore.ZPlugin;
import fr.maxlego08.zauctionhouse.ZAuctionPlugin;
import fr.maxlego08.zauctionhouse.command.commands.CommandAuction;
import org.bukkit.Bukkit;

import java.sql.SQLException;

/**
 * System to create your plugins very simply Projet:
 * <a href="https://github.com/Maxlego08/TemplatePlugin">https://github.com/Maxlego08/TemplatePlugin</a>
 *
 * @author Maxlego08
 */
public class StatsPlugin extends ZPlugin {

    private StatsManager manager;
    private SqlConnection connection;
    private GlobalStatsTable globalStatsTable;
    private GlobalEconomyStatsTable globalEconomyStatsTable;
    private PlayerItemSaleTable playerItemSaleTable;
    private PlayerItemPurchasedTable playerItemPurchasedTable;
    private PlayerStatsTable playerStatsTable;

    @Override
    public void onEnable() {

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.setPrefix("zahstats");

        this.preEnable();

        this.manager = new StatsManager(this);
        this.saveDefaultConfig();

        this.registerCommand("zahstats", new CommandStats(this), "zahs");

        this.addSave(new MessageLoader(this));
        this.addListener(new StatsListener(this.manager));

        Config.getInstance().load(this);
        this.loadFiles();

        service.execute(() -> {

            this.connection = new SqlConnection(this);
            if (!this.connection.isValid()) {
                getLogger().severe("Unable to connect to database !");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            this.globalStatsTable = new GlobalStatsTable(this.connection);
            this.globalEconomyStatsTable = new GlobalEconomyStatsTable(this.connection);
            this.playerItemSaleTable = new PlayerItemSaleTable(this.connection);
            this.playerItemPurchasedTable = new PlayerItemPurchasedTable(this.connection);
            this.playerStatsTable = new PlayerStatsTable(this.connection);
            try {
                this.globalStatsTable.create();
                this.globalEconomyStatsTable.create();
                this.playerItemSaleTable.create();
                this.playerItemPurchasedTable.create();
                this.playerStatsTable.create();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            try {
                this.manager.setGlobalValues(this.globalStatsTable.selectAll());
                this.manager.setEconomyValues(this.globalEconomyStatsTable.selectAll());
                this.manager.setPlayerSaleItems(this.playerItemSaleTable.selectAll());
                this.manager.setPlayerPurchaseItems(this.playerItemPurchasedTable.selectAll());
                this.manager.setPlayerStats(this.playerStatsTable.selectAll());
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });

        this.manager.registerGlobalPlaceholders();
        this.manager.registerPlayerPlaceholders();
        this.manager.registerRankingPlaceholders();
        this.manager.registerItemPlaceholders();

        ZAuctionPlugin auctionPlugin = (ZAuctionPlugin) Bukkit.getPluginManager().getPlugin("zAuctionHouseV3");
        CommandAuction commandAuction = auctionPlugin.getCommandAuction();
        commandAuction.addSubCommand(new CommandAuctionPrice(auctionPlugin, this));

        this.postEnable();
    }

    @Override
    public void onDisable() {

        this.preDisable();

        this.saveFiles();
        this.connection.disconnect();

        this.postDisable();
    }

    public SqlConnection getConnection() {
        return connection;
    }

    public StatsManager getManager() {
        return manager;
    }

    public GlobalStatsTable getGlobalStatsTable() {
        return globalStatsTable;
    }

    public GlobalEconomyStatsTable getGlobalEconomyStatsTable() {
        return globalEconomyStatsTable;
    }

    public PlayerItemPurchasedTable getPlayerItemPurchasedTable() {
        return playerItemPurchasedTable;
    }

    public PlayerItemSaleTable getPlayerItemSaleTable() {
        return playerItemSaleTable;
    }

    public PlayerStatsTable getPlayerStatsTable() {
        return playerStatsTable;
    }
}
