package net.fabricmc.example

import net.fabricmc.example.renderer.RenererFarWorld

// For support join https://discord.gg/v6v4pMv

class RenderFarWorldMod{
    companion object{
        lateinit var rfw: RenererFarWorld

        @JvmStatic
        fun initRfw()
        {
            rfw = RenererFarWorld()
        }
    }
}


val renderFarWorldMod = RenderFarWorldMod();


@Suppress("unused")
fun init() {
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.
}

