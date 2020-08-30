package net.fabricmc.example.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.example.renderer.uniform.UniformFloat;
import net.fabricmc.example.renderer.uniform.UniformInt;
import net.fabricmc.example.renderer.uniform.UniformMatrix4;
import net.fabricmc.example.renderer.uniform.UniformVec3;
import net.fabricmc.loader.util.sat4j.core.Vec;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.util.PngFile;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.CallbackI;
import sun.awt.image.PNGImageDecoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;


public class FarWorldTile {
    private final static int FLOAT_SIZE = 4;

    private static int VBO;
    private static int VAO;
    private static MyGlProgram program;

    public static void removeProgram() {
        program = null;
    }

    public static final UniformInt uDepthSampler = new UniformInt("uDepthSampler");
    public static final UniformVec3 uEyeWorldPos = new UniformVec3("uEyeWorldPos");
    public static final UniformMatrix4 uModelMat = new UniformMatrix4("uModelMat");
    public static final UniformMatrix4 uViewProjectionMat = new UniformMatrix4("uViewProjectionMat");

    public static void setFrameValues(Vector3f eyeWorldPos, Matrix4f viewProjectionMat) {
        requireProgramUsed();
        program.pushUniform(uEyeWorldPos, eyeWorldPos);
        program.pushUniform(uViewProjectionMat, viewProjectionMat);
    }

    public static void requireProgramUsed(){
        if(!program.isUsed())
            throw new RuntimeException("Far World Tile - Shaderprogram has to be used");
    }

    public static void useProgram(){
        program.use();
    }

    public static void unUseProgram() {
        program.unUse();
    }
    //##################################################################################################################

    private float size = 1f;
    private Vector3f position = new Vector3f();

    private Matrix4f transform = new Matrix4f();
    private ResourceTexture rt;
    GeneratedTexture gt;
    private int glDepthTexture = -1;

