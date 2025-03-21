package me.eventually.hikarisync.core.manager;

import me.eventually.hikarisync.core.HikariSyncCore;
import me.eventually.hikarisyncapi.HSAddon;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AddonManager {
    private final List<HSAddon> addons = new ArrayList<>();
    private final HikariSyncCore core;

    public AddonManager(HikariSyncCore core) {
        this.core = core;
    }

    public boolean isAddon(Plugin plugin) {
        return plugin instanceof HSAddon;
    }
    public boolean loadAddon(Plugin plugin) {
        if (isAddon(plugin)) {
            HSAddon addon = (HSAddon) plugin;
            addon.init(core.getDb());
            addons.add(addon);
            return true;
        } else {
            return false;
        }
    }
    public boolean loadAddons(Plugin[] plugins) {
        boolean anyLoaded = false;
        for (Plugin plugin : plugins) {
            if (loadAddon(plugin)) {
                anyLoaded = true;
            }
        }
        return anyLoaded;
    }
    public boolean initAddon(HSAddon addon){
        addon.init(core.getDb());
        AtomicBoolean error = new AtomicBoolean(false);
        addon.getTableDefinitions().forEach(table -> {
                try {
                    String createTableScript = table.generateCreateTableScript();
                    core.getDb().transaction(() -> {
                        try {
                            core.getDb().execute(createTableScript);
                        } catch (Exception e) {
                            throw new RuntimeException();
                        }
                        return null;
                    });
                }catch (Exception e){
                    error.set(true);
                    e.printStackTrace();
                }
            }
        );
        return error.get();
    }
    public boolean initAddons() {
        AtomicBoolean anyLoaded = new AtomicBoolean(false);
        addons.forEach(addon -> {
            if(initAddon(addon)) {
                anyLoaded.set(true);
            }
        });
        return anyLoaded.get();
    }
    public List<HSAddon> getAddonClasses() {
        return addons;
    }
    public void disableAddons() {
        addons.forEach(addon -> {
            addon.disable(core.getDb());
        });
    }
}
