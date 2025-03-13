package fr.maxlego08.stats.migrations;

import fr.maxlego08.sarah.database.Migration;
import fr.maxlego08.stats.api.Tables;

public class GlobalStatsMigration extends Migration {
    @Override
    public void up() {
        create(Tables.GLOBAL, table -> {
            table.string("key", 255).primary();
            table.string("value", 255);
        });
    }
}
