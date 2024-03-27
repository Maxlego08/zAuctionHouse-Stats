package fr.maxlego08.stats;

import fr.maxlego08.stats.command.commands.CommandStats;
import fr.maxlego08.stats.placeholder.LocalPlaceholder;
import fr.maxlego08.stats.save.Config;
import fr.maxlego08.stats.save.MessageLoader;
import fr.maxlego08.stats.zcore.ZPlugin;

/**
 * System to create your plugins very simply Projet:
 * <a href="https://github.com/Maxlego08/TemplatePlugin">https://github.com/Maxlego08/TemplatePlugin</a>
 *
 * @author Maxlego08
 */
public class StatsPlugin extends ZPlugin {

    @Override
    public void onEnable() {

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.setPrefix("zahstats");

        this.preEnable();

        this.saveDefaultConfig();

        this.registerCommand("zahstats", new CommandStats(this), "zahs");

        this.addSave(new MessageLoader(this));

        Config.getInstance().load(this);
        this.loadFiles();

        this.postEnable();
    }

    @Override
    public void onDisable() {

        this.preDisable();

        this.saveFiles();

        this.postDisable();
    }

}
