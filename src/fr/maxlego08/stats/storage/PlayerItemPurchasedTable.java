package fr.maxlego08.stats.storage;

import fr.maxlego08.stats.api.PlayerItemPurchased;
import fr.maxlego08.stats.api.Table;
import fr.maxlego08.stats.zcore.ZPlugin;
import fr.maxlego08.zauctionhouse.api.enums.AuctionType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerItemPurchasedTable extends Table {

    private final SqlConnection connection;
    private final String tableName = "zah_player_purchased_items";

    public PlayerItemPurchasedTable(SqlConnection connection) {
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
                "seller_id VARCHAR(36) NOT NULL, " +
                "seller_name VARCHAR(36) NOT NULL, " +
                "auction_type VARCHAR(32) NOT NULL, " +
                "purchase_time BIGINT NOT NULL) ENGINE=InnoDB";
    }

    public void insertItemPurchased(PlayerItemPurchased item) {
        ZPlugin.service.execute(() -> {
            String sql = "INSERT INTO " + this.tableName + " (player_id, player_name, itemstack, price, economy, seller_id, seller_name, purchase_time, auction_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, item.getPlayerId().toString());
                statement.setString(2, item.getPlayerName());
                statement.setString(3, item.getItemStack());
                statement.setLong(4, item.getPrice());
                statement.setString(5, item.getEconomy());
                statement.setString(6, item.getSellerId().toString());
                statement.setString(7, item.getSellerName());
                statement.setLong(8, System.currentTimeMillis());
                statement.setString(9, item.getAuctionType().name());
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
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public Map<UUID, List<PlayerItemPurchased>> selectAll() throws SQLException {
        Map<UUID, List<PlayerItemPurchased>> items = new HashMap<>();
        String sql = "SELECT * FROM " + this.tableName;
        try (PreparedStatement statement = connection.getConnection().prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("player_id"));
                    PlayerItemPurchased item = new PlayerItemPurchased(
                            uuid,
                            resultSet.getString("player_name"),
                            resultSet.getString("itemstack"),
                            resultSet.getLong("price"),
                            resultSet.getString("economy"),
                            UUID.fromString(resultSet.getString("seller_id")),
                            resultSet.getString("seller_name"),
                            resultSet.getLong("purchase_time"),
                            AuctionType.valueOf(resultSet.getString("auction_type")));
                    item.setId(resultSet.getLong("id"));
                    items.computeIfAbsent(uuid, u -> new ArrayList<>()).add(item);
                }
            }
        }
        return items;
    }
}
