package io.github.foundationgames.animatica.mixin;

import io.github.foundationgames.animatica.Animatica;
import net.minecraft.client.Options;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VideoSettingsScreen.class)
public abstract class VideoOptionsScreenMixin {
    // In 1.21.11 the video settings are built from three private static option groups
    // (display, quality, preferences), so the button is appended to the quality group
    @Inject(method = "qualityOptions", at = @At("RETURN"), cancellable = true)
    private static void animatica$addTextureAnimationOptionButton(Options options, CallbackInfoReturnable<OptionInstance<?>[]> cir) {
        var old = cir.getReturnValue();
        var extended = new OptionInstance<?>[old.length + 1];
        System.arraycopy(old, 0, extended, 0, old.length);
        extended[extended.length - 1] = Animatica.CONFIG.getAnimatedTexturesOption();
        cir.setReturnValue(extended);
    }
}
