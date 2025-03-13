package fr.maxlego08.stats.storage;

import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.stats.api.TableUtils;
import fr.maxlego08.stats.api.Tables;
import fr.maxlego08.stats.api.economy.EconomyKey;
import fr.maxlego08.stats.api.utils.Pair;
import fr.maxlego08.stats.dto.EconomyDTO;
import fr.maxlego08.stats.zcore.ZPlugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class GlobalEconomyStatsTable extends TableUtils {

    public GlobalEconomyStatsTable(DatabaseConnection connection, Logger logger) {
        super(connection, logger);
    }

    public Map<Pair<EconomyKey, String>, Long> selectAll() throws SQLException {
        Map<Pair<EconomyKey, String>, Long> stats = new HashMap<>();
        this.requestHelper.selectAll(Tables.GLOBAL_ECONOMIES, EconomyDTO.class).forEach(dto -> stats.put(new Pair<>(EconomyKey.valueOf(dto.key()), dto.economy()), dto.value()));
        return stats;
    }


    public void upsertGlobalStatistic(EconomyKey key, String economy, long value) {
        ZPlugin.service.execute(() -> this.requestHelper.upsert(Tables.GLOBAL_ECONOMIES, table -> {
            table.string("key", key.name()).primary();
            table.string("economy", economy).primary();
            table.bigInt("value", value);
        }));
    }
}
