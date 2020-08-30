package net.fabricmc.example.renderer.uniform;

import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

public class UniformVec3 extends Uniform<Vector3f>{
    public UniformVec3(String name) {
        super(name);
    }

    @Override
    public void push(int location, Vector3f data) {
        GL20.glUniform3f(location,data.x,data.y,data.z);
    }
}

