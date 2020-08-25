package net.fabricmc.example.renderer.uniform;


import org.lwjgl.opengl.GL20;

public class UniformFloat extends Uniform<Float>{
    public UniformFloat(String name) {
        super(name);
    }

    @Override
    public void push(int location, Float data) {
        GL20.glUniform1f(location,data);
    }
}