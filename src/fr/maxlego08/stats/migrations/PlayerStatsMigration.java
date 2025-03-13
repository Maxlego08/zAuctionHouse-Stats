package fr.maxlego08.stats.migrations;

import fr.maxlego08.sarah.database.Migration;
import fr.maxlego08.stats.api.Tables;

public class PlayerStatsMigration extends Migration {
    @Override
    public void up() {
        create(Tables.STATS, table -> {
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
