package fr.maxlego08.stats.storage;

import fr.maxlego08.stats.api.PlayerStats;
import fr.maxlego08.stats.api.Table;
import fr.maxlego08.stats.zcore.ZPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStatsTable extends Table {

    private final SqlConnection connection;
    private final String tableName = "zah_player_stats";

    public PlayerStatsTable(SqlConnection connection) {
        super(connection);
        this.connection = connection;
    }

    @Override
    protected String getCreate() {
        return "CREATE TABLE IF NOT EXISTS " + this.tableName + " (" +
                "uuid VARCHAR(36) NOT NULL, " +
                "name VARCHAR(255) NOT NULL, " +
                "total_items_sold BIGINT NOT NULL DEFAULT 0, " +
                "total_items_bought BIGINT NOT NULL DEFAULT 0, " +
                "first_item_bought_at BIGINT NOT NULL DEFAULT 0, " +
                "last_item_bought_at BIGINT NOT NULL DEFAULT 0, " +
                "first_item_sold_at BIGINT NOT NULL DEFAULT 0, " +
                "last_item_sold_at BIGINT NOT NULL DEFAULT 0, " +
                "PRIMARY KEY (uuid))";
    }

    public void updatePlayerStats(PlayerStats playerStats) {
        ZPlugin.service.execute(() -> {
            String sql = "INSERT INTO " + this.tableName +
                    " (uuid, name, total_items_sold, total_items_bought, first_item_bought_at, last_item_bought_at, first_item_sold_at, last_item_sold_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                    "name = ?, total_items_sold = ?, total_items_bought = ?, " +
                    "first_item_bought_at = COALESCE(first_item_bought_at, ?), last_item_bought_at = ?, " +
                    "first_item_sold_at = COALESCE(first_item_sold_at, ?), last_item_sold_at = ?";
            try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
                statement.setString(1, playerStats.getUuid().toString());
                statement.setString(2, playerStats.getName());
                statement.setLong(3, playerStats.getTotalItemsSold());
                statement.setLong(4, playerStats.getTotalItemsBought());
                statement.setObject(5, playerStats.getFirstItemBoughtAt());
                statement.setObject(6, playerStats.getLastItemBoughtAt());
                statement.setObject(7, playerStats.getFirstItemSoldAt());
                statement.setObject(8, playerStats.getLastItemSoldAt());

                statement.setString(9, playerStats.getName());
                statement.setLong(10, playerStats.getTotalItemsSold());
                statement.setLong(11, playerStats.getTotalItemsBought());
                statement.setObject(12, playerStats.getFirstItemBoughtAt());
                statement.setObject(13, playerStats.getLastItemBoughtAt());
                statement.setObject(14, playerStats.getFirstItemSoldAt());
                statement.setObject(15, playerStats.getLastItemSoldAt());
                statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }


    public Map<UUID, PlayerStats> selectAll() throws SQLException {
        Map<UUID, PlayerStats> allPlayerStats = new HashMap<>();
        String sql = "SELECT * FROM " + this.tableName;
        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    PlayerStats playerStats = new PlayerStats(
                            uuid,
                            resultSet.getString("name"),
                            resultSet.getInt("total_items_sold"),
                            resultSet.getInt("total_items_bought"),
                            resultSet.getLong("first_item_bought_at"),
                            resultSet.getLong("last_item_bought_at"),
                            resultSet.getLong("first_item_sold_at"),
                            resultSet.getLong("last_item_sold_at")
                    );
                    allPlayerStats.put(uuid, playerStats);
                }
            }
        }
        return allPlayerStats;
    }

}
