package org.sporotofpoorety.eternitymode.util;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.entity.EntityThrownBlock;




public final class BlockUtil 
{

    @Nullable
    public static BlockPos findFirstSolidBlock(Entity entity, float addRandomRadius, int maxDepth, int searchMode)
    {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos
        (
            (int) (entity.posX 
                + (entity.world.rand.nextFloat() - entity.world.rand.nextFloat()) * ((entity.width / 2.0F) + addRandomRadius)),
            (int) entity.getEntityBoundingBox().minY,
            (int) (entity.posZ 
                + (entity.world.rand.nextFloat() - entity.world.rand.nextFloat()) * ((entity.width / 2.0F) + addRandomRadius))
        );

        World world = entity.world;


//Search below
        if(searchMode == 1)
        {
            for (int depthAt = 0; depthAt < maxDepth; depthAt++)
            {
                IBlockState blockState = world.getBlockState(pos);

                if (blockState.getBlock().isFullBlock(blockState))
                {
                    return pos.toImmutable();
                }

                pos.setY(pos.getY() - 1);
            }
        }
//Search above and below
        if(searchMode == 2)
        {
//Starting Y
            int startingY = pos.getY();

            for (int depthAt = 0; depthAt < maxDepth; depthAt++)
            {
//Iterate up
                pos.setY(startingY + depthAt);

                IBlockState blockState = world.getBlockState(pos);

                if (blockState.getBlock().isFullBlock(blockState))
                {
                    return pos.toImmutable();
                }


//Iterate down
                pos.setY(startingY - depthAt);

                blockState = world.getBlockState(pos);

                if (blockState.getBlock().isFullBlock(blockState))
                {
                    return pos.toImmutable();
                }
            }
        }

        return null;
    }


    @Nullable
    public static BlockPos findFirstSolidBlock(World world, int atX, int atY, int atZ, float addRandomRadius, int maxDepth, int searchMode)
    {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos
        (
            (int) (atX + ((world.rand.nextFloat() - world.rand.nextFloat()) * addRandomRadius)),
            (int) atY,
            (int) (atZ + ((world.rand.nextFloat() - world.rand.nextFloat()) * addRandomRadius))
        );


//Search below
        if(searchMode == 1)
        {
            for (int depthAt = 0; depthAt < maxDepth; depthAt++)
            {
                IBlockState blockState = world.getBlockState(pos);

                if (blockState.getBlock().isFullBlock(blockState))
                {
                    return pos.toImmutable();
                }

                pos.setY(pos.getY() - 1);
            }
        }
//Search above and below
        if(searchMode == 2)
        {
//Starting Y
            int startingY = pos.getY();

            for (int depthAt = 0; depthAt < maxDepth; depthAt++)
            {
//Iterate up
                pos.setY(startingY + depthAt);

                IBlockState blockState = world.getBlockState(pos);

                if (blockState.getBlock().isFullBlock(blockState))
                {
                    return pos.toImmutable();
                }


//Iterate down
                pos.setY(startingY - depthAt);

                blockState = world.getBlockState(pos);

                if (blockState.getBlock().isFullBlock(blockState))
                {
                    return pos.toImmutable();
                }
            }
        }

        return null;
    }




    public static void destroyBlockPos(BlockPos destroyPos, World worldAt, float maxHardness)
    {
//Get blockstate of blockpos
        IBlockState state = worldAt.getBlockState(destroyPos);
//Get block of that blockstate
        Block block = state.getBlock();


        if (block.isAir(state, worldAt, destroyPos)) 
        {
            return;
        }


        float hardness = state.getBlockHardness(worldAt, destroyPos);
//      if (hardness > maxHardness || hardness <= 0.0F) //|| block == Blocks.BEDROCK || block == Blocks.END_PORTAL_FRAME) 
        if (hardness > maxHardness || block == Blocks.BEDROCK || block == Blocks.END_PORTAL_FRAME) 
        {
            return;
        }

        worldAt.setBlockToAir(destroyPos);
    }


    public static void destroyBlockPos(BlockPos destroyPos, World worldAt, float maxHardness, boolean allowNonSolids, boolean dropBlocks, int destroyMode)
    {
//Get blockstate of blockpos
        IBlockState state = worldAt.getBlockState(destroyPos);
//Get block of that blockstate
        Block block = state.getBlock();


        if (block.isAir(state, worldAt, destroyPos)) 
        {
            return;
        }


        float hardness = state.getBlockHardness(worldAt, destroyPos);
//      if (hardness > maxHardness || hardness <= 0.0F) //|| block == Blocks.BEDROCK || block == Blocks.END_PORTAL_FRAME) 
        if (hardness > maxHardness || block == Blocks.BEDROCK || block == Blocks.END_PORTAL_FRAME) 
        {
            return;
        }


        if (!allowNonSolids) 
        {
            if (!state.getMaterial().isSolid()) 
            {
                return;
            }
        }


        if (dropBlocks) 
        {
            worldAt.destroyBlock(destroyPos, true);
        } 
        else 
        {
            worldAt.setBlockToAir(destroyPos);
        }
    }




//Generate and return random thrown blocks 
//from a specified volume, and optionally destroy
    @Nullable
    public static ArrayList<EntityThrownBlock> generateAndReturnRandomBlocks(Entity searchOrigin, EntityLivingBase owner,
    int blockTotal, int searchWidth, int searchDepth, int searchMode, boolean destroyOrigin)
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
                    searchOrigin.world, blockOrigin.getX() + 0.5D, blockOrigin.getY() + 0.5D, blockOrigin.getZ() + 0.5D, 
                    owner, searchOrigin.world.getBlockState(blockOrigin), 
                    false, true, true, 1.0F
                );
                thrownBlock.dontBreakInitialPos = !destroyOrigin;


//Add block to list
                blockList.add(thrownBlock);
            }
        }


//Return list
        return blockList;
    }

}
