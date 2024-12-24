package me.mgin.graves.config.enums;

public enum GraveMergeOrder {
    // Current behavior. It returns your state to when you died, and merges your current items in afterwards.
    GRAVE,
    // Merges the grave items into your current inventory; putting grave items into empty slots.
    CURRENT
}
