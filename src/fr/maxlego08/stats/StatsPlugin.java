package fr.maxlego08.stats;

import fr.maxlego08.stats.command.commands.CommandStats;
import fr.maxlego08.stats.placeholder.LocalPlaceholder;
import fr.maxlego08.stats.save.Config;
import fr.maxlego08.stats.save.MessageLoader;
import fr.maxlego08.stats.storage.GlobalStatsTable;
import fr.maxlego08.stats.storage.SqlConnection;
import fr.maxlego08.stats.zcore.ZPlugin;
import org.bukkit.Bukkit;

import java.sql.SQLException;

/**
 * System to create your plugins very simply Projet:
 * <a href="https://github.com/Maxlego08/TemplatePlugin">https://github.com/Maxlego08/TemplatePlugin</a>
 *
 * @author Maxlego08
 */
public class StatsPlugin extends ZPlugin {

    private final StatsManager manager = new StatsManager(this);
    private SqlConnection connection;
    private GlobalStatsTable globalStatsTable;

    @Override
    public void onEnable() {

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.setPrefix("zahstats");

        this.preEnable();

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
            try {
                this.globalStatsTable.create();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            try {
                this.manager.setGlobalValues(this.globalStatsTable.selectAll());
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });

        this.manager.registerGlobalPlaceholders();

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
}
