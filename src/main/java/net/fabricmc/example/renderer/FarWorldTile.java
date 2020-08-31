package net.fabricmc.example.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.example.renderer.uniform.UniformInt;
import net.fabricmc.example.renderer.uniform.UniformMatrix4;
import net.fabricmc.example.renderer.uniform.UniformVec3;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;

import java.io.IOException;


public class FarWorldTile {
    private final static int FLOAT_SIZE = 4;

    private static int VBO;
    private static int VAO;
    private static MyGlProgram program;

    public static void removeProgram() {
        program = null;
    }

    public static final UniformInt uDepthSampler = new UniformInt("uDepthSampler");
    public static final UniformInt uColorSampler = new UniformInt("uColorSampler");
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

    private int size = 1;
    private Vector3f position = new Vector3f();

    private Matrix4f transform = new Matrix4f();
    private ResourceTexture rt;
    GeneratedTexture depthTexture;
    GeneratedTexture colorTexture;
    private int glDepthTexture = -1;

    private World world;

    public float getSize() {
        return size;
    }

    public void setSize(int size) {
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
                .translate(position.x,position.y,position.z)
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


            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);    // set texture wrapping to GL_REPEAT (default wrapping method)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
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

        depthTexture = new GeneratedTexture(size,size);
        colorTexture = new GeneratedTexture(size,size);

        //if(glDepthTexture>=0)
        //{
        //    GL11.glDeleteTextures(glDepthTexture);
        //}

        //glDepthTexture = GL11.glGenTextures();

        //ByteBuffer bb = ByteBuffer.allocateDirect(16 * 16 * 3);
        //byte[] bytes = new byte[16 * 16 * 3];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                byte h = (byte) world.getChunk(
                        ((int)position.x  +i)/16,
                        ((int)position.z  +j)/16,
                        ChunkStatus.EMPTY).getHeightmap(Heightmap.Type.WORLD_SURFACE)
                        .get(((int)position.x  +i)%16, ((int)position.z  +j)%16);


                colorTexture.getNativeImage().setPixelColor(i,j,DataGatherer.getColor(world,(int)position.x  +i,(int)position.z  +j));
                depthTexture.getNativeImage().setPixelColor(i,j,0xffffff00+DataGatherer.getHeight(world,(int)position.x  +i,(int)position.z  +j));
                //bytes[(i + j * 16)*3]=h;
                //bytes[(i + j * 16)*3+1]=h;
                //bytes[(i + j * 16)*3+2]=h;
                //bytes[(i + j * 16)*3 + 1] = h;
                //bytes[(i + j * 16)*3 + 2] = h;
            }
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        depthTexture.upload();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_EDGE);    // set texture wrapping to GL_REPEAT (default wrapping method)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_EDGE);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        program.pushUniform(uDepthSampler, 0);


        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        colorTexture.upload();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_EDGE);    // set texture wrapping to GL_REPEAT (default wrapping method)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_EDGE);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        program.pushUniform(uColorSampler, 1);

    }

    public void draw() {
        requireProgramUsed();

        //GL13.glActiveTexture(GL13.GL_TEXTURE0);
        //GL11.glBindTexture(GL11.GL_TEXTURE_2D,glDepthTexture);
        //if(rt!=null)
        //    rt.bindTexture();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        depthTexture.bindTexture();
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        colorTexture.bindTexture();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);


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
                        .uniform(uColorSampler)
                        .create();

                VAO = GL30.glGenVertexArrays();
                GL30.glBindVertexArray(VAO);

                VBO = GL15.glGenBuffers();

                float[] verts = {

                        //ppp
                        1, 0, 1, 0.0f, 0.0f, 1.0f, 1.0f,
                        1, 0, 0, 0.0f, 0.0f, 1.0f, 1.0f,
                        0, 0, 1, 0.0f, 0.0f, 1.0f, 1.0f,
                        0, 0, 1, 0.0f, 0.0f, 1.0f, 1.0f,
                        1, 0, 0, 0.0f, 0.0f, 1.0f, 1.0f,
                        0, 0, 0, 0.0f, 0.0f, 1.0f, 1.0f
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
