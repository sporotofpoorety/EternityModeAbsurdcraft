package org.sporotofpoorety.eternitymode.util;


import java.util.ArrayList;


import javax.annotation.Nullable;


import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


import org.sporotofpoorety.eternitymode.entity.EntityThrownBlock;




public final class EntityUtil {


    @Nullable
    public static BlockPos findSolidBlockBelow(Entity entity, int maxDepth, float addRandomRadius)
    {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos
        (
            (int) (entity.posX + (entity.world.rand.nextFloat() * ((entity.width / 2.0F)
                + (entity.world.rand.nextFloat() - entity.world.rand.nextFloat()) * addRandomRadius))),
            (int) entity.getEntityBoundingBox().minY,
            (int) (entity.posZ + (entity.world.rand.nextFloat() * ((entity.width / 2.0F)
                + (entity.world.rand.nextFloat() - entity.world.rand.nextFloat()) * addRandomRadius)))
        );

        World world = entity.world;

        for (int depthAt = 0; depthAt < maxDepth; depthAt++)
        {
            pos.setY(pos.getY() - 1);

            IBlockState blockState = world.getBlockState(pos);

            if (blockState.getBlock().isFullBlock(blockState))
            {
                return pos.toImmutable();
            }
        }
        return null;
    }




//Generate and return random thrown blocks 
//from a specified volume, and optionally destroy
    @Nullable
    public static ArrayList<EntityThrownBlock> generateAndReturnRandomBlocks(Entity searchOrigin, EntityLivingBase owner,
    int blockTotal, int searchWidth, int searchDepth, int destroyMode)
    {
//Block list to fill and return
        ArrayList<EntityThrownBlock> blockList = new ArrayList<>();


//For each block
        for(int blockAt = 0; blockAt < blockTotal; blockAt++)
        {
//Get origin
            BlockPos blockOrigin = findSolidBlockBelow(searchOrigin, searchDepth, (float) searchWidth);


//If origin not null
            if(blockOrigin != null)
            {
//Make block
                EntityThrownBlock thrownBlock = new EntityThrownBlock
                (
//Try using origin for pos (may need to add 0.5D for intended behavior, i don't know)
                    searchOrigin.world, owner, blockOrigin, blockOrigin.getX(), blockOrigin.getY(), blockOrigin.getZ(), 1.0F
                );


//Add block to list
                blockList.add(thrownBlock);


//Only destroy high up blocks
                if(destroyMode == 2)
                {
/*
//Optionally destroy blocks
                    miscUtil.destroyBlockPos(blockOrigin, 9999.0F, false, 2);
*/
                }
            }
        }


//Return list
        return blockList;
    }
}
