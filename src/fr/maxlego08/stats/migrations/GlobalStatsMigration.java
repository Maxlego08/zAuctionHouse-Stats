package fr.maxlego08.stats.migrations;

import fr.maxlego08.sarah.SchemaBuilder;
import fr.maxlego08.sarah.database.Migration;

public class GlobalStatsMigration extends Migration {
    @Override
    public void up() {
        SchemaBuilder.create(this, "zah_stats_global", table -> {
            table.string("key", 255);
            table.string("value", 255);
        });
    }
}
