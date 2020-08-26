package net.fabricmc.example.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.example.renderer.uniform.UniformFloat;
import net.fabricmc.example.renderer.uniform.UniformMatrix4;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import org.apache.commons.io.IOUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;


public class RendererFarWorld {

    //private int shaderProgram;
    private int VBO;
    private int VAO;

    private MyGlProgram program;

    private GameRenderer gameRenderer;
    private IGameRendererExposed gameRendererExposed;


    private final static int FLOAT_SIZE = 4;

    UniformFloat uPosXOffset = new UniformFloat("uPosXOffset");
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

        final String vertexShaderSource =
                "#version 330\n" +
                        "layout (location=0) in vec3 aPos;\n" +
                        "layout (location=1) in vec4 aColor;\n" +
                        "" +
                        "out vec4 vColor;" +
                        "" +
                        "uniform mat4 uModelMat;" +
                        "uniform mat4 uViewProjectionMat;" +
                        "\n" +
                        "void main()\n" +
                        "{\n" +
                        "    vColor = aColor;" +
                        "    gl_Position =uViewProjectionMat* uModelMat*vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" +
                        "}";

        final String fragmentShaderSource =
                "#version 330\n" +
                        "in vec4 vColor;" +
                        "" +
                        "out vec4 FragColor;\n" +
                        "" +
                        "\n" +
                        "void main()\n" +
                        "{\n" +
                        "    FragColor = vColor;\n" +
                        "} ";


        try {
            program = MyGlProgram.factory()
                    .vertexShaderFromSource(vertexShaderSource)
                    .fragmentShaderFromSource(fragmentShaderSource)
                    .uniform(uModelMat)
                    .uniform(uViewProjectionMat)
                    .create();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //gameRendererExposed.getResourceContainer().getResource(new Identifier("extremeviewdistance:shader/test_shader.glsl"));

            System.out.println(gameRendererExposed.getResourceContainer().getResource(new Identifier("extremeviewdistance:test_shader.glsl")));
        } catch (Exception e) {
            e.printStackTrace();
        }

/*

        int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexShader,vertexShaderSource);
        GL20.glCompileShader(vertexShader);

        int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentShader,fragmentShaderSource);
        GL20.glCompileShader(fragmentShader);

        shaderProgram = GL20.glCreateProgram();


        GL20.glAttachShader(shaderProgram,vertexShader);
        GL20.glAttachShader(shaderProgram,fragmentShader);
        GL20.glLinkProgram(shaderProgram);
*/


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

/*
        System.out.println("########################");
        System.out.println("OpenGL11: "+caps.OpenGL11);
        System.out.println("OpenGL12: "+caps.OpenGL12);
        System.out.println("OpenGL13: "+caps.OpenGL13);
        System.out.println("OpenGL14: "+caps.OpenGL14);
        System.out.println("OpenGL15: "+caps.OpenGL15);
        System.out.println("OpenGL20: "+caps.OpenGL20);
        System.out.println("OpenGL21: "+caps.OpenGL21);
        System.out.println("OpenGL30: "+caps.OpenGL30);
        System.out.println("OpenGL31: "+caps.OpenGL31);
        System.out.println("OpenGL32: "+caps.OpenGL32);
        System.out.println("OpenGL33: "+caps.OpenGL33);
        System.out.println("OpenGL40: "+caps.OpenGL40);
        System.out.println("OpenGL41: "+caps.OpenGL41);
        System.out.println("OpenGL42: "+caps.OpenGL42);
        System.out.println("OpenGL43: "+caps.OpenGL43);
        System.out.println("OpenGL44: "+caps.OpenGL44);
        System.out.println("OpenGL45: "+caps.OpenGL45);
        System.out.println("OpenGL46: "+caps.OpenGL46);
        System.out.println("########################");*/
    }

    public void renderA(float tickDelta, long limitTime, MatrixStack matrix)  {

        if(InputUtil.isKeyPressed(gameRendererExposed.getMinecraftClient().getWindow().getHandle(),GLFW.GLFW_KEY_O)){
            int i = 0;
        }

        int currentVAO = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

        gameRenderer.getCamera().update(gameRendererExposed.getMinecraftClient().world, gameRendererExposed.getMinecraftClient().cameraEntity, false, false, tickDelta);


        try {
            Resource r=gameRendererExposed.getResourceContainer().getResource(Identifier.tryParse("extremeviewdistance:shader/test_shader.glsl"));

            String theString = IOUtils.toString(r.getInputStream(), StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }

        GL11.glClearColor(.04f, .02f, .1f, 1f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);


        GL11.glDisable(GL11.GL_CULL_FACE);

        program.use();

        Matrix4f modelMat = new Matrix4f();
        modelMat.loadIdentity();
        modelMat.multiply(Matrix4f.translate(0.3f, 0, -1));


        //viewProjectionMatrix.multiply(Matrix4f.projectionMatrix(gameRendererInstance.getMinecraftClient().getFramebuffer().viewportWidth,gameRendererInstance.getMinecraftClient().getFramebuffer().viewportHeight,0.1f,1000.f));

        program.pushUniform(uModelMat, modelMat);
        program.pushUniform(uViewProjectionMat,getViewProjectionMatrix(tickDelta));


        //RenderSystem.depthMask(false);
        //RenderSystem.disableDepthTest();

        ARBVertexArrayObject.glBindVertexArray(VAO);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);


        float size = 0.5f;

        float[] verts = {
                -1f, -1f, -1f, 1.0f,  .0F,  .0F, 1.0F,
                 1f, -1f, -1f, 1.0F, 1.0f,  .0F, 1.0F,
                -1f,  1f, -1f,  .0F,  .0F, 1.0f, 1.0F
        };

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verts, GL15.GL_STATIC_DRAW);


        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);


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
        viewProjectionMatrix.multiply(((GameRenderer)gameRenderer).getBasicProjectionMatrix(gameRenderer.getCamera(), tickDelta, false));
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
