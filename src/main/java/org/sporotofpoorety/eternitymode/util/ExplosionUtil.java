package org.sporotofpoorety.eternitymode.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import javax.annotation.Nullable;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


import net.minecraft.block.Block;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.client.ExplosiveHandler;
import org.sporotofpoorety.eternitymode.util.BlockUtil;




public final class ExplosionUtil 
{

    public static void performOptimizedExplosion(World worldIn, Entity directSrc, EntityLivingBase indirSrc, double atX, double atY, double atZ,
    double radius, boolean dealsDamage, float damage, boolean hasPush, double pushForce, boolean breakBlocks, float breakHardness, boolean setsFire, 
    boolean hasParticles, int particleType, boolean hasSound)
    {

        if(indirSrc != null)
        {
//Simple AABB damage and knockback check
            if(dealsDamage)
            {
//AABB and entities
                AxisAlignedBB explosionAABB = new AxisAlignedBB(atX - radius, atY - radius, atZ - radius, atX + radius, atY + radius, atZ + radius);
                List<Entity> affectedEntities = worldIn.getEntitiesWithinAABBExcludingEntity(indirSrc, explosionAABB);


//Hit entity if living, not same team as caster, not immune to explosions
                for(Entity affectedEntity : affectedEntities)
                {
                    if(affectedEntity instanceof EntityLivingBase && !affectedEntity.isOnSameTeam(indirSrc)
                    && !affectedEntity.isImmuneToExplosions())
                    {
//Direct dmg
                        if(directSrc == indirSrc) { affectedEntity.attackEntityFrom(DamageSource.causeExplosionDamage(indirSrc), damage); }
//Indirect dmg
                        else { affectedEntity.attackEntityFrom(DamageSource.causeIndirectDamage(directSrc, indirSrc).setExplosion(), damage); }


//If has push, push entity
                        if(hasPush)
                        {
                            double entityDist = Math.sqrt
                            (Math.pow(affectedEntity.posX - atX, 2) + Math.pow(affectedEntity.posY - atY, 2) + Math.pow(affectedEntity.posZ - atZ, 2));

                            affectedEntity.motionX += pushForce * (affectedEntity.posX - atX) / entityDist;
                            affectedEntity.motionY += pushForce * (affectedEntity.posY - atY) / entityDist;
                            affectedEntity.motionZ += pushForce * (affectedEntity.posZ - atZ) / entityDist;
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
                    BlockUtil.destroyBlockPos(checkPos, worldIn, breakHardness, false, false, 69420);
                }


//If explosion should set fire
                if(setsFire)
                {
//If pos is air and has solid block below
                    if(worldIn.getBlockState(checkPos).getMaterial() == Material.AIR && worldIn.getBlockState(checkPos.down()).isFullBlock())
                    {
//Set fire
                        worldIn.setBlockState(checkPos, Blocks.FIRE.getDefaultState());
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
                    worldIn.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, atX, atY, atZ, 1.0D, 0.0D, 0.0D);
                }
                else
                {
                    worldIn.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, atX, atY, atZ, 1.0D, 0.0D, 0.0D);
                }
            }

            if(particleType == 1)
            {
                ExplosiveHandler.spawnParticles(worldIn, atX, atY, atZ,
                (float) radius, false, breakBlocks);
            }

            if(particleType == 2)
            {
                explosionVisual(worldIn, atX, atY, atZ, (float) radius);
            }
        }




        if(hasSound)
        {
            worldIn.playSound((EntityPlayer)null, atX, atY, atZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.2F) * 0.7F);
        }
    }




//Just recreating vanilla visuals logic
    public static void explosionVisual(World worldIn, double x, double y, double z, float radius)
    {
        List<BlockPos> affectedBlockPositions = Lists.<BlockPos>newArrayList();


        Set<BlockPos> posSet = Sets.<BlockPos>newHashSet();
        int i = 16;

        for (int j = 0; j < 16; ++j)
        {
            for (int k = 0; k < 16; ++k)
            {
                for (int l = 0; l < 16; ++l)
                {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15)
                    {
                        double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
                        double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
                        double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / d3;
                        d1 = d1 / d3;
                        d2 = d2 / d3;
                        float f = radius * (0.7F + worldIn.rand.nextFloat() * 0.6F);
                        double d4 = x;
                        double d6 = y;
                        double d8 = z;

                        for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F)
                        {
                            BlockPos blockpos = new BlockPos(d4, d6, d8);
                            IBlockState iblockstate = worldIn.getBlockState(blockpos);

                            if (iblockstate.getMaterial() != Material.AIR)
                            {
                                float f2 = iblockstate.getBlock().getExplosionResistance((Entity)null);
                                f -= (f2 + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F)
                            {
                                posSet.add(blockpos);
                            }

                            d4 += d0 * 0.30000001192092896D;
                            d6 += d1 * 0.30000001192092896D;
                            d8 += d2 * 0.30000001192092896D;
                        }
                    }
                }
            }
        }


        affectedBlockPositions.addAll(posSet);


        if (radius >= 2.0F)
        {
            worldIn.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, x, y, z, 1.0D, 0.0D, 0.0D);
        }
        else
        {
            worldIn.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, x, y, z, 1.0D, 0.0D, 0.0D);
        }


        for (BlockPos blockpos : affectedBlockPositions)
        {
            IBlockState iblockstate = worldIn.getBlockState(blockpos);
            Block block = iblockstate.getBlock();


            double d0 = (double)((float)blockpos.getX() + worldIn.rand.nextFloat());
            double d1 = (double)((float)blockpos.getY() + worldIn.rand.nextFloat());
            double d2 = (double)((float)blockpos.getZ() + worldIn.rand.nextFloat());
            double d3 = d0 - x;
            double d4 = d1 - y;
            double d5 = d2 - z;
            double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
            d3 = d3 / d6;
            d4 = d4 / d6;
            d5 = d5 / d6;
            double d7 = 0.5D / (d6 / (double)radius + 0.1D);
            d7 = d7 * (double)(worldIn.rand.nextFloat() * worldIn.rand.nextFloat() + 0.3F);
            d3 = d3 * d7;
            d4 = d4 * d7;
            d5 = d5 * d7;
            worldIn.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + x) / 2.0D, (d1 + y) / 2.0D, (d2 + z) / 2.0D, d3, d4, d5);
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5);
        }
    }

}
