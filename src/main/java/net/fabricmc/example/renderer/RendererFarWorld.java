package net.fabricmc.example.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.example.renderer.uniform.UniformFloat;
import net.fabricmc.example.renderer.uniform.UniformMatrix4;
import net.fabricmc.example.renderer.uniform.UniformVec3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;

import java.io.IOException;


public class RendererFarWorld {

    //private int shaderProgram;
    private int VBO;
    private int VAO;

    private MyGlProgram program;

    private GameRenderer gameRenderer;
    private IGameRendererExposed gameRendererExposed;


    private final static int FLOAT_SIZE = 4;

    UniformVec3 uEyeWorldPos = new UniformVec3("uEyeWorldPos");
    UniformMatrix4 uModelMat = new UniformMatrix4("uModelMat");
    UniformMatrix4 uViewProjectionMat = new UniformMatrix4("uViewProjectionMat");


    public RendererFarWorld(GameRenderer gameRenderer) {

        this.gameRenderer = gameRenderer;
        this.gameRendererExposed = (IGameRendererExposed) gameRenderer;

        //this.framebuffer = new Framebuffer(MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight(), true, false);

        System.out.println("######## Setting up FarWorld Renderer ########");
        System.out.println("OpenGL: " + GL11.glGetString(GL11.GL_VERSION));

        //caps = GL.createCapabilities(false);


        int currentVAO = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);


        try {
            program = MyGlProgram.factory()
                    .vertexShaderFromResource(new Identifier("extremeviewdistance:shader/world_vertex.glsl"),gameRendererExposed.getResourceContainer())
                    .tesselationControlShaderFromResource(new Identifier("extremeviewdistance:shader/world_tesselation_control.glsl"),gameRendererExposed.getResourceContainer())
                    .tesselationEvaluationShaderFromResource(new Identifier("extremeviewdistance:shader/world_tesselation_evaluation.glsl"),gameRendererExposed.getResourceContainer())
                    .fragmentShaderFromResource(new Identifier("extremeviewdistance:shader/world_fragment.glsl"),gameRendererExposed.getResourceContainer())
                    .uniform(uModelMat)
                    .uniform(uViewProjectionMat)
                    .uniform(uEyeWorldPos)
                    .create();

        } catch (IOException e) {
            e.printStackTrace();
        }

        float size = 1f;

        float[] verts = {
                -size, -size, size, 1.0f, .0F, .0F, 1.0F,
                size, -size, size, 1.0F, 1.0f, .0F, 1.0F,
                0.0f, size, size, .0F, .0F, 1.0f, 1.0F
        };

        VAO = ARBVertexArrayObject.glGenVertexArrays();
        VBO = GL15.glGenBuffers();

        ARBVertexArrayObject.glBindVertexArray(VAO);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verts, GL15.GL_STATIC_DRAW);


        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, (3 + 4) * FLOAT_SIZE, 0);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, (3 + 4) * FLOAT_SIZE, 3 * FLOAT_SIZE);
        GL20.glEnableVertexAttribArray(1);

        ARBVertexArrayObject.glBindVertexArray(currentVAO);

    }
    public void renderA(float tickDelta, long limitTime, MatrixStack matrix)  {

        int currentVAO = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

        gameRenderer.getCamera().update(gameRendererExposed.getMinecraftClient().world, gameRendererExposed.getMinecraftClient().cameraEntity, false, false, tickDelta);

        GL11.glClearColor(.04f, .02f, .1f, 1f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glDisable(GL11.GL_CULL_FACE);

        program.use();

        Matrix4f modelMat = new Matrix4f();
        modelMat.loadIdentity();
        modelMat.multiply(Matrix4f.translate(0f, 0, 0));


        //viewProjectionMatrix.multiply(Matrix4f.projectionMatrix(gameRendererInstance.getMinecraftClient().getFramebuffer().viewportWidth,gameRendererInstance.getMinecraftClient().getFramebuffer().viewportHeight,0.1f,1000.f));

        program.pushUniform(uModelMat, modelMat);
        program.pushUniform(uViewProjectionMat,getViewProjectionMatrix(tickDelta));
        program.pushUniform(uEyeWorldPos, new Vector3f(gameRenderer.getCamera().getPos()));


        //RenderSystem.depthMask(false);
        //RenderSystem.disableDepthTest();

        ARBVertexArrayObject.glBindVertexArray(VAO);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);


        float size = 10f;

        float[] verts = {
                -size, 0, -size, 1.0f, 1.0f, .0f, 1.0F,
                 size, 0, -size, .0F, 1.0f, 1.0f, 1.0F,
                -size, 0,  size, 1.0f, .0f, 1.0f, 1.0F,
                 size, 0, -size, .0F, 1.0f, 1.0f, 1.0F,
                -size, 0,  size, 1.0f, .0f, 1.0f, 1.0F,
                 size, 0,  size, 1.0f, 1.0f, 1.0f, 1.0F
        };

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verts, GL15.GL_STATIC_DRAW);

        GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_LINE );
        GL11.glDrawArrays(ARBTessellationShader.GL_PATCHES, 0, 6);
        GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_FILL );

        ARBVertexArrayObject.glBindVertexArray(currentVAO);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        //RenderSystem.enableCull();

        //RenderSystem.depthMask(true);
        //RenderSystem.enableDepthTest();

        //GlStateManager.bindFramebuffer(FramebufferInfo.FRAME_BUFFER, 0);

        //framebuffer.endWrite();

        //framebuffer.draw(MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight());


        program.unUse();

        RenderSystem.disableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();

        RenderSystem.pushMatrix();
        //RenderSystem.rotatef((System.currentTimeMillis()/10)%360,0,1,1);
        //RenderSystem.translatef(-1,1,0);

        if (gameRendererExposed.getRenderHand()) {
            RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
            gameRendererExposed.renderHandRelay(matrix, gameRenderer.getCamera(), tickDelta);
        }

        RenderSystem.popMatrix();
    }

    private Matrix4f getViewProjectionMatrix(float tickDelta){
        Matrix4f viewProjectionMatrix = new Matrix4f();
        viewProjectionMatrix.loadIdentity();
        viewProjectionMatrix.multiply(gameRenderer.getBasicProjectionMatrix(gameRenderer.getCamera(), tickDelta, false));
        //new Vector3f(0,1,0).getDegreesQuaternion( gameRendererInstance.getCamera().getPitch());

        viewProjectionMatrix.multiply(new Vector3f(1,0,0).getDegreesQuaternion( gameRenderer.getCamera().getPitch()));
        viewProjectionMatrix.multiply(new Vector3f(0,1,0).getDegreesQuaternion( gameRenderer.getCamera().getYaw()));
        viewProjectionMatrix.multiply(Matrix4f.translate(
                (float)gameRenderer.getCamera().getPos().getX(),
                -(float)gameRenderer.getCamera().getPos().getY(),
                (float)gameRenderer.getCamera().getPos().getZ()));


        return viewProjectionMatrix;
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

    public void render(float tickDelta, long limitTime, MatrixStack matrix) {
        renderA(tickDelta, limitTime, matrix);
    }
}
