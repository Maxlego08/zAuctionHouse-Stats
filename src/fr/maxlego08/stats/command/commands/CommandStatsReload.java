package fr.maxlego08.stats.command.commands;

import fr.maxlego08.stats.StatsPlugin;
import fr.maxlego08.stats.command.VCommand;
import fr.maxlego08.stats.save.Config;
import fr.maxlego08.stats.zcore.enums.Message;
import fr.maxlego08.stats.zcore.enums.Permission;
import fr.maxlego08.stats.zcore.utils.commands.CommandType;

public class CommandStatsReload extends VCommand {

    public CommandStatsReload(StatsPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZAUCTIONHOUSE_STATS_RELOAD);
        this.addSubCommand("reload", "rl");
        this.setDescription(Message.DESCRIPTION_RELOAD);
    }

    @Override
    protected CommandType perform(StatsPlugin plugin) {

        plugin.reloadConfig();
        Config.getInstance().load(plugin);
        plugin.reloadFiles();
        if (plugin.getMenuManager() != null) plugin.getMenuManager().loadInventories();
        message(sender, Message.RELOAD);

        return CommandType.SUCCESS;
    }

}
