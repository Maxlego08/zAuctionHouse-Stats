package fr.maxlego08.stats.migrations;

import fr.maxlego08.sarah.SchemaBuilder;
import fr.maxlego08.sarah.database.Migration;

public class PlayerItemSaleMigration extends Migration {
    @Override
    public void up() {
        SchemaBuilder.create(this, "zah_player_sales_items", table -> {
            table.autoIncrement("id");
            table.uuid("player_id");
            table.string("player_name", 36);
            table.longText("itemstack");
            table.bigInt("price");
            table.string("economy", 255);
            table.string("auction_type", 36);
            table.bigInt("created_at");
            table.bigInt("expire_at");
        });
    }
}
