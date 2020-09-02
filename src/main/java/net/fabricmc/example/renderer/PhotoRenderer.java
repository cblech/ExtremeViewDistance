package net.fabricmc.example.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class PhotoRenderer {

    private int glFBO = -1;
    private int glFbColorTexture = -1;
    private int glFbDepthTexture = -1;

    public PhotoRenderer() {

        glFBO = GL30.glGenFramebuffers();
        int currentFBO = glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
        glBindFramebuffer(GL_FRAMEBUFFER, glFBO);

        glFbColorTexture = GL11.glGenTextures();
        glBindTexture(GL_TEXTURE_2D, glFbColorTexture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 512, 512, 0, GL_RGB, GL_UNSIGNED_BYTE, (double[]) null);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, glFbColorTexture, 0);

        glFbDepthTexture = GL11.glGenTextures();
        glBindTexture(GL_TEXTURE_2D, glFbDepthTexture);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, 512, 512, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, (double[]) null);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, glFbDepthTexture, 0);

        glClearColor(0.5f, 0, 0, 0);

        glBindFramebuffer(GL_FRAMEBUFFER, currentFBO);

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

        glBindFramebuffer(GL_FRAMEBUFFER, currentFBO);
        drawDebug();
    }

    //DEBUG

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
            "void main()\n" +
            "{\n" +
            "    FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n" +
            "} ";

    private void initDebug() {
        debugVAO = glGenVertexArrays();
        debugVBO = glGenBuffers();

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
                    .create();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawDebug() {
        debugProgram.use();
        glBindVertexArray(debugVAO);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        glBindVertexArray(0);
        debugProgram.unUse();
    }
}
