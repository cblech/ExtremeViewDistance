package net.fabricmc.example.renderer.uniform;

import org.lwjgl.opengl.GL20;

public class UniformInt extends Uniform<Integer>{

    public UniformInt(String name) {
        super(name);
    }

    @Override
    public void push(int location, Integer data) {
        GL20.glUniform1i(location,data);
    }
}
