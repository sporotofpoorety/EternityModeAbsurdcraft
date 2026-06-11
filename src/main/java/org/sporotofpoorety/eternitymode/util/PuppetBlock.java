package org.sporotofpoorety.eternitymode.util;


import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import org.sporotofpoorety.eternitymode.util.EntityBlockData;
import org.sporotofpoorety.eternitymode.util.PuppetEntity;




//An entity, UUID, relative pos, timer and glue flag (i might add more later)
public class PuppetBlock extends PuppetEntity 
{
    public EntityBlockData blockData;

    public PuppetBlock(Entity entity,
    double offsetX, double offsetY, double offsetZ, int controlTime, int controlState,
    BlockPos blockOrigin, IBlockState basisState, 
    boolean dontPlaceBlock, boolean shouldDropItem, boolean dealsDamage, float thrownBlockDamage, boolean isSolid) 
    {
        super(entity, offsetX, offsetY, offsetZ, controlTime, controlState);

        this.blockData = new EntityBlockData(blockOrigin, basisState, 
            dontPlaceBlock, shouldDropItem, dealsDamage, thrownBlockDamage);
        blockData.isSolid = isSolid;
    }
}
