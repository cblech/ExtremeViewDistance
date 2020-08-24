package net.fabricmc.example.renderer;

import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;

public interface IGameRendererExposed {
    public boolean getRenderHand();

    public Camera getCamera();

    public void renderHandRelay(MatrixStack matrix, Camera camera, float tickDelta);
}