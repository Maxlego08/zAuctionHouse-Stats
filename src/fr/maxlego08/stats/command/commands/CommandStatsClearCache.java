package fr.maxlego08.stats.command.commands;

import fr.maxlego08.stats.StatsPlugin;
import fr.maxlego08.stats.command.VCommand;
import fr.maxlego08.stats.save.Config;
import fr.maxlego08.stats.zcore.enums.Message;
import fr.maxlego08.stats.zcore.enums.Permission;
import fr.maxlego08.stats.zcore.utils.commands.CommandType;

public class CommandStatsClearCache extends VCommand {

	public CommandStatsClearCache(StatsPlugin plugin) {
		super(plugin);
		this.setPermission(Permission.ZAUCTIONHOUSE_STATS_CLEARCACHE);
		this.addSubCommand("clearcache", "cc");
		this.setDescription(Message.DESCRIPTION_CLEARCACHE);
	}

	@Override
	protected CommandType perform(StatsPlugin plugin) {

		plugin.getManager().clearCaches();
		message(sender, Message.CLEAR);
		
		return CommandType.SUCCESS;
	}

}
