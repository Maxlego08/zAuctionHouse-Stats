package fr.maxlego08.stats.storage;

import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.stats.api.PlayerItemPurchased;
import fr.maxlego08.stats.api.TableUtils;
import fr.maxlego08.stats.api.Tables;
import fr.maxlego08.stats.dto.PlayerItemPurchasedDTO;
import fr.maxlego08.stats.zcore.ZPlugin;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PlayerItemPurchasedTable extends TableUtils {

    public PlayerItemPurchasedTable(DatabaseConnection connection, Logger logger) {
        super(connection, logger);
    }

    public void insertItemPurchased(PlayerItemPurchased item) {
        ZPlugin.service.execute(() -> {
            this.requestHelper.insert(Tables.PURCHASE_ITEMS, table -> {
                table.uuid("player_id", item.getPlayerId());
                table.string("player_name", item.getPlayerName());
                table.string("itemstack", item.getItemStack());
                table.bigInt("price", item.getPrice());
                table.string("economy", item.getEconomy());
                table.uuid("seller_id", item.getSellerId());
                table.string("seller_name", item.getSellerName());
                table.bigInt("purchase_time", System.currentTimeMillis());
                table.string("auction_type", item.getAuctionType().name());
            });
        });
    }


    public Map<UUID, List<PlayerItemPurchased>> selectAll() throws SQLException {
        return this.requestHelper.selectAll(Tables.PURCHASE_ITEMS, PlayerItemPurchasedDTO.class).stream().map(PlayerItemPurchased::new).collect(Collectors.groupingBy(PlayerItemPurchased::getPlayerId));
    }
}
