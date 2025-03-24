package me.eventually.hikarisync.core.util;

import me.eventually.hikarisync.core.HikariSyncCore;
import me.eventually.hikarisyncapi.structure.LoadReason;
import me.eventually.hikarisyncapi.structure.SaveReason;

public class LoggingUtil {
    public static void logDebug(String message) {
        if (HikariSyncCore.isDebug()) {
            HikariSyncCore.getInstance().getLogger().info(message);
        }
    }
    public static void logDebug(String message, LoadReason reason) {
        logDebug(message);
    }
    public static void logDebug(String message, SaveReason reason) {
        if (
                reason == SaveReason.PLAYER_STATISTIC_CHANGED ||
                reason == SaveReason.PLAYER_MOVE
        ) {
            if (!HikariSyncCore.isFull_debug()) return;
        }
        logDebug(message);
    }
}
