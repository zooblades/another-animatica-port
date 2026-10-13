package io.github.foundationgames.animatica.animation;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.foundationgames.animatica.Animatica;
import io.github.foundationgames.animatica.util.Flags;
import io.github.foundationgames.animatica.util.exception.PropertyParseException;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;

public final class AnimationLoader implements ResourceManagerReloadListener {
    public static final String[] ANIM_PATHS = {
            "animatica/anim",
            "mcpatcher/anim",
            "optifine/anim"
    };

    public static final Identifier ID = Animatica.id("animation_storage");

    public static final AnimationLoader INSTANCE = new AnimationLoader();

    private final Map<Identifier, Identifier> animationIds = new HashMap<>();
    private final Set<AnimatedTexture> animatedTextures = new HashSet<>();

    private AnimationLoader() {
    }

    private static void findAllMCPAnimations(ResourceManager manager, BiConsumer<Identifier, Resource> action) {
        for (var path : ANIM_PATHS) {
            manager.listResources(path, p -> p.getPath().endsWith(".properties")).forEach(action);
        }
    }

    public @Nullable Identifier getAnimationId(Identifier id) {
        return animationIds.get(id);
    }

    public void tickTextures() {
        if (!RenderSystem.isOnRenderThread()) {
            Minecraft.getInstance().execute(this::tickTextures);
        } else {
            for (var texture : animatedTextures) {
                texture.tick();
            }
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        this.animatedTextures.clear();
        this.animationIds.clear();

        if (!Animatica.CONFIG.animatedTextures) {
            return;
        }

        Flags.ALLOW_INVALID_ID_CHARS = true;

        var animations = new HashMap<Identifier, List<AnimationMeta>>();

        findAllMCPAnimations(manager, (id, resource) -> {
            try {
                try (var resourceInputStream = resource.open()) {
                    var ppt = new Properties();
                    ppt.load(resourceInputStream);

                    var anim = AnimationMeta.of(id, ppt);

                    var targetId = anim.target();
                    if (!animations.containsKey(targetId)) animations.put(targetId, new ArrayList<>());
                    animations.get(targetId).add(anim);
                }
            } catch (IOException | PropertyParseException e) {
                Animatica.LOG.error(e.getMessage());
            }
        });

        for (var targetId : animations.keySet()) {
            AnimatedTexture.tryCreate(manager, targetId, animations.get(targetId))
                    .ifPresent(tex -> {
                        var animId = Identifier.fromNamespaceAndPath(targetId.getNamespace(), targetId.getPath() + "-anim");
                        this.animationIds.put(targetId, animId);
                        this.animatedTextures.add(tex);
                        Minecraft.getInstance().getTextureManager().register(animId, tex);
                    });
        }

        Flags.ALLOW_INVALID_ID_CHARS = false;
    }
}
