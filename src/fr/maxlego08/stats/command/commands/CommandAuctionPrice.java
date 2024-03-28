package fr.maxlego08.stats.command.commands;

import fr.maxlego08.stats.StatsPlugin;
import fr.maxlego08.zauctionhouse.ZAuctionPlugin;
import fr.maxlego08.zauctionhouse.api.economy.AuctionEconomy;
import fr.maxlego08.zauctionhouse.api.enums.Message;
import fr.maxlego08.zauctionhouse.api.enums.Permission;
import fr.maxlego08.zauctionhouse.api.utils.Config;
import fr.maxlego08.zauctionhouse.command.VCommand;
import fr.maxlego08.zauctionhouse.zcore.utils.commands.CommandType;

import java.util.stream.Collectors;

public class CommandAuctionPrice extends VCommand {

    private final StatsPlugin plugin;

    public CommandAuctionPrice(ZAuctionPlugin plugin, StatsPlugin statsPlugin) {
        super(plugin);
        this.plugin = statsPlugin;
        this.addSubCommand("price");
        this.setPermission(Permission.ZAUCTIONHOUSE_PRICE);
        this.setDescription(Message.DESCRIPTION_PRICE);
        this.addOptionalArg("economy", (a, b) -> plugin.getEconomyManager().getEconomies().stream().map(AuctionEconomy::getName).collect(Collectors.toList()));
        this.onlyPlayers();
    }

    @Override
    protected CommandType perform(ZAuctionPlugin zAuctionPlugin) {

        String economyName = this.argAsString(0, Config.defaultEconomy);
        this.plugin.getManager().getItemPriceStatistics().sendInformations(zAuctionPlugin, player, economyName);

        return CommandType.SUCCESS;
    }
}
