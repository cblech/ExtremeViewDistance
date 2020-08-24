package net.fabricmc.example.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.GlProgram;
import net.minecraft.client.gl.GlProgramManager;

import java.io.IOException;

public class MyGlProgram  {

    private int glRefference;
    private MyGlShader vertexShader;
    private MyGlShader fragmentShader;

    private MyGlProgram() {
        try {
            glRefference = GlProgramManager.createProgram();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void link() throws IOException {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        fragmentShader.attachTo(this);
        vertexShader.attachTo(this);

        GlStateManager.linkProgram(glRefference);

        int i = GlStateManager.getProgram(glRefference, 35714);
        if (i == 0) {
            System.err.println("Error encountered when linking program ");
            System.err.println(GlStateManager.getProgramInfoLog(glRefference, 32768));
        }

    }

    static MyGlProgram createProgramFromSource(String vertexShader, String fragmentShader) throws IOException {
        MyGlProgram mgp = new MyGlProgram();
        mgp.vertexShader = MyGlShader.createFromSource(MyGlShader.Type.VERTEX, vertexShader);
        mgp.fragmentShader = MyGlShader.createFromSource(MyGlShader.Type.FRAGMENT, fragmentShader);

        mgp.link();
        return mgp;
    }

    public void use()
    {
        GlStateManager.useProgram(glRefference);
    }
    public void unUse()
    {
        GlStateManager.useProgram(0);
    }


    public int getGlRefference() {
        return glRefference;
    }
}