package me.eventually.hikarisync.core.task;

import me.eventually.hikarisync.core.HikariSyncCore;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class HeartBeatTask extends BukkitRunnable {
    private final HikariSyncCore core;

    public HeartBeatTask(HikariSyncCore core) {
        this.core = core;
    }

    @Override
    public void run() {
        try {
            core.getDb().execute("UPDATE hs_core_servers SET last_heartbeat = NOW() WHERE instance_id = ?", HikariSyncCore.getInstanceId());
        } catch (SQLException ignored) {
        }
    }
}
