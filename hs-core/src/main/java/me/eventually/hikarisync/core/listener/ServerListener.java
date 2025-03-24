package me.eventually.hikarisync.core.listener;

import me.eventually.hikarisync.core.HikariSyncCore;
import me.eventually.hikarisync.core.util.LoggingUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerListener implements Listener {
    private final HikariSyncCore core;

    public ServerListener(HikariSyncCore core) {
        this.core = core;
    }

    /**
     * If server successfully start up, core.initCore() will be called.
     * @param event Bukkit ServerLoadEvent
     */
    @EventHandler
    public void onServerStart(ServerLoadEvent event){
        if (event.getType() == ServerLoadEvent.LoadType.STARTUP){
            LoggingUtil.logDebug("Server startup completed. HikariSync-Core loading...");
            core.initCore();
        }
    }
}
