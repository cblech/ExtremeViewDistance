package net.fabricmc.example.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GLFW.class)
public abstract class GLFWMixin {

    @Shadow @Final public static int GLFW_OPENGL_DEBUG_CONTEXT;
    @Shadow @Final public static int GLFW_TRUE;

    @Inject(method = "glfwCreateWindow(IILjava/lang/CharSequence;JJ)J",at=@At("HEAD"),remap = false)
    static void glfwCreateWindow(int width, int height, CharSequence title, long monitor, long share, CallbackInfoReturnable<Long> cir)
    {
        GLFW.glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT,GLFW_TRUE);
    }
}
