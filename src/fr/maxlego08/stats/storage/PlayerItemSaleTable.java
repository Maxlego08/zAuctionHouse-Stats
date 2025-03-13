package fr.maxlego08.stats.storage;

import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.stats.api.PlayerItemForSale;
import fr.maxlego08.stats.api.TableUtils;
import fr.maxlego08.stats.api.Tables;
import fr.maxlego08.stats.dto.PlayerItemForSaleDTO;
import fr.maxlego08.stats.zcore.ZPlugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PlayerItemSaleTable extends TableUtils {

    public PlayerItemSaleTable(DatabaseConnection connection, Logger logger) {
        super(connection, logger);
    }

    public void insertItemForSale(PlayerItemForSale item) {
        ZPlugin.service.execute(() -> this.requestHelper.insert(Tables.SALES_ITEMS, table -> {
            table.uuid("player_id", item.getPlayerId());
            table.string("player_name", item.getPlayerName());
            table.string("itemstack", item.getItemStack());
            table.bigInt("price", item.getPrice());
            table.string("economy", item.getEconomy());
            table.string("auction_type", item.getAuctionType().name());
            table.bigInt("expire_at", item.getExpireAt());
            table.bigInt("created_at", item.getCreatedAt());
        }));
    }


    public Map<UUID, List<PlayerItemForSale>> selectAll() {
        return this.requestHelper.selectAll(Tables.SALES_ITEMS, PlayerItemForSaleDTO.class).stream().map(PlayerItemForSale::new).collect(Collectors.groupingBy(PlayerItemForSale::getPlayerId));
    }
}
