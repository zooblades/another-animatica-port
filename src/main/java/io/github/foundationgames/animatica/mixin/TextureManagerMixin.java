package io.github.foundationgames.animatica.mixin;

import io.github.foundationgames.animatica.Animatica;
import io.github.foundationgames.animatica.animation.AnimationLoader;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// Since the 1.21.5+ render pipeline rewrite there is no RenderSystem#_setShaderTexture(int, Identifier) to hook,
// so every texture lookup is redirected to the animated version here instead
@Mixin(TextureManager.class)
public class TextureManagerMixin {
    @ModifyVariable(method = "getTexture(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/texture/AbstractTexture;", at = @At("HEAD"), argsOnly = true)
    private Identifier animatica$replaceWithAnimatedTexture(Identifier old) {
        if (Animatica.CONFIG.animatedTextures) {
            var anim = AnimationLoader.INSTANCE.getAnimationId(old);
            if (anim != null) {
                return anim;
            }
        }
        return old;
    }
}
