package me.eventually.hikarisync.core.listener;

import me.eventually.hikarisync.core.HikariSyncCore;
import me.eventually.hikarisync.core.util.LoggingUtil;
import me.eventually.hikarisyncapi.structure.LoadReason;
import me.eventually.hikarisyncapi.structure.SaveReason;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;

import java.sql.SQLException;

public class PlayerListener implements Listener {
    private final HikariSyncCore core;
    private final boolean load_on_join;
    private final boolean save_on_quit;
    private final boolean save_on_inventory_close;
    private final boolean save_on_death;

    public PlayerListener(HikariSyncCore core) {
        this.core = core;
        load_on_join = core.getConfig().getBoolean("load-on-join");
        save_on_quit = core.getConfig().getBoolean("save-on-quit");
        save_on_inventory_close = core.getConfig().getBoolean("save-on-inventory-close");
        save_on_death = core.getConfig().getBoolean("save-on-death");
    }

    /** Handle player login attempt, kick if player has already logged in any data-sync server
     * Tests show that this solution looks not acceptable, but it works.
     * This is the highest priority event handler, to prevent any other plugins from interfering with this.
     * @param event Bukkit PlayerLoginEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void handlePlayerLoginAttempt(PlayerLoginEvent event){
        LoggingUtil.logDebug("Querying player uuid storing");
        try {
            core.getDb().insert("hs_core_players", event.getPlayer().getUniqueId().toString(), HikariSyncCore.getInstanceId());
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // duplicate entry, means multiple login
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You have already logged in other data-sync server.");
            }else {
                e.printStackTrace();
                event.getPlayer().kickPlayer("An error occurred while logging in!");
            }
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!load_on_join) return;
        HikariSyncCore.getAddonManager().callWithReason(
                LoadReason.PLAYER_JOIN,
                event.getPlayer()
        );
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // delete uuid entry from database
        LoggingUtil.logDebug("Executing delete for player uuid storing");
        try {
            HikariSyncCore.getInstance().getDb().execute("DELETE FROM hs_core_players WHERE uuid = ?", event.getPlayer().getUniqueId().toString());
        } catch (SQLException ignored) {
        }
        if (!save_on_quit) return;
        HikariSyncCore.getAddonManager().callWithReason(
                SaveReason.PLAYER_QUIT,
                event.getPlayer()
        );
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!save_on_death) return;
        HikariSyncCore.getAddonManager().callWithReason(
                SaveReason.PLAYER_DEATH,
                event.getEntity().getPlayer()
        );
    }
    @EventHandler
    public void onPlayerInventoryClose(InventoryCloseEvent event) {
        if (!save_on_inventory_close) return;
        HikariSyncCore.getAddonManager().callWithReason(
                SaveReason.PLAYER_INVENTORY_UPDATED,
                (OfflinePlayer) event.getPlayer()
        );
    }
    @EventHandler
    public void onPlayerSwap(PlayerSwapHandItemsEvent event){
        if (event.isCancelled()) return;
        HikariSyncCore.getAddonManager().callWithReason(
                SaveReason.PLAYER_INVENTORY_UPDATED,
                event.getPlayer()
        );
    }
    @EventHandler
    public void onPlayerChat(PlayerChatEvent event){
        if (event.isCancelled()) return;
        HikariSyncCore.getAddonManager().callWithReason(
                SaveReason.PLAYER_CHAT,
                event.getPlayer()
        );
    }
    @EventHandler
    public void onPlayerChatAsync(AsyncPlayerChatEvent event){
        if (event.isCancelled()) return;
        Bukkit.getScheduler().runTaskAsynchronously(
                HikariSyncCore.getInstance(),
                () -> HikariSyncCore.getAddonManager().callWithReason(
                        SaveReason.PLAYER_CHAT_ASYNC,
                        event.getPlayer()
                )
        );
    }
    @EventHandler
    public void onPlayerLevelUpdate(PlayerLevelChangeEvent event) {
        HikariSyncCore.getAddonManager().callWithReason(
                SaveReason.PLAYER_LEVEL_CHANGED,
                event.getPlayer()
        );
    }
    @EventHandler
    public void onPlayerExpUpdate(PlayerExpChangeEvent event) {
        HikariSyncCore.getAddonManager().callWithReason(
                SaveReason.PLAYER_EXP_CHANGED,
                event.getPlayer()
        );
    }
    @EventHandler
    public void onPlayerStatisticUpdate(PlayerStatisticIncrementEvent event) {
        HikariSyncCore.getAddonManager().callWithReason(
                SaveReason.PLAYER_STATISTIC_CHANGED,
                event.getPlayer()
        );
    }
    @EventHandler
    public void onPlayerHealthUpdate(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof OfflinePlayer offlinePlayer)) return;
        HikariSyncCore.getAddonManager().callWithReason(
                SaveReason.PLAYER_HEALTH_CHANGED,
                offlinePlayer
        );
    }
    @EventHandler
    public void onPlayerHurt(EntityDamageByBlockEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof OfflinePlayer offlinePlayer)) return;
        HikariSyncCore.getAddonManager().callWithReason(
                SaveReason.PLAYER_HEALTH_CHANGED,
                offlinePlayer
        );
    }
    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        HikariSyncCore.getAddonManager().callWithReason(
                SaveReason.PLAYER_DONE_ADVANCEMENT,
                event.getPlayer()
        );
    }
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        if (event.isCancelled()) return;
        HikariSyncCore.getAddonManager().callWithReason(
                SaveReason.PLAYER_TOGGLE_FLIGHT,
                event.getPlayer()
        );
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        HikariSyncCore.getAddonManager().callWithReason(
                SaveReason.PLAYER_RESPAWN,
                event.getPlayer()
        );
    }
    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        HikariSyncCore.getAddonManager().callWithReason(
                SaveReason.PLAYER_WORLD_CHANGED,
                event.getPlayer()
        );
    }
    @EventHandler
    public void onPlayerGamemodeChange(PlayerGameModeChangeEvent event) {
        HikariSyncCore.getAddonManager().callWithReason(
                SaveReason.PLAYER_GAMEMODE_CHANGED,
                event.getPlayer()
        );
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        HikariSyncCore.getAddonManager().callWithReason(
                SaveReason.PLAYER_MOVE,
                event.getPlayer()
        );
    }
}
