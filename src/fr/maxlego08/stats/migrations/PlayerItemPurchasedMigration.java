package fr.maxlego08.stats.migrations;

import fr.maxlego08.sarah.SchemaBuilder;
import fr.maxlego08.sarah.database.Migration;

public class PlayerItemPurchasedMigration extends Migration {
    @Override
    public void up() {
        SchemaBuilder.create(this, "zah_player_purchased_items", table -> {
            table.autoIncrement("id");
            table.uuid("player_id");
            table.string("player_name", 36);
            table.longText("itemstack");
            table.bigInt("price");
            table.string("economy", 255);
            table.uuid("seller_id");
            table.string("seller_name", 36);
            table.string("auction_type", 36);
            table.bigInt("purchase_time");
        });
    }
}
