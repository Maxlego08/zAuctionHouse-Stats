package fr.maxlego08.stats.command.commands;

import fr.maxlego08.stats.StatsPlugin;
import fr.maxlego08.stats.command.VCommand;
import fr.maxlego08.stats.zcore.enums.Permission;
import fr.maxlego08.stats.zcore.utils.commands.CommandType;

public class CommandStats extends VCommand {

	public CommandStats(StatsPlugin plugin) {
		super(plugin);
		this.setPermission(Permission.ZAUCTIONHOUSE_STATS_USE);
		this.addSubCommand(new CommandStatsReload(plugin));
		this.addSubCommand(new CommandStatsClearCache(plugin));
	}

	@Override
	protected CommandType perform(StatsPlugin plugin) {
		syntaxMessage();
		return CommandType.SUCCESS;
	}

}
