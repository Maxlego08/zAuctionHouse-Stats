package fr.maxlego08.stats.storage;

import fr.maxlego08.stats.api.Table;
import fr.maxlego08.stats.api.economy.EconomyKey;
import fr.maxlego08.stats.api.utils.Pair;
import fr.maxlego08.stats.zcore.ZPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GlobalEconomyStatsTable extends Table {

    private final SqlConnection connection;
    private final String tableName = "zah_stats_global_economies";

    public GlobalEconomyStatsTable(SqlConnection connection) {
        super(connection);
        this.connection = connection;
    }

    @Override
    protected String getCreate() {
        return "CREATE TABLE IF NOT EXISTS " + this.tableName + " (`key` VARCHAR(255) NOT NULL, `economy` VARCHAR(255) NOT NULL, `value` BIGINT NOT NULL, PRIMARY KEY (`key`, `economy`))";
    }

    public Map<Pair<EconomyKey, String>, Long> selectAll() throws SQLException {
        Map<Pair<EconomyKey, String>, Long> stats = new HashMap<>();
        try (PreparedStatement statement = connection.getConnection().prepareStatement("SELECT `key`, `economy`, `value` FROM " + this.tableName)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    EconomyKey key = EconomyKey.valueOf(resultSet.getString("key"));
                    String economy = resultSet.getString("economy");
                    Long value = resultSet.getLong("value");
                    stats.put(new Pair<>(key, economy), value);
                }
            }
        }
        return stats;
    }


    public void upsertGlobalStatistic(EconomyKey key, String economy, long value) {
        ZPlugin.service.execute(() -> {
            try (PreparedStatement statement = connection.getConnection().prepareStatement(
                    "INSERT INTO " + this.tableName + " (`key`, `economy`, `value`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `value` = VALUES(`value`)")) {
                statement.setString(1, key.name());
                statement.setString(2, economy);
                statement.setLong(3, value);
                statement.executeUpdate();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

}
