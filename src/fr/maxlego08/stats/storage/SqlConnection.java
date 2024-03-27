package fr.maxlego08.stats.storage;

import fr.maxlego08.stats.StatsPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SqlConnection {

    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;
    private Connection connection;

    public SqlConnection(StatsPlugin plugin) {
        FileConfiguration configuration = plugin.getConfig();
        this.host = configuration.getString("sql.host");
        this.port = configuration.getInt("sql.port");
        this.database = configuration.getString("sql.database");
        this.user = configuration.getString("sql.user");
        this.password = configuration.getString("sql.password");
    }

    public boolean isValid() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
            return false;
        }

        if (!isConnected(connection)) {
            try {
                Properties properties = new Properties();
                properties.setProperty("useSSL", "false");
                properties.setProperty("user", user);
                properties.setProperty("password", password);
                Connection temp_connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, properties);

                if (isConnected(temp_connection)) {
                    temp_connection.close();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
                return false;
            }
        }

        return true;
    }

    private boolean isConnected(Connection con) {
        if (con == null) {
            return false;
        }

        try {
            return con.isValid(1);
        } catch (SQLException e) {
            return false;
        }
    }

    public void disconnect() {
        if (isConnected(connection)) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void connect() {
        if (!isConnected(connection)) {
            try {
                Properties properties = new Properties();
                properties.setProperty("useSSL", "false");
                properties.setProperty("user", user);
                properties.setProperty("password", password);
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, properties);
            } catch (SQLException err) {
                err.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        connect();
        return connection;
    }

}
