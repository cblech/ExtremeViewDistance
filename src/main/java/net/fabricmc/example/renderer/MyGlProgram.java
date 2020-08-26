package net.fabricmc.example.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import javafx.util.Pair;
import net.fabricmc.example.renderer.uniform.Uniform;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.util.*;

public class MyGlProgram  {

    private int glReference;
    private MyGlShader vertexShader=null;
    private MyGlShader fragmentShader=null;
    private final Map<Uniform, Integer> uniforms = new HashMap<>();

    private static MyGlProgram currentlyUsedProgram = null;

    public static Factory factory()
    {
        return new Factory();
    }

    private MyGlProgram() {
        try {
            glReference = GlProgramManager.createProgram();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void link() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);

        fragmentShader.attachTo(this);
        vertexShader.attachTo(this);

        GlStateManager.linkProgram(glReference);

        int i = GlStateManager.getProgram(glReference, 35714);
        if (i == 0) {
            System.err.println("Error encountered when linking program ");
            System.err.println(GlStateManager.getProgramInfoLog(glReference, 32768));
        }

    }

    public <T extends Uniform<U>,U> void pushUniform(T uniform,U data){
        if(!isUsed())
        {
            System.err.println("This Program is not currently used. Aborting Uniform push");
            return;
        }
        int location = uniforms.get(uniform);
        if(location<0){
            System.err.println("Uniform "+uniform.getName()+" does not exist in this program");
            return;
        }
        uniform.push(uniforms.get(uniform),data);
    }

    public void use()
    {
        currentlyUsedProgram = this;
        GlStateManager.useProgram(glReference);
    }
    public void unUse()
    {
        currentlyUsedProgram = null;
        GlStateManager.useProgram(0);
    }
    public boolean isUsed(){
        return currentlyUsedProgram==this;
    }


    public int getGlReference() {
        return glReference;
    }

    public static class Factory{
        private MyGlProgram product = new MyGlProgram();

        public Factory vertexShaderFromSource(String source) throws IOException {
            product.vertexShader = MyGlShader.createFromSource(MyGlShader.Type.VERTEX,source);
            return this;
        }

        public Factory fragmentShaderFromSource(String source) throws IOException {
            product.fragmentShader = MyGlShader.createFromSource(MyGlShader.Type.FRAGMENT,source);
            return this;
        }

        public Factory vertexShaderFromResource(Identifier identifier, ResourceManager resourceManager) throws IOException {
            product.vertexShader = MyGlShader.createFromResource(MyGlShader.Type.VERTEX,identifier,resourceManager);
            return this;
        }

        public Factory fragmentShaderFromResource(Identifier identifier, ResourceManager resourceManager) throws IOException {
            product.fragmentShader = MyGlShader.createFromResource(MyGlShader.Type.FRAGMENT,identifier,resourceManager);
            return this;
        }

        public Factory uniform(Uniform uniform){
            product.uniforms.put(uniform,-1);
            return this;
        }

        public MyGlProgram create() {
            if(product.fragmentShader==null){
                System.err.println("Fragment Shader is required, but not set");
                return null;
            }
            if(product.vertexShader==null){
                System.err.println("Vertex Shader is required, but not set");
                return null;
            }

            product.link();

            for (Map.Entry<Uniform, Integer> entry : product.uniforms.entrySet()) {
                entry.setValue(GL20.glGetUniformLocation(product.glReference,entry.getKey().getName()));

                if(entry.getValue()<0){
                    System.err.println("Uniform "+entry.getKey().getName()+" does not exist in this program");
                }
            }

            return product;
        }
    }


}