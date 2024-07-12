package fr.maxlego08.stats.storage;

import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.stats.api.PlayerStats;
import fr.maxlego08.stats.api.TableUtils;
import fr.maxlego08.stats.dto.PlayerStatsDTO;
import fr.maxlego08.stats.zcore.ZPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PlayerStatsTable extends TableUtils {

    public PlayerStatsTable(DatabaseConnection connection, Logger logger) {
        super(connection, logger);
    }

    public void updatePlayerStats(PlayerStats playerStats) {
        ZPlugin.service.execute(() -> this.requestHelper.upsert("zah_player_stats", table -> {
            table.uuid("uuid", playerStats.getUuid()).primary();
            table.string("name", playerStats.getName());
            table.bigInt("total_items_sold", playerStats.getTotalItemsSold());
            table.bigInt("total_items_bought", playerStats.getTotalItemsBought());
            table.bigInt("first_item_bought_at", playerStats.getFirstItemBoughtAt());
            table.bigInt("last_item_bought_at", playerStats.getLastItemBoughtAt());
            table.bigInt("first_item_sold_at", playerStats.getFirstItemSoldAt());
            table.bigInt("last_item_sold_at", playerStats.getLastItemSoldAt());
        }));
    }


    public Map<UUID, PlayerStats> selectAll() {
        return this.requestHelper.selectAll("zah_player_stats", PlayerStatsDTO.class).stream().collect(Collectors.toMap(PlayerStatsDTO::uuid, PlayerStats::new));
    }

}
