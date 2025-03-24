package me.eventually.hikarisync.core.task;

import me.eventually.hikarisync.core.HikariSyncCore;
import me.eventually.hikarisync.core.util.LoggingUtil;
import me.eventually.hikarisyncapi.HSAddon;
import me.eventually.hikarisyncapi.structure.SaveReason;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class TimedSaveTask extends BukkitRunnable {
    private final HikariSyncCore core;

    public TimedSaveTask(HikariSyncCore core) {
        this.core = core;
    }

    @Override
    public void run() {
        for (HSAddon addon : HikariSyncCore.getAddonManager().getAddonsByReason(SaveReason.TIMED_SAVE)) {
            Bukkit.getServer().getOnlinePlayers().forEach(player -> addon.saveData(player, core.getDb(), SaveReason.TIMED_SAVE));
        }
        LoggingUtil.logDebug("Saved data for all players.");
    }
}
