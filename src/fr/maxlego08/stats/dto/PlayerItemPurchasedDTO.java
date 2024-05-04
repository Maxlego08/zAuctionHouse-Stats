package fr.maxlego08.stats.dto;

import fr.maxlego08.zauctionhouse.api.enums.AuctionType;

import java.util.UUID;

public record PlayerItemPurchasedDTO(
        long id,
        UUID player_id,
        String player_name,
        String itemStack,
        long price,
        String economy,
        UUID seller_id,
        String seller_name,
        long purchase_time,
        AuctionType auction_type
) {}