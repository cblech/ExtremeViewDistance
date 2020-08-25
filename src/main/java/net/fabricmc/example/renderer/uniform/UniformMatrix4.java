package net.fabricmc.example.renderer.uniform;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class UniformMatrix4 extends Uniform<Matrix4f> {
    public UniformMatrix4(String name) {
        super(name);
    }

    @Override
    public void push(int location, Matrix4f data) {
        ByteBuffer bb = ByteBuffer.allocateDirect(16*4);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        FloatBuffer fb = bb.asFloatBuffer();
        data.writeToBuffer(fb);

        GL20.glUniformMatrix4fv(location, false, fb);
    }
}
