package fr.maxlego08.stats.storage;

import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.stats.api.TableUtils;
import fr.maxlego08.stats.api.Tables;
import fr.maxlego08.stats.api.global.GlobalKey;
import fr.maxlego08.stats.api.global.GlobalValue;
import fr.maxlego08.stats.dto.GlobalStatsDTO;
import fr.maxlego08.stats.zcore.ZPlugin;

import java.sql.SQLException;
import java.util.EnumMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GlobalStatsTable extends TableUtils {

    public GlobalStatsTable(DatabaseConnection connection, Logger logger) {
        super(connection, logger);
    }

    public void upsert(GlobalKey key, GlobalValue value) {
        ZPlugin.service.execute(() -> this.requestHelper.upsert(Tables.GLOBAL, table -> {
            table.string("key", key.name()).primary();
            table.object("value", value.getValue());
        }));
    }


    public EnumMap<GlobalKey, GlobalValue> selectAll() throws SQLException {
        return this.requestHelper
                .selectAll(Tables.GLOBAL, GlobalStatsDTO.class)
                .stream()
                .collect(Collectors.toMap(
                        dto -> GlobalKey.valueOf(dto.key()),
                        dto -> new GlobalValue(dto.value()),
                        (existing, replacement) -> existing,
                        () -> new EnumMap<>(GlobalKey.class)
                ));
    }
}
