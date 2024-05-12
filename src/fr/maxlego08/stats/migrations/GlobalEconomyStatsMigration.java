package fr.maxlego08.stats.migrations;

import fr.maxlego08.sarah.SchemaBuilder;
import fr.maxlego08.sarah.database.Migration;

public class GlobalEconomyStatsMigration extends Migration {

    @Override
    public void up() {
        SchemaBuilder.create(this, "zah_stats_global_economies", table -> {
            table.string("key", 255).primary();
            table.string("economy", 255).primary();
            table.bigInt("value");
        });
    }
}
