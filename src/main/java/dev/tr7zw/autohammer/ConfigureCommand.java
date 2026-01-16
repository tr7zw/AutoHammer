package dev.tr7zw.autohammer;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.server.core.*;
import com.hypixel.hytale.server.core.asset.type.item.config.BlockGroup;
import com.hypixel.hytale.server.core.command.system.*;
import com.hypixel.hytale.server.core.entity.entities.*;
import com.hypixel.hytale.server.core.universe.*;
import com.hypixel.hytale.server.core.universe.world.*;
import com.hypixel.hytale.server.core.universe.world.storage.*;
import com.hypixel.hytale.protocol.packets.interface_.*;

import javax.annotation.*;
import java.util.concurrent.*;

import static com.hypixel.hytale.server.core.command.commands.player.inventory.InventorySeeCommand.*;

/**
 * This is an example command that will simply print the name of the plugin in chat when used.
 */
public class ConfigureCommand extends AbstractCommand {

    private final PlayerSettings playerSettings;

    public ConfigureCommand(PlayerSettings playerSettings) {
        super("autohammer", "Configures the AutoHammer plugin.");
        this.playerSettings = playerSettings;
    }

    @Nullable
    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        CommandSender sender = context.sender();
        if (sender instanceof Player player) {
            Ref<EntityStore> ref = player.getReference();
            if (ref != null && ref.isValid()) {
                Store<EntityStore> store = ref.getStore();
                World world = store.getExternalData().getWorld();
                var itemInHand = player.getInventory().getItemInHand();
                if (itemInHand == null || !itemInHand.isValid()) {
                    context.sendMessage(Message.translation("autohammer.errors.noitem"));
                    return CompletableFuture.completedFuture(null);
                }
                var group = BlockGroup.findItemGroup(itemInHand.getItem());
                if (group == null) {
                    context.sendMessage(Message.translation("autohammer.errors.nogroup"));
                    return CompletableFuture.completedFuture(null);
                }
                return CompletableFuture.runAsync(() -> {
                    PlayerRef playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());
                    if (playerRefComponent != null) {
                        player.getPageManager().openCustomPage(ref, store, new AutoHammerConfigUI(playerRefComponent, CustomPageLifetime.CanDismiss, group, playerSettings));
                    }
                }, world);
            } else {
                context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
                return CompletableFuture.completedFuture(null);
            }
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }
}