package net.fabricmc.example.renderer;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.util.math.MatrixStack;

public interface IWorldRendererExposed {

    //ObjectList<WorldRenderer.ChunkInfo> getVisibleChunks();
    void setupTerrainRelay(Camera camera, Frustum frustum, boolean hasForcedFrustum, int frame, boolean spectator);

    //void addVisibleChunk(ChunkBuilder.BuiltChunk builtChunk);

    void renderLayerRelay(RenderLayer renderLayer, MatrixStack matrixStack, double d, double e, double f);

}
