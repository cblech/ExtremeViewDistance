package net.fabricmc.example.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
// Mixins HAVE to be written in java due to constraints in the mixin system.
public abstract class ExampleMixin implements SynchronousResourceReloadListener, AutoCloseable{

    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("TAIL"), method = "render")
    private void renderer(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera,
                          GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
        //System.out.println("This line is printed by an example mod mixin!");

        //try{
        //    RenderFarWorldMod.rfw.render(camera);
        //}
        //catch (NullPointerException e){
        //    RenderFarWorldMod.initRfw();
        //}
    }



/*


    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;
    @Shadow private ClientWorld world;
    @Shadow @Nullable private Frustum capturedFrustum;

    @Shadow @Nullable private ShaderEffect entityOutlineShader;
    @Shadow @Final private Set<BlockEntity> noCullingBlockEntities;
    @Shadow @Final private ObjectList<WorldRenderer.ChunkInfo> visibleChunks;

    @Shadow protected abstract void checkEmpty(MatrixStack matrices);

    @Shadow @Final private BufferBuilderStorage bufferBuilders;
    @Shadow @Nullable private Framebuffer entityOutlinesFramebuffer;

    @Shadow protected abstract boolean canDrawEntityOutlines();

    @Shadow @Final private FpsSmoother chunkUpdateSmoother;
    @Shadow @Nullable private Framebuffer entityFramebuffer;
    @Shadow @Nullable private Framebuffer weatherFramebuffer;
    @Shadow private int regularEntityCount;
    @Shadow private int blockEntityCount;

    @Shadow protected abstract void renderLayer(RenderLayer renderLayer, MatrixStack matrixStack, double d, double e, double f);

    @Shadow protected abstract void updateChunks(long limitTime);

    @Shadow private boolean shouldCaptureFrustum;
    @Shadow @Final private Vector3d capturedFrustumPosition;
    @Shadow private int frame;

    @Shadow protected abstract void setupTerrain(Camera camera, Frustum frustum, boolean hasForcedFrustum, int frame, boolean spectator);

    @Shadow protected abstract void captureFrustum(Matrix4f modelMatrix, Matrix4f matrix4f, double x, double y, double z, Frustum frustum);

    @Shadow public abstract void renderSky(MatrixStack matrices, float tickDelta);

    @Shadow @Nullable private Framebuffer translucentFramebuffer;
    @Shadow @Nullable private Framebuffer particlesFramebuffer;
    @Shadow @Nullable private ShaderEffect transparencyShader;

    @Shadow protected abstract void drawBlockOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState);

    @Shadow @Final private Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions;

    @Shadow @Nullable private Framebuffer cloudsFramebuffer;

    @Shadow public abstract void renderClouds(MatrixStack matrices, float tickDelta, double cameraX, double cameraY, double cameraZ);

    @Shadow protected abstract void renderClouds(BufferBuilder builder, double x, double y, double z, Vec3d color);

    @Shadow protected abstract void renderChunkDebugInfo(Camera camera);

    @Shadow protected abstract void renderWeather(LightmapTextureManager manager, float f, double d, double e, double g);

    @Shadow protected abstract void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers);

    @Shadow protected abstract void renderWorldBorder(Camera camera);


    */
/**
     * @author cblech
     */
