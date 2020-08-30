package net.fabricmc.example.mixin;

import net.fabricmc.example.renderer.util.converter.Matrix4fInitializer;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix4f.class)
public class Matrix4fMixin implements Matrix4fInitializer {

    @Shadow protected float a00;
    @Shadow protected float a01;
    @Shadow protected float a02;
    @Shadow protected float a03;
    @Shadow protected float a10;
    @Shadow protected float a11;
    @Shadow protected float a12;
    @Shadow protected float a13;
    @Shadow protected float a20;
    @Shadow protected float a21;
    @Shadow protected float a22;
    @Shadow protected float a23;
    @Shadow protected float a30;
    @Shadow protected float a31;
    @Shadow protected float a32;
    @Shadow protected float a33;

    @Override
    public void set(org.joml.Matrix4f values) {
        this.a00 = values.m00();
        this.a01 = values.m01();
        this.a02 = values.m02();
        this.a03 = values.m03();
        this.a10 = values.m10();
        this.a11 = values.m11();
        this.a12 = values.m12();
        this.a13 = values.m13();
        this.a20 = values.m20();
        this.a21 = values.m21();
        this.a22 = values.m22();
        this.a23 = values.m23();
        this.a30 = values.m30();
        this.a31 = values.m31();
        this.a32 = values.m32();
        this.a33 = values.m33();
    }
}