    private World world;

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
        updateTransformMat();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        updateTransformMat();
    }

    private void updateTransformMat() {
        //transform.loadIdentity();
        //transform.multiply(Matrix4f.translate(position.getX(),position.getY(),position.getZ()));
        //transform.multiply(Matrix4f.scale(size,size,size));
        transform
                .identity()
                .translate(position)
                .scale(size);
    }

    public void setDepthTexture(Identifier identifier, ResourceManager resourceManager) {
        //try {
            //Resource depthTexture = resourceManager.getResource(identifier);
            //PngFile pngFile = new PngFile("Depth Texture", depthTexture.getInputStream());
            //System.out.println(pngFile.height);
            //rt = new ResourceTexture(identifier);
            //rt.load(resourceManager);
            //rt.bindTexture();


            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);    // set texture wrapping to GL_REPEAT (default wrapping method)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
            // set texture filtering parameters
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            program.pushUniform(uDepthSampler, 0);
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
    }

    public void setTexturesFromWorld() {
        program.use();

        gt = new GeneratedTexture(16,16);

        //if(glDepthTexture>=0)
        //{
        //    GL11.glDeleteTextures(glDepthTexture);
        //}

        //glDepthTexture = GL11.glGenTextures();

        //ByteBuffer bb = ByteBuffer.allocateDirect(16 * 16 * 3);
        //byte[] bytes = new byte[16 * 16 * 3];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                byte h = (byte) world.getChunk(0, 0).getHeightmap(Heightmap.Type.WORLD_SURFACE).get(i, j);

                gt.getNativeImage().setPixelColor(i,j,0xffffff00+h);
                //bytes[(i + j * 16)*3]=h;
                //bytes[(i + j * 16)*3+1]=h;
                //bytes[(i + j * 16)*3+2]=h;
                //bytes[(i + j * 16)*3 + 1] = h;
                //bytes[(i + j * 16)*3 + 2] = h;
            }
        }

        gt.upload();

        //GlStateManager.pixelStore(GL11.GL_UNPACK_ROW_LENGTH,0);
        //GlStateManager.pixelStore(GL11.GL_UNPACK_SKIP_PIXELS,0);
        //GlStateManager.pixelStore(GL11.GL_UNPACK_SKIP_ROWS,0);


        //GL13.glActiveTexture(GL13.GL_TEXTURE0);

        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);    // set texture wrapping to GL_REPEAT (default wrapping method)
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        //// set texture filtering parameters
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        //bb.put(bytes);

        //TODO MAKE TEXTURES WORK
        //GL11.glTexImage2D(GL11.GL_TEXTURE_2D,0,GL11.GL_RGB8,16,16,0,GL11.GL_RGB,GL11.GL_BYTE,bb);

        //GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);


        //program.pushUniform(uDepthSampler, 0);
    }

    public void draw() {
        requireProgramUsed();

        //GL13.glActiveTexture(GL13.GL_TEXTURE0);
        //GL11.glBindTexture(GL11.GL_TEXTURE_2D,glDepthTexture);
        //if(rt!=null)
        //    rt.bindTexture();

        gt.bindTexture();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();

        ARBVertexArrayObject.glBindVertexArray(VAO);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);

        //GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

        //mat.loadIdentity();
        //mat.multiply(Matrix4f.translate(i * offset, 63, k * offset));
        program.pushUniform(FarWorldTile.uModelMat, transform);
        //vertexBufferObject.draw(ARBTessellationShader.GL_PATCHES);
        GL11.glDrawArrays(ARBTessellationShader.GL_PATCHES, 0, 6);


        //GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

    }

    FarWorldTile(IGameRendererExposed gameRendererExposed, World world) {
        this.world = world;
        if (program == null) {
            int currentVAO = GL11.glGetInteger(ARBVertexArrayObject.GL_VERTEX_ARRAY_BINDING);

            try {
                program = MyGlProgram.factory()
                        .vertexShaderFromResource(new Identifier("extremeviewdistance:shader/world_vertex.glsl"), gameRendererExposed.getResourceContainer())
                        .tesselationControlShaderFromResource(new Identifier("extremeviewdistance:shader/world_tesselation_control.glsl"), gameRendererExposed.getResourceContainer())
                        .tesselationEvaluationShaderFromResource(new Identifier("extremeviewdistance:shader/world_tesselation_evaluation.glsl"), gameRendererExposed.getResourceContainer())
                        .fragmentShaderFromResource(new Identifier("extremeviewdistance:shader/world_fragment.glsl"), gameRendererExposed.getResourceContainer())
                        .uniform(uModelMat)
                        .uniform(uViewProjectionMat)
                        .uniform(uEyeWorldPos)
                        .uniform(uDepthSampler)
                        .create();

                VAO = GL30.glGenVertexArrays();
                GL30.glBindVertexArray(VAO);

                VBO = GL15.glGenBuffers();

                float[] verts = {

                        //ppp
                        1, 0, 1, 0.0f, 0.0f, 1.0f, 1.0F,
                        1, 0, 0, 0.0f, 0.0f, 1.0f, 1.0F,
                        0, 0, 1, 0.0f, 0.0f, 1.0f, 1.0F,
                        0, 0, 1, 0.0f, 0.0f, 1.0f, 1.0F,
                        1, 0, 0, 0.0f, 0.0f, 1.0f, 1.0F,
                        0, 0, 0, 0f, 0.0f, 1.0f, 1.0F
                };

                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verts, GL15.GL_STATIC_DRAW);

                GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, (3 + 4) * FLOAT_SIZE, 0);
                GL20.glEnableVertexAttribArray(0);
                GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, (3 + 4) * FLOAT_SIZE, 3 * FLOAT_SIZE);
                GL20.glEnableVertexAttribArray(1);

            } catch (IOException e) {
                e.printStackTrace();
            }

            ARBVertexArrayObject.glBindVertexArray(currentVAO);
        }

        updateTransformMat();
    }


}
