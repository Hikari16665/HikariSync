package me.eventually.hikarisyncapi.structure;

/**
 * An enum for Events that can trigger a load.
 * These events are listened by core, you don't need to listen them yourself.
 *
 * @author Eventually
 */
public enum LoadReason {
    PLAYER_JOIN,
    PLAYER_WORLD_CHANGED
}
