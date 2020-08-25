package net.fabricmc.example.renderer.uniform;

public abstract class Uniform<T>{

    public Uniform(String name){
        this.name=name;
    }
    private String name;
    public String getName(){
        return name;
    }

    public abstract void push(int location, T data);
}