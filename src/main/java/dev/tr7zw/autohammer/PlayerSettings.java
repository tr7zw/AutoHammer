package dev.tr7zw.autohammer;

import com.hypixel.hytale.server.core.asset.type.item.config.*;
import com.hypixel.hytale.server.core.universe.*;

import java.util.*;

public class PlayerSettings {

    private final Map<UUID, PlayerHolder> playerSettings = new HashMap<>();

    private class PlayerHolder {
        Map<String, Set<String>> settings = new HashMap<>();
    }

    public String getRandomBlock(UUID playerId, BlockGroup group) {
        Set<String> filter = Collections.EMPTY_SET;
        if (playerSettings.containsKey(playerId)) {
            var holder = playerSettings.get(playerId);
            if (holder.settings.containsKey(group.getId())) {
                filter = holder.settings.get(group.getId());
            }
        }
        List<String> entries = new ArrayList<>();
        for (int i = 0; i < group.size(); i++) {
            var item = group.get(i);
            if (filter.contains(item)) {
                entries.add(item);
            }
        }
        if (entries.isEmpty()) {
            return null;
        }
        int id = (int) (entries.size() * Math.random());
        return entries.get(id);
    }

    public boolean hasEnabled(UUID playerId, BlockGroup group, String blockId) {
        if (playerSettings.containsKey(playerId)) {
            var holder = playerSettings.get(playerId);
            if (holder.settings.containsKey(group.getId())) {
                return holder.settings.get(group.getId()).contains(blockId);
            }
        }
        return false;
    }

    public void toggleBlock(UUID playerId, BlockGroup group, String blockId) {
        PlayerHolder holder;
        if (playerSettings.containsKey(playerId)) {
            holder = playerSettings.get(playerId);
        } else {
            holder = new PlayerHolder();
            playerSettings.put(playerId, holder);
        }
        Set<String> set;
        if (holder.settings.containsKey(group.getId())) {
            set = holder.settings.get(group.getId());
        } else {
            set = new HashSet<>();
            holder.settings.put(group.getId(), set);
        }
        if (set.contains(blockId)) {
            set.remove(blockId);
        } else {
            set.add(blockId);
        }
    }

    public void resetGroup(UUID playerId, BlockGroup group) {
        if (playerSettings.containsKey(playerId)) {
            var holder = playerSettings.get(playerId);
            if (holder.settings.containsKey(group.getId())) {
                holder.settings.remove(group.getId());
            }
        }
    }

}
