package fr.maxlego08.stats.save;

import fr.maxlego08.stats.StatsPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    public static boolean enableDebug = true;
    public static boolean enableDebugTime = false;
    public static String noName = "✘";
    public static String noAmount = "0";
    public static String loading = "Loading...";
    public static boolean enableNonApplicable = true;
    public static String nonApplicable = "N/A";
    public static long cacheDurationMaterial = 3600000; // 1 hour in millisecondes

    /**
     * static Singleton instance.
     */
    private static volatile Config instance;


    /**
     * Private constructor for singleton.
     */
    private Config() {
    }

    /**
     * Return a singleton instance of Config.
     */
    public static Config getInstance() {
        // Double lock for thread safety.
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    instance = new Config();
                }
            }
        }
        return instance;
    }

    public void load(StatsPlugin plugin) {

        FileConfiguration configuration = plugin.getConfig();
        enableDebug = configuration.getBoolean("enableDebug", false);
        enableDebugTime = configuration.getBoolean("enableDebugTime", false);
        enableNonApplicable = configuration.getBoolean("enableNonApplicable", true);
        noName = configuration.getString("noName", "X");
        noAmount = configuration.getString("noAmount", "0");
        loading = configuration.getString("loading", "0");
        nonApplicable = configuration.getString("nonApplicable", "N/A");
		cacheDurationMaterial = configuration.getLong("cacheDurationMaterial", 3600000);
    }

}
