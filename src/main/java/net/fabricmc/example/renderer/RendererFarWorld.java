package net.fabricmc.example.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.example.renderer.util.converter.McJoml;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
//import net.minecraft.util.math.Matrix4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.*;

import java.util.ArrayList;
import java.util.List;


public class RendererFarWorld {

    private List<FarWorldTile> farWorldTiles = new ArrayList<>();

    private GameRenderer gameRenderer;
    private IGameRendererExposed gameRendererExposed;

    private MinecraftClient client;

    private static RendererFarWorld rfwInstance;

    private final static int FLOAT_SIZE = 4;

    public static RendererFarWorld getRfwInstance() {
        return rfwInstance;
    }

    public RendererFarWorld(GameRenderer gameRenderer) {
        rfwInstance = this;
        this.gameRenderer = gameRenderer;
        this.gameRendererExposed = (IGameRendererExposed) gameRenderer;
        this.client = gameRendererExposed.getMinecraftClient();

        //this.framebuffer = new Framebuffer(MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight(), true, false);

        System.out.println("######## Setting up FarWorld Renderer ########");
        System.out.println("OpenGL: " + GL11.glGetString(GL11.GL_VERSION));

        FarWorldTile.removeProgram();

        int x = 16;
        int s = 64;

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < x; j++) {
                FarWorldTile newFarWorldTile = new FarWorldTile(gameRendererExposed, client.world);
                newFarWorldTile.setPosition(new Vector3f(i * s, 0, j * s));
                newFarWorldTile.setSize(s);
                newFarWorldTile.setTexturesFromWorld();
                farWorldTiles.add(newFarWorldTile);
            }
        }

    }

    public void renderA(float tickDelta, long limitTime, MatrixStack matrix) {

        int currentVAO = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

        //gameRenderer.getCamera().update(gameRendererExposed.getMinecraftClient().world, gameRendererExposed.getMinecraftClient().cameraEntity, false, false, tickDelta);

        FarWorldTile.useProgram();
        FarWorldTile.setFrameValues(McJoml.toJomlVector3f(gameRenderer.getCamera().getPos()), getViewProjectionMatrix(tickDelta));
        for (FarWorldTile f : farWorldTiles) {
            f.draw();
        }

        FarWorldTile.unUseProgram();


        ARBVertexArrayObject.glBindVertexArray(currentVAO);

    }


    public void /*GameRenderer.*/renderWorld(float tickDelta, long limitTime, MatrixStack matrix) {
        gameRendererExposed.getLightmapTextureManager().update(tickDelta);
        if (this.client.getCameraEntity() == null) {
            this.client.setCameraEntity(this.client.player);
        }

        gameRenderer.updateTargetedEntity(tickDelta);
        this.client.getProfiler().push("center");
        boolean bl = gameRendererExposed.shouldRenderBlockOutlineRelay();
        this.client.getProfiler().swap("camera");
        Camera camera = gameRenderer.getCamera();
        gameRendererExposed.setViewDistance((float) (this.client.options.viewDistance * 16));
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.peek().getModel().multiply(gameRenderer.getBasicProjectionMatrix(camera, tickDelta, true));
        gameRendererExposed.bobViewWhenHurtRelay(matrixStack, tickDelta);
        if (this.client.options.bobView) {
            gameRendererExposed.bobViewRelay(matrixStack, tickDelta);
        }

        float f = MathHelper.lerp(tickDelta, this.client.player.lastNauseaStrength, this.client.player.nextNauseaStrength);
        if (f > 0.0F) {
            int i = 20;
            if (this.client.player.hasStatusEffect(StatusEffects.NAUSEA)) {
                i = 7;
            }

            float g = 5.0F / (f * f + 5.0F) - f * 0.04F;
            g *= g;
            net.minecraft.client.util.math.Vector3f vector3f = new net.minecraft.client.util.math.Vector3f(0.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F);
            matrixStack.multiply(vector3f.getDegreesQuaternion(((float) gameRendererExposed.getTicks() + tickDelta) * (float) i));
            matrixStack.scale(1.0F / g, 1.0F, 1.0F);
            float h = -((float) gameRendererExposed.getTicks() + tickDelta) * (float) i;
            matrixStack.multiply(vector3f.getDegreesQuaternion(h));
        }

        net.minecraft.util.math.Matrix4f matrix4f = matrixStack.peek().getModel();
        gameRenderer.loadProjectionMatrix(matrix4f);
        camera.update(
                this.client.world,
                (Entity)
                        (this.client.getCameraEntity() ==
                                null ? this.client.player :
                                this.client.getCameraEntity()
                        ),

                false,//this.clientxyz.options.perspective > 0,
                false,//this.clientxyz.options.perspective == 2,
                tickDelta);

        matrix.multiply(net.minecraft.client.util.math.Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
        matrix.multiply(net.minecraft.client.util.math.Vector3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));
        this.client.worldRenderer.render(matrix, tickDelta, limitTime, bl, camera, gameRenderer, gameRendererExposed.getLightmapTextureManager(), matrix4f);
        this.client.getProfiler().swap("hand");
        if (gameRendererExposed.getRenderHand()) {
            RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
            gameRendererExposed.renderHandRelay(matrix, camera, tickDelta);
        }

        this.client.getProfiler().pop();
    }


