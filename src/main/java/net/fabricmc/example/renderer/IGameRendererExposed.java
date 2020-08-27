package net.fabricmc.example.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;

public interface IGameRendererExposed {
    public boolean getRenderHand();

    public Camera getCamera();

    public MinecraftClient getMinecraftClient();

    public ResourceManager getResourceContainer();

    public double getFovRelay(Camera camera, float tickDelta, boolean changingFov);

    public void renderHandRelay(MatrixStack matrix, Camera camera, float tickDelta);

    long getLastWindowFocusedTime();

    void setLastWindowFocusedTime(long measuringTimeMs);

    long getLastWorldIconUpdate();

    void setLastWorldIconUpdate(long measuringTimeMs);

    void updateWorldIconRelay();

    boolean shouldRenderBlockOutlineRelay();

    LightmapTextureManager getLightmapTextureManager();

    void setViewDistance(float v);

    void bobViewWhenHurtRelay(MatrixStack matrixStack, float tickDelta);

    void bobViewRelay(MatrixStack matrixStack, float tickDelta);

    int getTicks();
}
