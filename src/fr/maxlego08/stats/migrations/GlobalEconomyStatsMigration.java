package fr.maxlego08.stats.migrations;

import fr.maxlego08.sarah.database.Migration;
import fr.maxlego08.stats.api.Tables;

public class GlobalEconomyStatsMigration extends Migration {

    @Override
    public void up() {
        create(Tables.GLOBAL_ECONOMIES, table -> {
            table.string("key", 255).primary();
            table.string("economy", 255).primary();
            table.bigInt("value");
        });
    }
}
