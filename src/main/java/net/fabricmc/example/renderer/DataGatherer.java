package net.fabricmc.example.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.HashMap;
import java.util.Map;

public class DataGatherer {
    private static Map<Block,Integer> blockColor = new HashMap<>();

    public static int getHeight(World world, int x, int z){
        return
                world.getChunk(x/16,z/16,
                ChunkStatus.EMPTY).getHeightmap(Heightmap.Type.WORLD_SURFACE)
                .get(x%16, z%16);
    }

    /**
     *
     * @param world the World
     * @param x x position
     * @param z z position
     * @return Color in ABGR format
     */
    public static int getColor(World world, int x, int z){
        int i = getHeight(world,x,z);
        BlockState bv= world.getBlockState(new BlockPos(x,i-1,z));

        if(bv.getBlock()==Blocks.NETHERRACK)
        {
            int fu = 0;
        }

        if(bv.getBlock()==Blocks.WATER)
        {
            return 0xff8e692e;
        }



        //MaterialColor c= bv.getTopMaterialColor(null,null);

        //return c.color + 0xff000000;


        try{
            return blockColor.get(bv.getBlock());
        }catch (Exception e){

            MaterialColor c= bv.getTopMaterialColor(null,null);

            return c.color + 0xff000000;

            //return 0xff000000;
        }



    }

    static {
        blockColor.put(Blocks.SNOW,                 0xffdddddd);
        blockColor.put(Blocks.GRASS_BLOCK,          0xff326b43);
        blockColor.put(Blocks.WATER,                0xff7a5926);
        blockColor.put(Blocks.STONE,                0xff444444);
        blockColor.put(Blocks.OAK_LEAVES,           0xff154f28);
        blockColor.put(Blocks.DARK_OAK_LEAVES,      0xff16552c);
        blockColor.put(Blocks.BIRCH_LEAVES,         0xff215821);
        blockColor.put(Blocks.RED_MUSHROOM_BLOCK,   0xff3133b6);
        blockColor.put(Blocks.BROWN_MUSHROOM_BLOCK, 0xff4f6e92);
        blockColor.put(Blocks.SAND,                 0xff97c0ca);
        blockColor.put(Blocks.KELP,                 0xff8e692e);
    }

}
