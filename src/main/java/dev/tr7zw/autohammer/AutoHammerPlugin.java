package dev.tr7zw.autohammer;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import java.io.*;

public class AutoHammerPlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final PlayerSettings playerSettings = new PlayerSettings();

    public static void main(String... args) throws IOException {
        String userHome = System.getProperty("user.home");
        com.hypixel.hytale.Main.main(
                new String[]{
                        "--allow-op",
                        "--disable-sentry",
                        "--assets=" + userHome + "\\AppData\\Roaming\\Hytale\\install\\release\\package\\game\\latest\\Assets",
                        "--packs=.\\src\\main"
                }
        );
    }

    public AutoHammerPlugin(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up plugin " + this.getName());
        this.getCommandRegistry().registerCommand(new ConfigureCommand(playerSettings));
        getEntityStoreRegistry().registerSystem(new BlockPlaceEventSystem(playerSettings));
    }
}
