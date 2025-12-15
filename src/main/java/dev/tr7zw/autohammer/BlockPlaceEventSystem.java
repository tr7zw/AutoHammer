package dev.tr7zw.autohammer;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.*;
import com.hypixel.hytale.component.system.*;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.server.core.asset.type.item.config.*;
import com.hypixel.hytale.server.core.asset.type.item.config.BlockGroup;
import com.hypixel.hytale.server.core.entity.entities.*;
import com.hypixel.hytale.server.core.event.events.ecs.*;
import com.hypixel.hytale.server.core.universe.*;
import com.hypixel.hytale.server.core.universe.world.storage.*;

import javax.annotation.*;

public class BlockPlaceEventSystem extends EntityEventSystem<EntityStore, PlaceBlockEvent> {

    private final PlayerSettings playerSettings;

    public BlockPlaceEventSystem(PlayerSettings playerSettings) {
        super(PlaceBlockEvent.class);
        this.playerSettings = playerSettings;
    }

    @Override
    public void handle(final int index, @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull final Store<EntityStore> store, @Nonnull final CommandBuffer<EntityStore> commandBuffer, @Nonnull final PlaceBlockEvent event) {
        var ref = archetypeChunk.getReferenceTo(index);
        var player = store.getComponent(ref, Player.getComponentType());
        var group = BlockGroup.findItemGroup(event.getItemInHand().getItem());
        if (group != null) {
            var newItem = playerSettings.getRandomBlock(player.getUuid(), group);
            if (newItem == null) {
                return;
            }
            var target = event.getTargetBlock();
            player.getWorld().setBlock(target.x, target.y, target.z, newItem);
            if (player.getGameMode() != GameMode.Creative) {
                player.getInventory().getHotbar().setItemStackForSlot(player.getInventory().getActiveHotbarSlot(), event.getItemInHand().withQuantity(event.getItemInHand().getQuantity() - 1));
            }
            event.setCancelled(true);
        }
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }
}
