package net.fabricmc.example.renderer.util.converter;


import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

public class McJoml {
    public static org.joml.Vector3f toJoml(Vector3f value)
    {
        return new org.joml.Vector3f(value.getX(),value.getY(),value.getZ());
    }
    public static org.joml.Vector3d toJoml(Vec3d value)
    {
        return new org.joml.Vector3d(value.x,value.y,value.z);
    }

    public static org.joml.Vector3f toJomlVector3f(Vec3d value)
    {
        return new org.joml.Vector3f((float)value.x,(float)value.y,(float)value.z);
    }

    public static Matrix4f toMc(org.joml.Matrix4f value)
    {
        Matrix4f m = new Matrix4f();
        ((Matrix4fInitializer)(Object)m).set(value);
        return m;
    }
}
