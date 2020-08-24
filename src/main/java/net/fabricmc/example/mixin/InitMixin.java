package net.fabricmc.example.mixin;

import net.fabricmc.example.RenderFarWorldMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class InitMixin {

    @Inject(at = @At("TAIL"), method = "render")
    void init(boolean tick, CallbackInfo ci)
    {
    }
}
