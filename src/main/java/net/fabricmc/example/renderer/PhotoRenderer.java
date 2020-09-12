package net.fabricmc.example.renderer;

import net.fabricmc.example.renderer.uniform.UniformInt;
import net.fabricmc.example.renderer.util.converter.McJoml;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class PhotoRenderer {

    private int glFBO = -1;
    private int glFbColorTexture = -1;
    private int glFbDepthTexture = -1;
    private int glRenderStencilBuffer = -1;

    private RendererFarWorld rfw;

    private WorldRenderer worldRenderer;

    public PhotoRenderer(RendererFarWorld rendererFarWorld) {
        this.rfw = rendererFarWorld;
        this.worldRenderer = new WorldRenderer(rfw.client,rfw.client.getBufferBuilders());
        ClientWorld cwMain = rfw.client.world;
        this.worldRenderer.setWorld(cwMain);



        glFBO = glGenFramebuffers();
        int currentFBO = glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        int currentTexture2D = glGetInteger(GL_TEXTURE_BINDING_2D);
        glBindFramebuffer(GL_FRAMEBUFFER, glFBO);

        glFbColorTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, glFbColorTexture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 512, 512, 0, GL_RGB, GL_UNSIGNED_BYTE, (double[]) null);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, glFbColorTexture, 0);

        glFbDepthTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, glFbDepthTexture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, 512, 512, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, (double[]) null);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, glFbDepthTexture, 0);

        glRenderStencilBuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, glRenderStencilBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER,GL_STENCIL_INDEX8,512,512);
        glBindRenderbuffer(GL_RENDERBUFFER,0);

        glFramebufferRenderbuffer(GL_FRAMEBUFFER,GL_STENCIL_ATTACHMENT,GL_RENDERBUFFER, glRenderStencilBuffer);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            System.err.println( "ERROR::FRAMEBUFFER:: Framebuffer is not complete!");

        glClearColor(0.5f, 0, 0, 0);

        glBindFramebuffer(GL_FRAMEBUFFER, currentFBO);
        glBindTexture(GL_TEXTURE_2D,currentTexture2D);

        initDebug();
    }

    @Override
    protected void finalize() throws Throwable {
        //GL30.glDeleteFramebuffers(glFBO);
        //glDeleteTextures(glFbColorTexture);
        super.finalize();
    }

    public void draw() {
        //GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        //GL11.glEnable(GL11.GL_SCISSOR_TEST);
        //GL11.glScissor(100,100,500,500);

        //GL11.glDisable(GL11.GL_SCISSOR_TEST);

        int currentFBO = glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);

        glBindFramebuffer(GL_FRAMEBUFFER, glFBO);
        glClearColor(0.5f, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        int i = glCheckFramebufferStatus(GL_FRAMEBUFFER);
/*

        switch (i){
            case GL_FRAMEBUFFER_COMPLETE:
                int j = 0;
                break;
            case GL_FRAMEBUFFER_UNDEFINED:
                int j = 0;
                break;
            case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                int j = 0;
                break;
            case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                int j = 0;
                break;
            case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
                int j = 0;
                break;
            case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
                int j = 0;
                break;
            case GL_FRAMEBUFFER_UNSUPPORTED:
                int j = 0;
                break;
            case GL_FRAMEBUFFER_COMPLETE:
                int j = 0;
                break;
            case GL_FRAMEBUFFER_COMPLETE:
                int j = 0;
                break;
            case GL_FRAMEBUFFER_COMPLETE:
                int j = 0;
                break;
        }
*/

        //TODO DRAW STUFF
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Matrix4f matrix4f = new Matrix4f();
        Frustum frustum = new Frustum(matrix4f,matrix4f){
            @Override
            public boolean isVisible(Box box) {
                return true;
            }


        };
        matrix4f.loadIdentity();
/*
        org.joml.Matrix4f m = new org.joml.Matrix4f(
        0.70660746f, 0.0f, 0.0f, 0.0f,
        0.0f, 1.2571725f, 0.0f, 0.0f,
        0.0f, 0.0f, -1.0001302f, -0.10000651f,
        0.0f, 0.0f, -1.0f, 0.0f
);*/
        org.joml.Matrix4f m = new org.joml.Matrix4f();
        //m.ortho(16,16,16,16,1,255);
        //m.

        //rfw.renderWorld(0,0,new MatrixStack());
        //((IWorldRendererExposed)rfw.client.worldRenderer).setupTerrainRelay(camera,frustum,false,0,false);
        //((IWorldRendererExposed)rfw.client.worldRenderer).renderLayerRelay(RenderLayer.getSolid(),new MatrixStack(),0,0,0);
        //rfw.client.worldRenderer.render(new MatrixStack(), 0, 0, false, camera, rfw.gameRenderer, rfw.gameRendererExposed.getLightmapTextureManager(), matrix4f);

        MatrixStack ms = new MatrixStack();

        ms.multiply(net.minecraft.client.util.math.Vector3f.POSITIVE_X.getDegreesQuaternion(90.f));
        //ms.multiply(net.minecraft.client.util.math.Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
        //ms.multiply(net.minecraft.client.util.math.Vector3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));


        camera = new Camera();

        worldRenderer.render(ms,
                0.5f,
                365547044800L,
                false,
                camera,
                rfw.gameRenderer,
                rfw.gameRendererExposed.getLightmapTextureManager(),
                McJoml.toMc(m));


        glBindFramebuffer(GL_FRAMEBUFFER, currentFBO);
        //drawDebug();
    }

    //DEBUG  ###########################################################################

    private int debugVAO = -1, debugVBO = -1;

    private MyGlProgram debugProgram;

    private float[] debugVerts = {
            //      POS             UV
            -0.95f, -0.95f, 0, 0,
            -0.3f, -0.95f, 1, 0,
            -0.95f, -0.3f, 0, 1,
            -0.3f, -0.3f, 1, 1
    };

    private String debugVertexShaderSource = "#version 330\n" +
            "\n" +
            "layout (location=0) in vec2 aPos;\n" +
            "layout (location=1) in vec2 aTex;\n" +
            "\n" +
            "out vec2 fPos;\n" +
            "out vec2 fTex;\n" +
            "\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    fPos = aPos;\n" +
            "    fTex = aTex;\n" +
            "    gl_Position = vec4(aPos,0.f,1.f);\n" +
            "}\n";

    private String debugFragmentShaderSource = "#version 330 core\n" +
            "in vec2 fPos;\n" +
            "in vec2 fTex;\n" +
            "\n" +
            "out vec4 FragColor;\n" +
            "\n" +
            "uniform sampler2D uTexture;\n" +
            "void main()\n" +
            "{\n" +
            "    FragColor =texture(uTexture,fTex);// vec4(1.0f, 0.5f, 0.2f, 1.0f);\n" +
            "} ";

    private UniformInt uDebugTexture = new UniformInt("uTexture");

    private void initDebug() {
        debugVAO = glGenVertexArrays();
        debugVBO = glGenBuffers();

        int currentVAO = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        glBindVertexArray(debugVAO);
        glBindBuffer(GL_ARRAY_BUFFER, debugVBO);
        glBufferData(GL_ARRAY_BUFFER, debugVerts, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * 4, 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * 4, 2 * 4);

        try {
            debugProgram = MyGlProgram.factory()
                    .vertexShaderFromSource(debugVertexShaderSource)
                    .fragmentShaderFromSource(debugFragmentShaderSource)
                    .uniform(uDebugTexture)
                    .create();
        } catch (IOException e) {
            e.printStackTrace();
        }
        glBindVertexArray(currentVAO);
    }

    void drawDebug() {
        debugProgram.use();
        glBindVertexArray(debugVAO);
        int currentTexture = glGetInteger(GL_TEXTURE_BINDING_2D);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D,glFbColorTexture);
        debugProgram.pushUniform(uDebugTexture,0);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        glBindTexture(GL_TEXTURE_2D,currentTexture);
        glBindVertexArray(0);
        debugProgram.unUse();
    }
}
