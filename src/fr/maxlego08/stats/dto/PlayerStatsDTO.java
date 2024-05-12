package fr.maxlego08.stats.dto;

import java.util.UUID;

public record PlayerStatsDTO(UUID uuid, String name, long total_items_sold, long total_items_bought,
                             long first_item_bought_at,
                             long last_item_bought_at, long first_item_sold_at, long last_item_sold_at) {
}
