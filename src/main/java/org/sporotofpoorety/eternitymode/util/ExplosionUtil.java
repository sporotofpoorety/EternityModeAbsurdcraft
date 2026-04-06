package org.sporotofpoorety.eternitymode.util;


import java.util.ArrayList;
import java.util.List;


import javax.annotation.Nullable;


import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.client.ExplosiveHandler;
import org.sporotofpoorety.eternitymode.util.BlockUtil;




public final class ExplosionUtil 
{

    public static void performOptimizedExplosion(World world, EntityLivingBase caster, double atX, double atY, double atZ,
    double radius, boolean dealsDamage, float damage, boolean hasPush, double pushForce, boolean breakBlocks, float breakHardness, boolean setsFire, 
    boolean hasParticles, int particleType, boolean hasSound)
    {

        if(caster != null)
        {
//Simple AABB damage and knockback check
            if(dealsDamage)
            {
//AABB and entities
                AxisAlignedBB explosionAABB = new AxisAlignedBB(atX - radius, atY - radius, atZ - radius, atX + radius, atY + radius, atZ + radius);
                List<Entity> affectedEntities = world.getEntitiesWithinAABBExcludingEntity(caster, explosionAABB);


//Hit entity if living, not same team as caster, not immune to explosions
                for(Entity affectedEntity : affectedEntities)
                {
                    if(affectedEntity instanceof EntityLivingBase && !affectedEntity.isOnSameTeam(caster)
                    && !affectedEntity.isImmuneToExplosions())
                    {
                        affectedEntity.attackEntityFrom(DamageSource.causeExplosionDamage(caster), damage);

//If has push, push entity
                        if(hasPush)
                        {
                            double entityDist = Math.sqrt
                            (Math.pow(affectedEntity.posX - atX, 2) + Math.pow(affectedEntity.posY - atY, 2) + Math.pow(affectedEntity.posZ - atZ, 2));

                            affectedEntity.motionX += ((affectedEntity.posX - atX) * pushForce / Math.max(1.0D, entityDist));
                            affectedEntity.motionY += ((affectedEntity.posY - atY) * pushForce / Math.max(1.0D, entityDist));
                            affectedEntity.motionZ += ((affectedEntity.posZ - atZ) * pushForce / Math.max(1.0D, entityDist));
                        }
                    }
                }
            }
        }




//Block affecting logic
        if(breakBlocks || setsFire)
        {
//Block positions to potentially affect
            ArrayList<BlockPos> affectedBlockPositions = new ArrayList<>();


//Iterate block positions
            for (int blockX = (int) -radius; blockX <= (int) radius; ++blockX)
            {
                for (int blockY = (int) -radius; blockY <= (int) radius; ++blockY)
                {
                    for (int blockZ = (int) -radius; blockZ <= (int) radius; ++blockZ)
                    {
//Check potential pos
                        BlockPos potentialPos = new BlockPos((int) atX + blockX, (int) atY + blockY, (int) atZ + blockZ);

//Add potential pos
                        affectedBlockPositions.add(potentialPos);
                    }
                }
            }


//For each blockpos
            for(BlockPos checkPos : affectedBlockPositions)
            {
//If explosion should break blocks
                if(breakBlocks)
                {
//Destroy with conditions
                    BlockUtil.destroyBlockPos(checkPos, world, breakHardness, false, false, 69420);
                }


//If explosion should set fire
                if(setsFire)
                {
//If pos is air and has solid block below
                    if(world.getBlockState(checkPos).getMaterial() == Material.AIR && world.getBlockState(checkPos.down()).isFullBlock())
                    {
//Set fire
                        world.setBlockState(checkPos, Blocks.FIRE.getDefaultState());
                    }
                }
            }
        }




        if(hasParticles)
        {
            if(particleType <= 0)
            {
                if (radius >= 2.0D)
                {
                    world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, atX, atY, atZ, 1.0D, 0.0D, 0.0D);
                }
                else
                {
                    world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, atX, atY, atZ, 1.0D, 0.0D, 0.0D);
                }
            }

            if(particleType == 1)
            {
                ExplosiveHandler.spawnParticles(world, atX, atY, atZ,
                (float) radius, false, breakBlocks);
            }
        }




        if(hasSound)
        {
            world.playSound((EntityPlayer)null, atX, atY, atZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
        }
    }

}
