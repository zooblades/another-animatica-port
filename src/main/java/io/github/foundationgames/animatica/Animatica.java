package io.github.foundationgames.animatica;

import io.github.foundationgames.animatica.animation.AnimationLoader;
import io.github.foundationgames.animatica.config.AnimaticaConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = Animatica.NAMESPACE, dist = Dist.CLIENT)
public class Animatica {
    public static final Logger LOG = LogManager.getLogger("Animatica");
    public static final String NAMESPACE = "animatica";

    public static final AnimaticaConfig CONFIG = new AnimaticaConfig();

    public Animatica(IEventBus modBus, ModContainer container) {
        modBus.addListener(Animatica::registerReloadListeners);
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Pre event) -> AnimationLoader.INSTANCE.tickTextures());
    }

    private static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(AnimationLoader.INSTANCE);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, path);
    }
}
