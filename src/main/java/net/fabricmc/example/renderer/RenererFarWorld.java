package net.fabricmc.example.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.*;

import java.io.IOException;


public class RenererFarWorld {

    //private int shaderProgram;
    private int VBO;
    private int VAO;

    private MyGlProgram program;

    private Framebuffer framebuffer;

    private GLCapabilities caps;


    private final static int FLOAT_SIZE = 4;


    public RenererFarWorld() {
/*        KHRDebug.glDebugMessageCallback(new GLDebugMessageCallbackI() {
            @Override
            public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
                Pointer p = new Pointer() {
                    @Override
                    public long address() {
                        return message;
                    }
                };

                p.getClass();
            }
        },0);*/

        this.framebuffer = new Framebuffer(MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight(), true, false);

        System.out.println("########################");
        System.out.println("OpenGL: "+GL11.glGetString(GL11.GL_VERSION));

        caps = GL.createCapabilities(false);


        int currentVAO = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

        final String vertexShaderSource =
                "#version 330\n" +
                        "layout (location=0) in vec3 aPos;\n" +
                        "layout (location=1) in vec4 aColor;\n" +
                        "" +
                        "out vec4 vColor;" +
                        "\n" +
                        "void main()\n" +
                        "{\n" +
                        "    vColor = aColor;" +
                        "    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" +
                        "}";

        final String fragmentShaderSource =
                "#version 330\n" +
                        "in vec4 vColor;" +
                        "" +
                        "out vec4 FragColor;\n" +
                        "\n" +
                        "void main()\n" +
                        "{\n" +
                        "    FragColor = vColor;\n" +
                        "} ";


        try {
            program = MyGlProgram.createProgramFromSource(vertexShaderSource,fragmentShaderSource);
        } catch (IOException e) {
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

        float[] verts={
                -size, -size, size, 1.0f,  .0F,  .0F, 1.0F,
                 size, -size, size, 1.0F, 1.0f,  .0F, 1.0F,
                 0.0f,  size, size,  .0F,  .0F, 1.0f, 1.0F
        };

        VAO = ARBVertexArrayObject.glGenVertexArrays();
        VBO = GL15.glGenBuffers();

        ARBVertexArrayObject.glBindVertexArray(VAO);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,VBO);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verts,GL15.GL_STATIC_DRAW);


        GL20.glVertexAttribPointer(0,3,GL11.GL_FLOAT,false,(3+4)*FLOAT_SIZE,0);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(1,4,GL11.GL_FLOAT,false,(3+4)*FLOAT_SIZE,3*FLOAT_SIZE);
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

    public void renderA(float tickDelta, long limitTime, MatrixStack matrix, IGameRendererExposed gameRendererInstance) {


        int currentVAO = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);


        GL11.glClearColor(.04f,.02f,.1f,1f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);


        GL11.glDisable(GL11.GL_CULL_FACE);

        program.use();
        //framebuffer.beginWrite(false);


        //RenderSystem.depthMask(false);
        //RenderSystem.disableDepthTest();

        ARBVertexArrayObject.glBindVertexArray(VAO);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,VBO);



        float size = 0.5f;

        float[] verts={
                -1f, -1f, -1f, 1.0f,  .0F,  .0F, 1.0F,
                1f, -1f, -1f, 1.0F, 1.0f,  .0F, 1.0F,
                -1f,  1f, -1f,  .0F,  .0F, 1.0f, 1.0F
        };

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verts,GL15.GL_STATIC_DRAW);


        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);



        ARBVertexArrayObject.glBindVertexArray(currentVAO);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,0);

        //RenderSystem.enableCull();

        //RenderSystem.depthMask(true);
        //RenderSystem.enableDepthTest();

        //GlStateManager.bindFramebuffer(FramebufferInfo.FRAME_BUFFER, 0);

        //framebuffer.endWrite();

        //framebuffer.draw(MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight());




        program.unUse();

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();

        RenderSystem.pushMatrix();
        RenderSystem.rotatef((System.currentTimeMillis()/10)%360,0,1,1);
        //RenderSystem.translatef(-1,1,0);

        if (gameRendererInstance.getRenderHand()) {
            RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
            gameRendererInstance.renderHandRelay(matrix, gameRendererInstance.getCamera(), tickDelta);
        }

        RenderSystem.popMatrix();
    }

    public void renderB(Camera camera) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufBuil = tessellator.getBuffer();

        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();

        float size = .5f;

        float[] verts={
                -size, -size, 0, 1.0f,  .0F,  .0F, 1.0F,
                size, -size, 0, 1.0F, 1.0f,  .0F, 1.0F,
                0.0f,  size, 0,  .0F,  .0F, 1.0f, 1.0F
        };

        bufBuil.begin(GL11.GL_TRIANGLES, VertexFormats.POSITION_COLOR);

        bufBuil.vertex(-size, -size, size).color(1.0f,  .0F,  .0F, 1.0F).next();
        bufBuil.vertex(size, -size, size).color(1.0F, 1.0f,  .0F, 1.0F).next();
        bufBuil.vertex(0.0f,  size, size).color(.0F,  .0F, 1.0f, 1.0F).next();


        tessellator.draw();

        RenderSystem.translated(-0.5,0.3,0);


        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();


    }

    public void render(float tickDelta, long limitTime, MatrixStack matrix, IGameRendererExposed gameRendererInstance) {
        renderA(tickDelta,limitTime,matrix,gameRendererInstance);
    }
}
