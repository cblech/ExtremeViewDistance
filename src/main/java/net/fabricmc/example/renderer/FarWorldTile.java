package net.fabricmc.example.renderer;

import net.fabricmc.example.renderer.uniform.UniformMatrix4;
import net.fabricmc.example.renderer.uniform.UniformVec3;
import net.fabricmc.loader.util.sat4j.core.Vec;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.CallbackI;

import java.io.IOException;

public class FarWorldTile {
    private final static int FLOAT_SIZE = 4;

    private static int VBO;
    private static int VAO;
    private static MyGlProgram program;
    public static void removeProgram(){
        program=null;
    }

    public static final UniformVec3 uEyeWorldPos = new UniformVec3("uEyeWorldPos");
    public static final UniformMatrix4 uModelMat = new UniformMatrix4("uModelMat");
    public static final UniformMatrix4 uViewProjectionMat = new UniformMatrix4("uViewProjectionMat");

    public static void setFrameValues(Vector3f eyeWorldPos, Matrix4f viewProjectionMat)
    {
        requireProgramUsed();
        program.pushUniform(uEyeWorldPos,eyeWorldPos);
        program.pushUniform(uViewProjectionMat,viewProjectionMat);
    }

    public static void requireProgramUsed(){
        if(!program.isUsed())
            throw new RuntimeException("Far World Tile - Shaderprogram has to be used");
    }

    public static void useProgram(){
        program.use();
    }


    private float size=1f;
    private Vector3f position = new Vector3f();

    private Matrix4f transform = new Matrix4f();

    public static void unUseProgram() {
        program.unUse();
    }

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

    private void updateTransformMat(){
        transform.loadIdentity();
        transform.multiply(Matrix4f.translate(position.getX(),position.getY(),position.getZ()));
        transform.multiply(Matrix4f.scale(size,size,size));
    }

    public void draw(){
        requireProgramUsed();

        ARBVertexArrayObject.glBindVertexArray(VAO);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

        //mat.loadIdentity();
        //mat.multiply(Matrix4f.translate(i * offset, 63, k * offset));
        program.pushUniform(FarWorldTile.uModelMat, transform);
        //vertexBufferObject.draw(ARBTessellationShader.GL_PATCHES);
        GL11.glDrawArrays(ARBTessellationShader.GL_PATCHES, 0, 6);


        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

    }

    FarWorldTile(IGameRendererExposed gameRendererExposed ){
        if(program==null){
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
                        .create();

                VAO = GL30.glGenVertexArrays();
                GL30.glBindVertexArray(VAO);

                VBO = GL15.glGenBuffers();

                float[] verts = {

                        //ppp
                        1, 0, 1, 0.0f, 0.0f, 1.0f, 1.0F,
                        1, 0, 0,    0.0f, 0.0f, 1.0f, 1.0F,
                        0,   0, 1,  0.0f, 0.0f, 1.0f, 1.0F,
                        0, 0, 1,    0.0f, 0.0f, 1.0f, 1.0F,
                        1, 0, 0,    0.0f, 0.0f, 1.0f, 1.0F,
                        0, 0, 0,         0f, 0.0f, 1.0f, 1.0F
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
