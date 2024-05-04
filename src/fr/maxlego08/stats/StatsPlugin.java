package fr.maxlego08.stats;

import fr.maxlego08.sarah.MigrationManager;
import fr.maxlego08.stats.command.commands.CommandAuctionPrice;
import fr.maxlego08.stats.command.commands.CommandStats;
import fr.maxlego08.stats.migrations.GlobalEconomyStatsMigration;
import fr.maxlego08.stats.migrations.GlobalStatsMigration;
import fr.maxlego08.stats.migrations.PlayerItemPurchasedMigration;
import fr.maxlego08.stats.migrations.PlayerItemSaleMigration;
import fr.maxlego08.stats.migrations.PlayerStatsMigration;
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
import fr.maxlego08.stats.zmenu.ZMenuManager;
import fr.maxlego08.zauctionhouse.ZAuctionPlugin;
import fr.maxlego08.zauctionhouse.api.AuctionManager;
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
    private ZMenuManager menuManager;

    @Override
    public void onEnable() {

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.setPrefix("zahstats");

        this.files.add("stats");
        this.files.add("stats_items");

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
            if (!this.connection.getDatabaseConnection().isValid()) {
                getLogger().severe("Unable to connect to database !");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }

            this.globalStatsTable = new GlobalStatsTable(this.connection.getDatabaseConnection(), this.getLogger());
            this.globalEconomyStatsTable = new GlobalEconomyStatsTable(this.connection.getDatabaseConnection(), this.getLogger());
            this.playerItemSaleTable = new PlayerItemSaleTable(this.connection.getDatabaseConnection(), this.getLogger());
            this.playerItemPurchasedTable = new PlayerItemPurchasedTable(this.connection.getDatabaseConnection(), this.getLogger());
            this.playerStatsTable = new PlayerStatsTable(this.connection.getDatabaseConnection(), this.getLogger());

            MigrationManager.registerMigration(new GlobalStatsMigration());
            MigrationManager.registerMigration(new GlobalEconomyStatsMigration());
            MigrationManager.registerMigration(new PlayerItemSaleMigration());
            MigrationManager.registerMigration(new PlayerItemPurchasedMigration());
            MigrationManager.registerMigration(new PlayerStatsMigration());
            MigrationManager.execute(this.connection.getConnection(), this.connection.getDatabaseConnection().getDatabaseConfiguration(), this.getLogger());

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

        if (fr.maxlego08.zauctionhouse.api.utils.Config.USE_ZMENU_INVENTORY) {
            this.menuManager = new ZMenuManager(this);
            this.menuManager.loadButtons();
            this.menuManager.loadInventories();
        }

        this.postEnable();
    }

    @Override
    public void onDisable() {

        this.preDisable();

        this.saveFiles();
        this.connection.getDatabaseConnection().disconnect();

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

    public ZMenuManager getMenuManager() {
        return menuManager;
    }

    public AuctionManager getAuctionManager() {
        ZAuctionPlugin auctionPlugin = (ZAuctionPlugin) Bukkit.getPluginManager().getPlugin("zAuctionHouseV3");
        return auctionPlugin.getAuctionManager();
    }
}
