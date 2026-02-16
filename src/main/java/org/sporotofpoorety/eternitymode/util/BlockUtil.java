package org.sporotofpoorety.eternitymode.util;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;




public final class BlockUtil 
{

    @Nullable
    public static BlockPos findFirstSolidBlock(Entity entity, float addRandomRadius, int maxDepth, int searchMode)
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


//Search below
        if(searchMode == 1)
        {
            for (int depthAt = 0; depthAt < maxDepth; depthAt++)
            {
                pos.setY(pos.getY() - 1);

                IBlockState blockState = world.getBlockState(pos);

                if (blockState.getBlock().isFullBlock(blockState))
                {
                    return pos.toImmutable();
                }
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


        float hardness = block.getBlockHardness(state, worldAt, destroyPos);
        if (hardness < 0 || hardness > maxHardness) 
        {
            return;
        }

        worldAt.setBlockToAir(destroyPos);
    }

    public static void destroyBlockPos(BlockPos destroyPos, World worldAt, float maxHardness, boolean allowNonSolids, boolean dropBlocks, int destroyMode)
    {
        worldAt.setBlockToAir(destroyPos);

//Get blockstate of blockpos
        IBlockState state = worldAt.getBlockState(destroyPos);
//Get block of that blockstate
        Block block = state.getBlock();


        if (block.isAir(state, worldAt, destroyPos)) 
        {
            return;
        }


        float hardness = block.getBlockHardness(state, worldAt, destroyPos);
        if (hardness < 0 || hardness > maxHardness) 
        {
            return;
        }


        Material mat = state.getMaterial();
        if (!allowNonSolids) 
        {
            if (!mat.isSolid()) 
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

}
