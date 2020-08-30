package net.fabricmc.example.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;

import java.io.IOException;

public class GeneratedTexture extends AbstractTexture {

    private NativeImage nativeImage;

    public GeneratedTexture(int width, int height){
        nativeImage = new NativeImage(width,height,false);
    }

    @Override
    public void load(ResourceManager manager) throws IOException {

        //NativeImage nativeImage = new NativeImage(16,16,false);
    }

    public NativeImage getNativeImage(){
        return nativeImage;
    }

    public void upload() {
        TextureUtil.allocate(this.getGlId(), 0, nativeImage.getWidth(), nativeImage.getHeight());
        nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), false, false, false, true);
    }
}
