package net.fabricmc.example.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.GlProgram;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.GlShader;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;

public class MyGlShader {
    int glRefference;
    Type type;

    public MyGlShader(int glRefference, Type type) {
        this.glRefference = glRefference;
        this.type = type;
    }

    public void attachTo(MyGlProgram glProgram) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.attachShader(glProgram.getGlRefference(), glRefference);
    }

    public static MyGlShader createFromSource(Type type, String sourceCode) throws IOException {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (sourceCode == null||sourceCode.equals("")) {
            throw new InvalidPropertiesFormatException("No Source code provided. Failed to create shader");
        } else {
            int i = GlStateManager.createShader(type.glType);
            GlStateManager.shaderSource(i, sourceCode);
            GlStateManager.compileShader(i);

            if (GlStateManager.getShader(i, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                String string2 = StringUtils.trim(GlStateManager.getShaderInfoLog(i, 32768));
                throw new IOException("Couldn't compile " + type.name + " program: " + string2);
            } else {
                return new MyGlShader(i, type);
            }
        }
    }

    enum Type {
        VERTEX("Vertex Shader", GL20.GL_VERTEX_SHADER),
        FRAGMENT("Fragment Shader", GL20.GL_FRAGMENT_SHADER);

        String name;
        int glType;

        Type(String name, int glType) {
            this.name = name;
            this.glType = glType;
        }
    }
}
