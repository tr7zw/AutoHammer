package dev.tr7zw.autohammer;

import com.hypixel.hytale.assetstore.*;
import com.hypixel.hytale.codec.*;
import com.hypixel.hytale.codec.builder.*;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.server.core.*;
import com.hypixel.hytale.server.core.asset.type.item.config.BlockGroup;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.*;
import com.hypixel.hytale.server.core.entity.entities.player.pages.*;
import com.hypixel.hytale.server.core.ui.*;
import com.hypixel.hytale.server.core.ui.builder.*;
import com.hypixel.hytale.server.core.universe.*;
import com.hypixel.hytale.server.core.universe.world.storage.*;
import com.hypixel.hytale.protocol.packets.interface_.*;
import org.jetbrains.annotations.*;

import javax.annotation.*;

public class AutoHammerConfigUI extends InteractiveCustomUIPage<AutoHammerConfigUI.ConfigGuiData> {

    private final BlockGroup group;
    private final PlayerSettings settings;

    public AutoHammerConfigUI(@Nonnull PlayerRef playerRef, @Nonnull CustomPageLifetime lifetime, BlockGroup group, PlayerSettings settings) {
        super(playerRef, lifetime, ConfigGuiData.CODEC);
        this.group = group;
        this.settings = settings;
    }

    public static class ConfigGuiData {
        public static final BuilderCodec<ConfigGuiData> CODEC = BuilderCodec.<ConfigGuiData>builder(ConfigGuiData.class, ConfigGuiData::new)
                .addField(new KeyedCodec<>("Item", Codec.STRING), (searchGuiData, s) -> searchGuiData.item = s, searchGuiData -> searchGuiData.item)
                .addField(new KeyedCodec("Type", Codec.STRING), (entry, s) -> entry.type = s, (entry) -> entry.type)
                .build();

        private String type;
        private String item;

    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        /**
         * The append method loads the .ui file from the path specified and loads all the elements inside it.
         */
        uiCommandBuilder.append("Pages/AutoHammerConfig.ui");
        /**
         * This page has a search bar, and you use `addEventBinding` to configure what to do when interacted with an element.
         *
         * In this case, when the user types in the search bar `#SearchInput`, the value of the search bar is sent to the server.
         *
         * What its sent it's defined in the EventData object, the first parameter is the key that will be used to send the value to the server (in the codec), the second parameter is the selector of the ui element.
         */
        //uiEventBuilder.addEventBinding(CustomUIEventBindingType.SlotMouseEntered, "#ItemGrid");
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Reset", (new EventData()).append("Type", "Reset"), false);

        //this.buildList(ref, uiCommandBuilder, uiEventBuilder, store);
        /*BsonArray bsonArray = new BsonArray();
        for(int i = 0; i < group.size(); i++) {
            var item = group.get(i);
            BsonDocument encoded = ItemStack.CODEC.encode(new ItemStack(item), new ExtraInfo());
            encoded.remove("OverrideDroppedItemAnimation");
            bsonArray.add(encoded);
        }
        try {
            Method value = UICommandBuilder.class.getDeclaredMethod("setBsonValue", String.class, BsonValue.class);
            value.setAccessible(true);
            value.invoke(uiCommandBuilder,"#ItemGrid.ItemStacks", bsonArray);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }*/
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        this.buildButtons(playerComponent, uiCommandBuilder, uiEventBuilder);
    }

    @Override
    public void handleDataEvent(@NotNull Ref<EntityStore> ref, @NotNull Store<EntityStore> store, @NotNull AutoHammerConfigUI.ConfigGuiData data) {
        super.handleDataEvent(ref, store, data);
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        if(data.type != null) {
            switch (data.type) {
                case "Reset" -> {
                    settings.resetGroup(playerComponent.getUuid(), group);
                }
            }
        } else if (data.item != null) {
            settings.toggleBlock(playerComponent.getUuid(), group, data.item);
        }
        UICommandBuilder commandBuilder = new UICommandBuilder();
        UIEventBuilder eventBuilder = new UIEventBuilder();
        this.buildButtons(playerComponent, commandBuilder, eventBuilder);
        this.sendUpdate(commandBuilder, eventBuilder, false);
    }

    private void buildButtons(@Nonnull Player playerComponent, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
        commandBuilder.clear("#SubcommandCards");
        commandBuilder.set("#SubcommandSection.Visible", true);
        int rowIndex = 0;
        int cardsInCurrentRow = 0;

        for (int i = 0; i < group.size(); i++) {
            String item = group.get(i);
            var itemRef = AssetRegistry.getAssetStore(Item.class).getAssetMap().getAsset(item);

            if (cardsInCurrentRow == 0) {
                commandBuilder.appendInline("#SubcommandCards", "Group { LayoutMode: Left; Anchor: (Bottom: 0); }");
            }

            commandBuilder.append("#SubcommandCards[" + rowIndex + "]", "Pages/AHItem.ui");

            commandBuilder.set("#SubcommandCards[" + rowIndex + "][" + cardsInCurrentRow + "] #ItemIcon.ItemId", item);
            commandBuilder.set("#SubcommandCards[" + rowIndex + "][" + cardsInCurrentRow + "] #ItemName.TextSpans", Message.translation(itemRef.getTranslationKey()));
            commandBuilder.setObject("#SubcommandCards[" + rowIndex + "][" + cardsInCurrentRow + "].Background", settings.hasEnabled(playerComponent.getUuid(), group, item) ? new PatchStyle().setColor(Value.of("#48ff0077")) : new PatchStyle().setColor(Value.of("#97000077")));
            //commandBuilder.set("#SubcommandCards[" + rowIndex + "][" + cardsInCurrentRow + "] #SubcommandUsage.TextSpans", this.getSimplifiedUsage(item, playerComponent));
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SubcommandCards[" + rowIndex + "][" + cardsInCurrentRow + "]", EventData.of("Item", item));
            ++cardsInCurrentRow;
            if (cardsInCurrentRow >= 3) {
                cardsInCurrentRow = 0;
                ++rowIndex;
            }
        }


        //commandBuilder.set("#BackButton.Visible", !this.subcommandBreadcrumb.isEmpty());
    }


}