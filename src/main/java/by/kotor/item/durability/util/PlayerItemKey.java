package by.kotor.item.durability.util;

import java.util.UUID;

public class PlayerItemKey {
    private final UUID playerId;
    private final String itemKey;

    public PlayerItemKey(UUID playerId, String itemKey) {
        this.playerId = playerId;
        this.itemKey = itemKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerItemKey)) return false;
        PlayerItemKey other = (PlayerItemKey) o;
        return playerId.equals(other.playerId) && itemKey.equals(other.itemKey);
    }

    @Override
    public int hashCode() {
        return 31 * playerId.hashCode() + itemKey.hashCode();
    }
}
