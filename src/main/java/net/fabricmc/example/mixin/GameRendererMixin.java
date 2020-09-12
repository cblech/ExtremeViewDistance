package net.fabricmc.example.mixin;

import net.fabricmc.example.renderer.IGameRendererExposed;
import net.fabricmc.example.renderer.RendererFarWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.BlankGlyph;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
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

    @Shadow protected abstract double getFov(Camera camera, float tickDelta, boolean changingFov);

    @Shadow private long lastWindowFocusedTime;
    @Shadow private long lastWorldIconUpdate;

    @Shadow protected abstract void updateWorldIcon();

    @Shadow protected abstract boolean shouldRenderBlockOutline();

    @Shadow @Final private LightmapTextureManager lightmapTextureManager;
    @Shadow private float viewDistance;

    @Shadow protected abstract void bobViewWhenHurt(MatrixStack matrixStack, float f);

    @Shadow protected abstract void bobView(MatrixStack matrixStack, float f);

    @Shadow private int ticks;
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

    @Override
    public double getFovRelay(Camera camera, float tickDelta, boolean changingFov) {
        return getFov(camera,tickDelta,changingFov);
    }

    @Override
    public long getLastWindowFocusedTime() {
        return lastWindowFocusedTime;
    }

    @Override
    public void setLastWindowFocusedTime(long lastWindowFocusedTime) {
        this.lastWindowFocusedTime = lastWindowFocusedTime;
    }

    @Override
    public long getLastWorldIconUpdate() {
        return lastWorldIconUpdate;
    }

    @Override
    public void setLastWorldIconUpdate(long lastWorldIconUpdate) {
        this.lastWorldIconUpdate = lastWorldIconUpdate;
    }

    @Override
    public void updateWorldIconRelay() {
        updateWorldIcon();
    }

    @Override
    public boolean shouldRenderBlockOutlineRelay() {
        return shouldRenderBlockOutline();
    }

    @Override
    public LightmapTextureManager getLightmapTextureManager() {
        return lightmapTextureManager;
    }

    @Override
    public void setViewDistance(float v) {
        viewDistance=v;
    }

    @Override
    public void bobViewWhenHurtRelay(MatrixStack matrixStack, float tickDelta) {
        bobViewWhenHurt(matrixStack,tickDelta);
    }

    @Override
    public void bobViewRelay(MatrixStack matrixStack, float tickDelta) {
        bobView(matrixStack,tickDelta);
    }

    @Override
    public int getTicks() {
        return ticks;
    }

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
                //e.printStackTrace();
                rendererFarWorld = new RendererFarWorld((GameRenderer)(IGameRendererExposed)this);
            }
        }

    }

}
