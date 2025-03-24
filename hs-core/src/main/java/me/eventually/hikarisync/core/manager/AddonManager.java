package me.eventually.hikarisync.core.manager;

import me.eventually.hikarisync.core.HikariSyncCore;
import me.eventually.hikarisync.core.util.LoggingUtil;
import me.eventually.hikarisyncapi.HSAddon;
import me.eventually.hikarisyncapi.structure.LoadReason;
import me.eventually.hikarisyncapi.structure.SaveReason;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AddonManager {
    private final List<HSAddon> addons = new ArrayList<>();
    private final Map<LoadReason, Set<HSAddon>> loadReasonsMap = new EnumMap<>(LoadReason.class);
    private final Map<SaveReason, Set<HSAddon>> saveReasonsMap = new EnumMap<>(SaveReason.class);
    private final HikariSyncCore core;

    public AddonManager(HikariSyncCore core) {
        this.core = core;
    }
    private void initReasonMappings(HSAddon addon) {
        for (LoadReason loadReason : addon.getLoadReasons()) {
            loadReasonsMap.computeIfAbsent(loadReason, k -> new HashSet<>()).add(addon);
        }
        for (SaveReason saveReason : addon.getSaveReasons()) {
            saveReasonsMap.computeIfAbsent(saveReason, k -> new HashSet<>()).add(addon);
        }
    }

    /**
     * Unused because we use try-catch class cast to check for addon.
     * @param plugin Java plugin
     * @return is the plugin an addon?
     */
    public boolean isAddon(JavaPlugin plugin) {
        return plugin instanceof HSAddon;
    }
    public boolean loadAddon(JavaPlugin plugin) {
        try {
            HSAddon addon = (HSAddon) plugin;
            addon.init(core.getDb());
            initReasonMappings(addon);
            addons.add(addon);
            Bukkit.getLogger().info("Addon loaded: " + plugin.getName());
            if (initAddon(addon)) {
                return true;
            } else {
                Bukkit.getLogger().severe("Addon failed to init: " + plugin.getName());
                return false;
            }
        } catch (ClassCastException e) {
            return false;
        }
    }
    public boolean loadAddons(Plugin[] plugins) {
        boolean anyLoaded = false;
        for (Plugin plugin : plugins) {
            if (loadAddon((JavaPlugin) plugin)) {
                anyLoaded = true;
            }
        }
        return anyLoaded;
    }
    public boolean initAddon(HSAddon addon){
        addon.init(core.getDb());
        AtomicBoolean error = new AtomicBoolean(false);
        addon.getTableDefinitions().forEach(table -> {
            String createTableScript = table.generateCreateTableScript();
            Bukkit.getLogger().info("Create table script: " + createTableScript);
            try {
                core.getDb().transaction(() -> {
                    core.getDb().execute(createTableScript);
                    return null;
                });
            } catch (Exception e) {
                e.printStackTrace();
                error.set(true);
            }
        });
        return !error.get();
    }
    public List<HSAddon> getAddonClasses() {
        return addons;
    }
    public void disableAddons() {
        addons.forEach(addon -> {
            addon.disable(core.getDb());
        });
    }
    public Set<HSAddon> getAddonsByReason(LoadReason reason) {
        return loadReasonsMap.getOrDefault(reason, Collections.emptySet());
    }

    public Set<HSAddon> getAddonsByReason(SaveReason reason) {
        return saveReasonsMap.getOrDefault(reason, Collections.emptySet());
    }

    public void callWithReason(LoadReason reason, OfflinePlayer p) {
        LoggingUtil.logDebug("Requesting save from addons, reason: " + reason.toString(), reason);
        Set<HSAddon> addons = getAddonsByReason(reason);
        addons.forEach(addon -> {
            addon.loadData(p, core.getDb(), reason);
        });
    }
    public void callWithReason(SaveReason reason, OfflinePlayer p) {
        LoggingUtil.logDebug("Requesting save from addons, reason: " + reason.toString(), reason);
        Set<HSAddon> addons = getAddonsByReason(reason);
        addons.forEach(addon -> {
            addon.saveData(p, core.getDb(), reason);
        });
    }
}