/*

    private void WorldRenderer_renderWorld(float tickDelta, long startTime, long limitTime, MatrixStack matrix,boolean tick)
    {
        if (!this.client.isWindowFocused() && this.client.options.pauseOnLostFocus && (!this.client.options.touchscreen || !this.client.mouse.wasRightButtonClicked())) {
            if (Util.getMeasuringTimeMs() - gameRendererExposed.getLastWindowFocusedTime() > 500L) {
                this.client.openPauseMenu(false);
            }
        } else {
            gameRendererExposed.setLastWindowFocusedTime(Util.getMeasuringTimeMs());
        }

        if (!this.client.skipGameRender) {
            int i = (int)(this.client.mouse.getX() * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth());
            int j = (int)(this.client.mouse.getY() * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight());
            RenderSystem.viewport(0, 0, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
            if (tick && this.client.world != null) {
                this.client.getProfiler().push("level");
                gameRenderer.renderWorld(tickDelta, startTime, new MatrixStack());
                if (this.client.isIntegratedServerRunning() && gameRendererExposed.getLastWorldIconUpdate() < Util.getMeasuringTimeMs() - 1000L) {
                    gameRendererExposed.setLastWorldIconUpdate(Util.getMeasuringTimeMs());
                    if (!this.client.getServer().hasIconFile()) {
                        gameRendererExposed.updateWorldIconRelay();
                    }
                }

                this.client.worldRenderer.drawEntityOutlinesFramebuffer();
                if (this.shader != null && this.shadersEnabled) {
                    RenderSystem.disableBlend();
                    RenderSystem.disableDepthTest();
                    RenderSystem.disableAlphaTest();
                    RenderSystem.enableTexture();
                    RenderSystem.matrixMode(5890);
                    RenderSystem.pushMatrix();
                    RenderSystem.loadIdentity();
                    this.shader.render(tickDelta);
                    RenderSystem.popMatrix();
                }

                this.client.getFramebuffer().beginWrite(true);
            }

            Window window = this.client.getWindow();
            RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
            RenderSystem.matrixMode(5889);
            RenderSystem.loadIdentity();
            RenderSystem.ortho(0.0D, (double)window.getFramebufferWidth() / window.getScaleFactor(), (double)window.getFramebufferHeight() / window.getScaleFactor(), 0.0D, 1000.0D, 3000.0D);
            RenderSystem.matrixMode(5888);
            RenderSystem.loadIdentity();
            RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
            DiffuseLighting.enableGuiDepthLighting();
            MatrixStack matrixStack = new MatrixStack();
            if (tick && this.client.world != null) {
                this.client.getProfiler().swap("gui");
                if (!this.client.options.hudHidden || this.client.currentScreen != null) {
                    RenderSystem.defaultAlphaFunc();
                    this.renderFloatingItem(this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight(), tickDelta);
                    this.client.inGameHud.render(matrixStack, tickDelta);
                    RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
                }

                this.client.getProfiler().pop();
            }

            CrashReport crashReport2;
            CrashReportSection crashReportSection2;
            if (this.client.overlay != null) {
                try {
                    this.client.overlay.render(matrixStack, i, j, this.client.getLastFrameDuration());
                } catch (Throwable var13) {
                    crashReport2 = CrashReport.create(var13, "Rendering overlay");
                    crashReportSection2 = crashReport2.addElement("Overlay render details");
                    crashReportSection2.add("Overlay name", () -> {
                        return this.client.overlay.getClass().getCanonicalName();
                    });
                    throw new CrashException(crashReport2);
                }
            } else if (this.client.currentScreen != null) {
                try {
                    this.client.currentScreen.render(matrixStack, i, j, this.client.getLastFrameDuration());
                } catch (Throwable var12) {
                    crashReport2 = CrashReport.create(var12, "Rendering screen");
                    crashReportSection2 = crashReport2.addElement("Screen render details");
                    crashReportSection2.add("Screen name", () -> {
                        return this.client.currentScreen.getClass().getCanonicalName();
                    });
                    crashReportSection2.add("Mouse location", () -> {
                        return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.client.mouse.getX(), this.client.mouse.getY());
                    });
                    crashReportSection2.add("Screen size", () -> {
                        return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight(), this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), this.client.getWindow().getScaleFactor());
                    });
                    throw new CrashException(crashReport2);
                }
            }

        }
    }
*/

    private Matrix4f getViewProjectionMatrix(float tickDelta) {
        Camera camera = gameRenderer.getCamera();

        Vector3fc translation = McJoml.toJomlVector3f(camera.getPos()).negate();
        float aspectRat = (float) gameRendererExposed.getMinecraftClient().getWindow().getFramebufferWidth() / (float) gameRendererExposed.getMinecraftClient().getWindow().getFramebufferHeight();
        //System.out.println(aspectRat);

        return new Matrix4f().perspective(
                (float) Math.toRadians(gameRendererExposed.getFovRelay(camera, tickDelta, false) * 1.1),
                aspectRat,
                0.5f, 100000)
                .rotate((float) Math.toRadians(camera.getPitch()), 1, 0, 0)
                .rotate((float) Math.toRadians(camera.getYaw() + 180f), 0, 1, 0)
                .translate(translation)
                ;

    }

    public void renderB(Camera camera) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufBuil = tessellator.getBuffer();

        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();

        float size = .5f;

        float[] verts = {
                -size, -size, 0, 1.0f, .0F, .0F, 1.0F,
                size, -size, 0, 1.0F, 1.0f, .0F, 1.0F,
                0.0f, size, 0, .0F, .0F, 1.0f, 1.0F
        };

        bufBuil.begin(GL11.GL_TRIANGLES, VertexFormats.POSITION_COLOR);

        bufBuil.vertex(-size, -size, size).color(1.0f, .0F, .0F, 1.0F).next();
        bufBuil.vertex(size, -size, size).color(1.0F, 1.0f, .0F, 1.0F).next();
        bufBuil.vertex(0.0f, size, size).color(.0F, .0F, 1.0f, 1.0F).next();


        tessellator.draw();

        RenderSystem.translated(-0.5, 0.3, 0);


        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();


    }

    public void renderDispatcher() {
        renderA(tickDelta, limitTime, matrix);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

    }


    private float tickDelta;
    private long limitTime;
    private MatrixStack matrix;

    public void render(float tickDelta, long limitTime, MatrixStack matrix) {
        this.tickDelta = tickDelta;
        this.limitTime = limitTime;
        this.matrix = matrix;

        renderWorld(tickDelta, limitTime, matrix);
        //renderA(tickDelta, limitTime, matrix);
    }
}
