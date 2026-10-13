package io.github.foundationgames.animatica.mixin;

import io.github.foundationgames.animatica.Animatica;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(VideoSettingsScreen.class)
public abstract class VideoOptionsScreenMixin {
    @ModifyArg(
            method = "addOptions",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/OptionsList;addSmall([Lnet/minecraft/client/OptionInstance;)V"
            ),
            index = 0
    )
    private OptionInstance<?>[] animatica$addTextureAnimationOptionButton(OptionInstance<?>[] old) {
        var options = new OptionInstance<?>[old.length + 1];
        System.arraycopy(old, 0, options, 0, old.length);
        options[options.length - 1] = Animatica.CONFIG.getAnimatedTexturesOption();
        return options;
    }
}
