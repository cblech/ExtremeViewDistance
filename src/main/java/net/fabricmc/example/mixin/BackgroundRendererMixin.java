package net.fabricmc.example.mixin;

import net.fabricmc.example.renderer.RendererFarWorld;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @Inject(at = @At("HEAD"), method = "applyFog",cancellable = true)
    private static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci) {
        if(fogType== BackgroundRenderer.FogType.FOG_TERRAIN)
        {
            RendererFarWorld.getRfwInstance().renderDispacher();
        }
        ci.cancel();
    }
}
