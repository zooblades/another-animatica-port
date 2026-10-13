package io.github.foundationgames.animatica;

import io.github.foundationgames.animatica.animation.AnimationLoader;
import io.github.foundationgames.animatica.config.AnimaticaConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Animatica implements ClientModInitializer {
    public static final Logger LOG = LogManager.getLogger("Animatica");
    public static final String NAMESPACE = "animatica";

    public static final AnimaticaConfig CONFIG = new AnimaticaConfig();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> AnimationLoader.INSTANCE.tickTextures());

        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(AnimationLoader.ID, AnimationLoader.INSTANCE);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(NAMESPACE, path);
    }
}
