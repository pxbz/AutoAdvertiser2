package me.pxbz.autoadvertiser2;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoAdvertiser2 implements ModInitializer {

    private static KeyBinding keyBinding;
    private static KeyBinding screenHotKey;
    private ScheduledExecutorService executor = null;
    private static final HashMap<String, Long> delays = new HashMap<>();
    private static final HashMap<String, String> messages = new HashMap<>();
    private int adCount = 0;

    public static final Logger LOGGER = LoggerFactory.getLogger("autoadvertiser");

    @Override
    public void onInitialize() {
        final long normalDelay = 2 + 16 * 60L;
        final long vipDelay = 2 + 14 * 60L;
        final long proDelay = 2 + 10 * 60L;
        final long legendDelay = 2 + 6 * 60L;
        final long patronDelay = 2 + 2 * 60L;

        delays.put("account1", vipDelay);
        delays.put("account2", vipDelay);
        delays.put("account3", proDelay);

        LOGGER.info("Hello Fabric world");

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.examplemod.sendmessage", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_Y, // The keycode of the key
                "category.examplemod.sendmessage" // The translation key of the keybinding's category.
        ));

        screenHotKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.examplemod.openscreen", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_X, // The keycode of the key
                "category.examplemod.openscreen" // The translation key of the keybinding's category.
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (keyBinding.wasPressed())
                toggleAd(client);
            if (screenHotKey.wasPressed())
                client.setScreen(new CustomScreen());
        });
    }

    private void toggleAd(MinecraftClient client) {
        if (client.player == null) return;

        if (executor == null) {
            client.player.sendMessage(Text.of("Auto Advertiser enabled"), true);

            String name = client.player.getName().getString().toLowerCase();
            final long delay = delays.get(name);

            executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                try {
                    client.player.networkHandler.sendCommand(CustomScreen.getEnteredText());
                    adCount++;
                } catch (Exception ignored) {}
            }, 0, delay, TimeUnit.SECONDS);
        }
        else {
            client.player.sendMessage(Text.of("Auto Advertiser disabled. (" + adCount + " ads)"), true);
            adCount = 0;
            try {
                executor.shutdown();
                executor = null;

            } catch (Exception e) {
                client.player.sendMessage(Text.of("Error occurred with shutdown"), true);
            }
        }
    }
}
