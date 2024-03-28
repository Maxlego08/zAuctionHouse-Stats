package fr.maxlego08.stats.zmenu;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;
import fr.maxlego08.stats.StatsPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ZMenuManager {

    private final StatsPlugin plugin;
    private final PatternManager patternManager;
    private final ButtonManager buttonManager;
    private final InventoryManager inventoryManager;

    public ZMenuManager(StatsPlugin plugin) {
        this.plugin = plugin;
        this.buttonManager = plugin.getProvider(ButtonManager.class);
        this.inventoryManager = plugin.getProvider(InventoryManager.class);
        this.patternManager = plugin.getProvider(PatternManager.class);
    }

    public void loadButtons() {
        this.buttonManager.unregisters(this.plugin);

    }

    public void loadInventories() {

        File folder = new File(this.plugin.getDataFolder(), "inventories");
        if (!folder.exists()) {
            folder.mkdir();
            // registerDefaultFiles();
        }

        // Load patterns first
        this.loadPatterns();

        this.inventoryManager.deleteInventories(this.plugin);
        this.files(folder, file -> {
            try {
                this.inventoryManager.loadInventory(this.plugin, file);
            } catch (InventoryException exception) {
                exception.printStackTrace();
            }
        });
    }

    private void files(File folder, Consumer<File> consumer) {
        try (Stream<Path> s = Files.walk(Paths.get(folder.getPath()))) {
            s.skip(1).map(Path::toFile).filter(File::isFile).filter(e -> e.getName().endsWith(".yml"))
                    .forEach(consumer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void loadPatterns() {
        File folder = new File(this.plugin.getDataFolder(), "patterns");
        if (!folder.exists()) {
            folder.mkdir();
            registerDefaultPatterns();
        }

        files(folder, file -> {
            try {
                this.patternManager.loadPattern(file);
            } catch (InventoryException exception) {
                exception.printStackTrace();
            }
        });
    }

    private void registerDefaultPatterns() {
        List<String> files = new ArrayList<>();
        // files.add("patterns/auction.yml");

        files.forEach(e -> {
            if (!new File(this.plugin.getDataFolder(), e).exists()) {

                if (NMSUtils.isNewVersion()) {
                    this.plugin.saveResource(e.replace("patterns/", "patterns/1_13/"), e, false);
                } else {
                    this.plugin.saveResource(e, false);
                }
            }
        });
    }

}
