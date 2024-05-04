package fr.maxlego08.stats.dto;

import fr.maxlego08.zauctionhouse.api.enums.AuctionType;

import java.util.UUID;

public record PlayerItemForSaleDTO(long id, UUID player_id, String player_name, String itemStack, long price,
                                   String economy, AuctionType auction_type, long expire_at, long created_at) {
}
