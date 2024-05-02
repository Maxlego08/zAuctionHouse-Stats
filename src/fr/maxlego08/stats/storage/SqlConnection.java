package fr.maxlego08.stats.storage;

import fr.maxlego08.sarah.DatabaseConfiguration;
import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.sarah.MySqlConnection;
import fr.maxlego08.stats.StatsPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;

public class SqlConnection {

    private final DatabaseConnection databaseConnection;

    public SqlConnection(StatsPlugin plugin) {
        FileConfiguration configuration = plugin.getConfig();
        boolean debug = configuration.getBoolean("sql.debug", false);
        String host = configuration.getString("sql.host");
        int port = configuration.getInt("sql.port");
        String database = configuration.getString("sql.database");
        String user = configuration.getString("sql.user");
        String password = configuration.getString("sql.password");
        DatabaseConfiguration databaseConfiguration = DatabaseConfiguration.create(user, password, port, host, database, debug);
        this.databaseConnection = new MySqlConnection(databaseConfiguration);
    }

    public Connection getConnection() {
        return databaseConnection.getConnection();
    }

    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }
}
