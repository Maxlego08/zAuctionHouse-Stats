package fr.maxlego08.stats.migrations;

import fr.maxlego08.sarah.SchemaBuilder;
import fr.maxlego08.sarah.database.Migration;

public class PlayerStatsMigration extends Migration {
    @Override
    public void up() {
        SchemaBuilder.create(this, "zah_player_stats", table -> {
            table.uuid("uuid").primary();
            table.string("name", 255);
            table.bigInt("total_items_sold").defaultValue("0");
            table.bigInt("total_items_bought").defaultValue("0");
            table.bigInt("first_item_bought_at").defaultValue("0");
            table.bigInt("last_item_bought_at").defaultValue("0");
            table.bigInt("first_item_sold_at").defaultValue("0");
            table.bigInt("last_item_sold_at").defaultValue("0");
        });
    }
}
