package net.fabricmc.example.mixin;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.example.renderer.IWorldRendererExposed;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin implements IWorldRendererExposed {
    @Shadow protected abstract void setupTerrain(Camera camera, Frustum frustum, boolean hasForcedFrustum, int frame, boolean spectator);

    @Shadow protected abstract void renderLayer(RenderLayer renderLayer, MatrixStack matrixStack, double d, double e, double f);
//@Shadow @Final private ObjectList<WorldRenderer.ChunkInfo> visibleChunks;



    @Override
    public void setupTerrainRelay(Camera camera, Frustum frustum, boolean hasForcedFrustum, int frame, boolean spectator) {
        setupTerrain(camera,frustum,hasForcedFrustum,frame,spectator);
    }

    /*@Override
    public void addVisibleChunk(ChunkBuilder.BuiltChunk builtChunk) {
        visibleChunks.add(new WorldRenderer.ChunkInfo(builtChunk,null,0));
    }
*/

    @Override
    public void renderLayerRelay(RenderLayer renderLayer, MatrixStack matrixStack, double d, double e, double f) {
        renderLayer(renderLayer,matrixStack,d,e,f);
    }
}
