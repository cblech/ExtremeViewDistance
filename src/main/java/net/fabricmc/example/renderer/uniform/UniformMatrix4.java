package net.fabricmc.example.renderer.uniform;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class UniformMatrix4 extends Uniform<Matrix4f> {

    private static ByteBuffer bb = ByteBuffer.allocateDirect(16*4);
    private static FloatBuffer fb;
    public UniformMatrix4(String name) {
        super(name);
    }

    static {
        bb.order(ByteOrder.LITTLE_ENDIAN);
        fb = bb.asFloatBuffer();
    }

    @Override
    public void push(int location, Matrix4f data) {

        data.writeToBuffer(fb);

        GL20.glUniformMatrix4fv(location, false, fb);
    }
}
