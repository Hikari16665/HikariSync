package me.eventually.hikarisyncapi.structure;

import org.bukkit.entity.Player;

/**
 * An enum for Events that can trigger a save.
 * These events are listened by core, you don't need to listen them yourself.
 * Btw: The EventHandler priority is NORMAL.
 *
 * @author Eventually
 */
public enum SaveReason {
    /*
     * Player chat events, async chat will be called in Bukkit runTaskAsynchronously method.
     */
    PLAYER_CHAT,
    PLAYER_CHAT_ASYNC,

    // Player inventory events (Close, Swap)
    PLAYER_INVENTORY_UPDATED,

    /*
     * Player death, respawn, toggle flight, world change, level, statistic etc.
     */
    PLAYER_MOVE,
    PLAYER_DEATH,
    PLAYER_DONE_ADVANCEMENT,
    PLAYER_EXP_CHANGED,
    PLAYER_GAMEMODE_CHANGED,
    PLAYER_HEALTH_CHANGED,
    PLAYER_LEVEL_CHANGED,
    PLAYER_QUIT,
    PLAYER_RESPAWN,

    /* Player statistic
     * Called frequently! (About 2 * player count / second)
     * So be careful when using this event!
     * Use this instead of PLAYER_MOVE can be a better choice.
     */
    PLAYER_STATISTIC_CHANGED,
    PLAYER_TOGGLE_FLIGHT,
    PLAYER_WORLD_CHANGED,

    // Timed save
    TIMED_SAVE
}
