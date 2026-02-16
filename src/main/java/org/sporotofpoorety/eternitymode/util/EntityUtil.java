package org.sporotofpoorety.eternitymode.util;


import java.util.ArrayList;


import javax.annotation.Nullable;


import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


import org.sporotofpoorety.eternitymode.entity.EntityThrownBlock;
import org.sporotofpoorety.eternitymode.util.BlockUtil;




public final class EntityUtil 
{

//Generate and return random thrown blocks 
//from a specified volume, and optionally destroy
    @Nullable
    public static ArrayList<EntityThrownBlock> generateAndReturnRandomBlocks(Entity searchOrigin, EntityLivingBase owner,
    int blockTotal, int searchWidth, int searchDepth, int searchMode, int destroyMode)
    {
//Block list to fill and return
        ArrayList<EntityThrownBlock> blockList = new ArrayList<>();


//For each block
        for(int blockAt = 0; blockAt < blockTotal; blockAt++)
        {
//Get origin
            BlockPos blockOrigin = BlockUtil.findFirstSolidBlock(searchOrigin, (float) searchWidth, searchDepth, searchMode);


//If origin not null
            if(blockOrigin != null)
            {
//Make entity block at origin
                EntityThrownBlock thrownBlock = new EntityThrownBlock
                (
                    searchOrigin.world, owner, blockOrigin, blockOrigin.getX() + 0.5D, blockOrigin.getY() + 0.5D, blockOrigin.getZ() + 0.5D, 1.0F
                );


//Add block to list
                blockList.add(thrownBlock);


                if(!searchOrigin.world.isRemote)
                {
//Only destroy high up blocks
                    if(destroyMode == 2)
                    {
//Optionally destroy blocks
//                      BlockUtil.destroyBlockPos(blockOrigin, searchOrigin.world, 9999.0F);
                    }
                }
            }
        }


//Return list
        return blockList;
    }
}
