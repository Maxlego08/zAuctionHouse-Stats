package fr.maxlego08.stats.storage;

import fr.maxlego08.stats.api.global.GlobalKey;
import fr.maxlego08.stats.api.global.GlobalValue;
import fr.maxlego08.stats.api.Table;
import fr.maxlego08.stats.zcore.ZPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;

public class GlobalStatsTable extends Table {

    private final SqlConnection connection;
    private final String tableName = "zah_stats_global";

    public GlobalStatsTable(SqlConnection connection) {
        super(connection);
        this.connection = connection;
    }

    @Override
    protected String getCreate() {
        return "CREATE TABLE IF NOT EXISTS " + this.tableName + " (`key` VARCHAR(255) NOT NULL, `value` VARCHAR(255), PRIMARY KEY (`key`))";
    }

    public void upsert(GlobalKey key, GlobalValue value) {
        ZPlugin.service.execute(() -> {
            try (PreparedStatement statement = connection.getConnection().prepareStatement("INSERT INTO " + this.tableName + " (`key`, `value`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `value` = VALUES(`value`)")) {
                statement.setString(1, key.name());
                statement.setObject(2, value.getValue());
                statement.executeUpdate();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public EnumMap<GlobalKey, GlobalValue> selectAll() throws SQLException {
        EnumMap<GlobalKey, GlobalValue> allEntries = new EnumMap<>(GlobalKey.class);

        try (PreparedStatement statement = connection.getConnection().prepareStatement("SELECT `key`, `value` FROM " + this.tableName)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String keyString = resultSet.getString("key");
                    Object value = resultSet.getObject("value");

                    GlobalKey key = GlobalKey.valueOf(keyString);
                    GlobalValue GlobalValue = new GlobalValue(value);
                    allEntries.put(key, GlobalValue);
                }
            }
        }
        return allEntries;
    }
}