/*

    @Overwrite
    public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f){
        BlockEntityRenderDispatcher.INSTANCE.configure(this.world, this.client.getTextureManager(), this.client.textRenderer, camera, this.client.crosshairTarget);
        this.entityRenderDispatcher.configure(this.world, camera, this.client.targetedEntity);
        Profiler profiler = this.world.getProfiler();
        profiler.swap("light_updates");
        this.client.world.getChunkManager().getLightingProvider().doLightUpdates(Integer.MAX_VALUE, true, true);
        Vec3d vec3d = camera.getPos();
        double d = vec3d.getX();
        double e = vec3d.getY();
        double f = vec3d.getZ();
        Matrix4f matrix4f2 = matrices.peek().getModel();
        profiler.swap("culling");
        boolean bl = this.capturedFrustum != null;
        Frustum frustum2;
        if (bl) {
            frustum2 = this.capturedFrustum;
            frustum2.setPosition(this.capturedFrustumPosition.x, this.capturedFrustumPosition.y, this.capturedFrustumPosition.z);
        } else {
            frustum2 = new Frustum(matrix4f2, matrix4f);
            frustum2.setPosition(d, e, f);
        }

        this.client.getProfiler().swap("captureFrustum");
        if (this.shouldCaptureFrustum) {
            this.captureFrustum(matrix4f2, matrix4f, vec3d.x, vec3d.y, vec3d.z, bl ? new Frustum(matrix4f2, matrix4f) : frustum2);
            this.shouldCaptureFrustum = false;
        }

        profiler.swap("clear");
        BackgroundRenderer.render(camera, tickDelta, this.client.world, this.client.options.viewDistance, gameRenderer.getSkyDarkness(tickDelta));
        RenderSystem.clear(16640, MinecraftClient.IS_SYSTEM_MAC);
        float g = gameRenderer.getViewDistance();
        boolean bl2 = this.client.world.getSkyProperties().useThickFog(MathHelper.floor(d), MathHelper.floor(e)) || this.client.inGameHud.getBossBarHud().shouldThickenFog();
        if (this.client.options.viewDistance >= 4) {
            BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_SKY, g, bl2);
            profiler.swap("sky");
            this.renderSky(matrices, tickDelta);
        }

        profiler.swap("fog");
        BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_TERRAIN, Math.max(g - 16.0F, 32.0F), bl2);
        profiler.swap("terrain_setup");
        this.setupTerrain(camera, frustum2, bl, this.frame++, this.client.player.isSpectator());
        profiler.swap("updatechunks");
        int i = true;
        int j = this.client.options.maxFps;
        long l = 33333333L;
        long n;
        if ((double)j == Option.FRAMERATE_LIMIT.getMax()) {
            n = 0L;
        } else {
            n = (long)(1000000000 / j);
        }

        long o = Util.getMeasuringTimeNano() - limitTime;
        long p = this.chunkUpdateSmoother.getTargetUsedTime(o);
        long q = p * 3L / 2L;
        long r = MathHelper.clamp(q, n, 33333333L);
        this.updateChunks(limitTime + r);
        profiler.swap("terrain");
        this.renderLayer(RenderLayer.getSolid(), matrices, d, e, f);
        this.renderLayer(RenderLayer.getCutoutMipped(), matrices, d, e, f);
        this.renderLayer(RenderLayer.getCutout(), matrices, d, e, f);
        if (this.world.getSkyProperties().isDarkened()) {
            DiffuseLighting.enableForLevel(matrices.peek().getModel());
        } else {
            DiffuseLighting.method_27869(matrices.peek().getModel());
        }

        profiler.swap("entities");
        profiler.push("prepare");
        this.regularEntityCount = 0;
        this.blockEntityCount = 0;
        profiler.swap("entities");
        if (this.entityFramebuffer != null) {
            this.entityFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
            this.entityFramebuffer.copyDepthFrom(this.client.getFramebuffer());
            this.client.getFramebuffer().beginWrite(false);
        }

        if (this.weatherFramebuffer != null) {
            this.weatherFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
        }

        if (this.canDrawEntityOutlines()) {
            this.entityOutlinesFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
            this.client.getFramebuffer().beginWrite(false);
        }

        boolean bl3 = false;
        VertexConsumerProvider.Immediate immediate = this.bufferBuilders.getEntityVertexConsumers();
        Iterator var39 = this.world.getEntities().iterator();

        while(true) {
            Entity entity;
            int w;
            do {
                do {
                    do {
                        if (!var39.hasNext()) {
                            this.checkEmpty(matrices);
                            immediate.draw(RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
                            immediate.draw(RenderLayer.getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
                            immediate.draw(RenderLayer.getEntityCutoutNoCull(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
                            immediate.draw(RenderLayer.getEntitySmoothCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
                            profiler.swap("blockentities");
                            ObjectListIterator var53 = this.visibleChunks.iterator();

                            while(true) {
                                List list;
                                do {
                                    if (!var53.hasNext()) {
                                        synchronized(this.noCullingBlockEntities) {
                                            Iterator var57 = this.noCullingBlockEntities.iterator();

                                            while(true) {
                                                if (!var57.hasNext()) {
                                                    break;
                                                }

                                                BlockEntity blockEntity2 = (BlockEntity)var57.next();
                                                BlockPos blockPos2 = blockEntity2.getPos();
                                                matrices.push();
                                                matrices.translate((double)blockPos2.getX() - d, (double)blockPos2.getY() - e, (double)blockPos2.getZ() - f);
                                                BlockEntityRenderDispatcher.INSTANCE.render(blockEntity2, tickDelta, matrices, immediate);
                                                matrices.pop();
                                            }
                                        }

                                        this.checkEmpty(matrices);
                                        immediate.draw(RenderLayer.getSolid());
                                        immediate.draw(TexturedRenderLayers.getEntitySolid());
                                        immediate.draw(TexturedRenderLayers.getEntityCutout());
                                        immediate.draw(TexturedRenderLayers.getBeds());
                                        immediate.draw(TexturedRenderLayers.getShulkerBoxes());
                                        immediate.draw(TexturedRenderLayers.getSign());
                                        immediate.draw(TexturedRenderLayers.getChest());
                                        this.bufferBuilders.getOutlineVertexConsumers().draw();
                                        if (bl3) {
                                            this.entityOutlineShader.render(tickDelta);
                                            this.client.getFramebuffer().beginWrite(false);
                                        }

                                        profiler.swap("destroyProgress");
                                        ObjectIterator var54 = this.blockBreakingProgressions.long2ObjectEntrySet().iterator();

                                        while(var54.hasNext()) {
                                            Long2ObjectMap.Entry<SortedSet<BlockBreakingInfo>> entry2 = (Long2ObjectMap.Entry)var54.next();
                                            BlockPos blockPos3 = BlockPos.fromLong(entry2.getLongKey());
                                            double h = (double)blockPos3.getX() - d;
                                            double x = (double)blockPos3.getY() - e;
                                            double y = (double)blockPos3.getZ() - f;
                                            if (h * h + x * x + y * y <= 1024.0D) {
                                                SortedSet<BlockBreakingInfo> sortedSet2 = (SortedSet)entry2.getValue();
                                                if (sortedSet2 != null && !sortedSet2.isEmpty()) {
                                                    int z = ((BlockBreakingInfo)sortedSet2.last()).getStage();
                                                    matrices.push();
                                                    matrices.translate((double)blockPos3.getX() - d, (double)blockPos3.getY() - e, (double)blockPos3.getZ() - f);
                                                    MatrixStack.Entry entry3 = matrices.peek();
                                                    VertexConsumer vertexConsumer2 = new TransformingVertexConsumer(this.bufferBuilders.getEffectVertexConsumers().getBuffer((RenderLayer) ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(z)), entry3.getModel(), entry3.getNormal());
                                                    this.client.getBlockRenderManager().renderDamage(this.world.getBlockState(blockPos3), blockPos3, this.world, matrices, vertexConsumer2);
                                                    matrices.pop();
                                                }
                                            }
                                        }

                                        this.checkEmpty(matrices);
                                        profiler.pop();
                                        HitResult hitResult = this.client.crosshairTarget;
                                        if (renderBlockOutline && hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
                                            profiler.swap("outline");
                                            BlockPos blockPos4 = ((BlockHitResult)hitResult).getBlockPos();
                                            BlockState blockState = this.world.getBlockState(blockPos4);
                                            if (!blockState.isAir() && this.world.getWorldBorder().contains(blockPos4)) {
                                                VertexConsumer vertexConsumer3 = immediate.getBuffer(RenderLayer.getLines());
                                                this.drawBlockOutline(matrices, vertexConsumer3, camera.getFocusedEntity(), d, e, f, blockPos4, blockState);
                                            }
                                        }

                                        RenderSystem.pushMatrix();
                                        RenderSystem.multMatrix(matrices.peek().getModel());
                                        this.client.debugRenderer.render(matrices, immediate, d, e, f);
                                        RenderSystem.popMatrix();
                                        immediate.draw(TexturedRenderLayers.getEntityTranslucentCull());
                                        immediate.draw(TexturedRenderLayers.getBannerPatterns());
                                        immediate.draw(TexturedRenderLayers.getShieldPatterns());
                                        immediate.draw(RenderLayer.getArmorGlint());
                                        immediate.draw(RenderLayer.getArmorEntityGlint());
                                        immediate.draw(RenderLayer.getGlint());
                                        immediate.draw(RenderLayer.getEntityGlint());
                                        immediate.draw(RenderLayer.getWaterMask());
                                        this.bufferBuilders.getEffectVertexConsumers().draw();
                                        immediate.draw(RenderLayer.getLines());
                                        immediate.draw();
                                        if (this.transparencyShader != null) {
                                            this.translucentFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
                                            this.translucentFramebuffer.copyDepthFrom(this.client.getFramebuffer());
                                            profiler.swap("translucent");
                                            this.renderLayer(RenderLayer.getTranslucent(), matrices, d, e, f);
                                            profiler.swap("string");
                                            this.renderLayer(RenderLayer.getTripwire(), matrices, d, e, f);
                                            this.particlesFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
                                            this.particlesFramebuffer.copyDepthFrom(this.client.getFramebuffer());
                                            RenderPhase.PARTICLES_TARGET.startDrawing();
                                            profiler.swap("particles");
                                            this.client.particleManager.renderParticles(matrices, immediate, lightmapTextureManager, camera, tickDelta);
                                            RenderPhase.PARTICLES_TARGET.endDrawing();
                                        } else {
                                            profiler.swap("translucent");
                                            this.renderLayer(RenderLayer.getTranslucent(), matrices, d, e, f);
                                            profiler.swap("string");
                                            this.renderLayer(RenderLayer.getTripwire(), matrices, d, e, f);
                                            profiler.swap("particles");
                                            this.client.particleManager.renderParticles(matrices, immediate, lightmapTextureManager, camera, tickDelta);
                                        }

                                        RenderSystem.pushMatrix();
                                        RenderSystem.multMatrix(matrices.peek().getModel());
                                        if (this.client.options.getCloudRenderMode() != CloudRenderMode.OFF) {
                                            if (this.transparencyShader != null) {
                                                this.cloudsFramebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
                                                RenderPhase.CLOUDS_TARGET.startDrawing();
                                                profiler.swap("clouds");
                                                this.renderClouds(matrices, tickDelta, d, e, f);
                                                RenderPhase.CLOUDS_TARGET.endDrawing();
                                            } else {
                                                profiler.swap("clouds");
                                                this.renderClouds(matrices, tickDelta, d, e, f);
                                            }
                                        }

                                        if (this.transparencyShader != null) {
                                            RenderPhase.WEATHER_TARGET.startDrawing();
                                            profiler.swap("weather");
                                            this.renderWeather(lightmapTextureManager, tickDelta, d, e, f);
                                            this.renderWorldBorder(camera);
                                            RenderPhase.WEATHER_TARGET.endDrawing();
                                            this.transparencyShader.render(tickDelta);
                                            this.client.getFramebuffer().beginWrite(false);
                                        } else {
                                            RenderSystem.depthMask(false);
                                            profiler.swap("weather");
                                            this.renderWeather(lightmapTextureManager, tickDelta, d, e, f);
                                            this.renderWorldBorder(camera);
                                            RenderSystem.depthMask(true);
                                        }

                                        this.renderChunkDebugInfo(camera);
                                        RenderSystem.shadeModel(7424);
                                        RenderSystem.depthMask(true);
                                        RenderSystem.disableBlend();
                                        RenderSystem.popMatrix();
                                        BackgroundRenderer.method_23792();
                                        return;
                                    }

                                    WorldRenderer.ChunkInfo chunkInfo = (WorldRenderer.ChunkInfo)var53.next();
                                    list = chunkInfo.chunk.getData().getBlockEntities();
                                } while(list.isEmpty());

                                Iterator var61 = list.iterator();

                                while(var61.hasNext()) {
                                    BlockEntity blockEntity = (BlockEntity)var61.next();
                                    BlockPos blockPos = blockEntity.getPos();
                                    VertexConsumerProvider vertexConsumerProvider3 = immediate;
                                    matrices.push();
                                    matrices.translate((double)blockPos.getX() - d, (double)blockPos.getY() - e, (double)blockPos.getZ() - f);
                                    SortedSet<BlockBreakingInfo> sortedSet = (SortedSet)this.blockBreakingProgressions.get(blockPos.asLong());
                                    if (sortedSet != null && !sortedSet.isEmpty()) {
                                        w = ((BlockBreakingInfo)sortedSet.last()).getStage();
                                        if (w >= 0) {
                                            MatrixStack.Entry entry = matrices.peek();
                                            VertexConsumer vertexConsumer = new TransformingVertexConsumer(this.bufferBuilders.getEffectVertexConsumers().getBuffer((RenderLayer)ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(w)), entry.getModel(), entry.getNormal());
                                            vertexConsumerProvider3 = (renderLayer) -> {
                                                VertexConsumer vertexConsumer2 = immediate.getBuffer(renderLayer);
                                                return renderLayer.hasCrumbling() ? VertexConsumers.dual(vertexConsumer, vertexConsumer2) : vertexConsumer2;
                                            };
                                        }
                                    }

                                    BlockEntityRenderDispatcher.INSTANCE.render(blockEntity, tickDelta, matrices, (VertexConsumerProvider)vertexConsumerProvider3);
                                    matrices.pop();
                                }
                            }
                        }

                        entity = (Entity)var39.next();
                    } while(!this.entityRenderDispatcher.shouldRender(entity, frustum2, d, e, f) && !entity.hasPassengerDeep(this.client.player));
                } while(entity == camera.getFocusedEntity() && !camera.isThirdPerson() && (!(camera.getFocusedEntity() instanceof LivingEntity) || !((LivingEntity)camera.getFocusedEntity()).isSleeping()));
            } while(entity instanceof ClientPlayerEntity && camera.getFocusedEntity() != entity);

            ++this.regularEntityCount;
            if (entity.age == 0) {
                entity.lastRenderX = entity.getX();
                entity.lastRenderY = entity.getY();
                entity.lastRenderZ = entity.getZ();
            }

            Object vertexConsumerProvider2;
            if (this.canDrawEntityOutlines() && this.client.method_27022(entity)) {
                bl3 = true;
                OutlineVertexConsumerProvider outlineVertexConsumerProvider = this.bufferBuilders.getOutlineVertexConsumers();
                vertexConsumerProvider2 = outlineVertexConsumerProvider;
                int k = entity.getTeamColorValue();

                int t = k >> 16 & 255;
                int u = k >> 8 & 255;
                w = k & 255;
                outlineVertexConsumerProvider.setColor(t, u, w, 255);
            } else {
                vertexConsumerProvider2 = immediate;
            }

            this.renderEntity(entity, d, e, f, tickDelta, matrices, (VertexConsumerProvider)vertexConsumerProvider2);
        }
    }
*/

}
