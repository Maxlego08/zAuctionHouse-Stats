package fr.maxlego08.stats.storage;

import fr.maxlego08.stats.api.PlayerItemForSale;
import fr.maxlego08.stats.api.Table;
import fr.maxlego08.stats.zcore.ZPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerItemSaleTable extends Table {

    private final SqlConnection connection;
    private final String tableName = "zah_player_sales_items";

    public PlayerItemSaleTable(SqlConnection connection) {
        super(connection);
        this.connection = connection;
    }

    @Override
    protected String getCreate() {
        return "CREATE TABLE IF NOT EXISTS " + this.tableName + " (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "player_id VARCHAR(36) NOT NULL, " +
                "player_name VARCHAR(36) NOT NULL, " +
                "itemstack LONGTEXT NOT NULL, " +
                "price BIGINT NOT NULL, " +
                "economy VARCHAR(32) NOT NULL, " +
                "auction_type VARCHAR(32) NOT NULL, " +
                "created_at BIGINT NOT NULL, " +
                "expire_at BIGINT NOT NULL) ENGINE=InnoDB";
    }

    public void insertItemForSale(PlayerItemForSale item)  {
        ZPlugin.service.execute(() -> {
            String sql = "INSERT INTO " + this.tableName + " (player_id, player_name, itemstack, price, economy, auction_type, expire_at, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, item.getPlayerId().toString());
                statement.setString(2, item.getPlayerName());
                statement.setString(3, item.getItemStack());
                statement.setLong(4, item.getPrice());
                statement.setString(5, item.getEconomy());
                statement.setString(6, item.getAuctionType().name());
                statement.setLong(7, item.getExpireAt());
                statement.setLong(8, item.getCreatedAt());
                int affectedRows = statement.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating item failed, no rows affected.");
                }

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        item.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Creating item failed, no ID obtained.");
                    }
                }
            }catch (Exception exception){
                exception.printStackTrace();
            }
        });
    }


    public Map<UUID, List<PlayerItemForSale>> selectAll() throws SQLException {
        Map<UUID, List<PlayerItemForSale>> items = new HashMap<>();
        String sql = "SELECT * FROM " + this.tableName;
        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("player_id"));
                    PlayerItemForSale item = new PlayerItemForSale(
                            resultSet.getLong("id"),
                            uuid,
                            resultSet.getString("player_name"),
                            resultSet.getString("itemstack"),
                            resultSet.getLong("price"),
                            resultSet.getString("economy"),
                            resultSet.getString("auction_type"),
                            resultSet.getLong("expire_at"),
                            resultSet.getLong("created_at")
                    );
                    items.computeIfAbsent(uuid, u -> new ArrayList<>()).add(item);
                }
            }
        }
        return items;
    }
}
