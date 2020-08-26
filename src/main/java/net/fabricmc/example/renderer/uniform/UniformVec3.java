package net.fabricmc.example.renderer.uniform;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL20;

public class UniformVec3 extends Uniform<Vector3f>{
    public UniformVec3(String name) {
        super(name);
    }

    public void push(int location, Vec3d data) {
        push(location,new Vector3f(data));
    }

    @Override
    public void push(int location, Vector3f data) {
        GL20.glUniform3f(location,data.getX(),data.getY(),data.getZ());
    }
}

