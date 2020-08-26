package net.fabricmc.example.mixin;

import net.fabricmc.example.renderer.IGameRendererExposed;
import net.fabricmc.example.renderer.RendererFarWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.font.ImageGraphicAttribute;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements IGameRendererExposed {

    private RendererFarWorld rendererFarWorld;

    @Shadow @Final private Camera camera;

    @Shadow private boolean renderHand;

    @Shadow protected abstract void renderHand(MatrixStack matrices, Camera camera, float tickDelta);

    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private ResourceManager resourceContainer;
    private boolean cancle = true;

    @Override
    public MinecraftClient getMinecraftClient() {
        return client;
    }

    @Override
    public ResourceManager getResourceContainer() {
        return resourceContainer;
    }

    @Override
    public void renderHandRelay(MatrixStack matrix, Camera camera, float tickDelta) {
        renderHand(matrix,camera,tickDelta);
    }

    @Override
    public boolean getRenderHand() {
        return renderHand;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    private boolean oPressed=false;
    private boolean pPressed=false;

    @Inject(at = @At("HEAD"), method = "renderWorld",cancellable = true)
    void renderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci)
    {

        if(InputUtil.isKeyPressed(getMinecraftClient().getWindow().getHandle(), GLFW.GLFW_KEY_O)){
            if(!oPressed){
                cancle = !cancle;
                oPressed=true;
            }
        }else{
            oPressed=false;
        }

        if(InputUtil.isKeyPressed(getMinecraftClient().getWindow().getHandle(), GLFW.GLFW_KEY_P)){
            if(!pPressed){
                rendererFarWorld = null;
                pPressed=true;
            }
        }else{
            pPressed=false;
        }


        if(cancle){
            ci.cancel();

            try{
                rendererFarWorld.render(tickDelta,limitTime,matrix);
            }
            catch (NullPointerException e){
                e.printStackTrace();
                rendererFarWorld = new RendererFarWorld((GameRenderer)(IGameRendererExposed)this);
            }
        }

    }

}
