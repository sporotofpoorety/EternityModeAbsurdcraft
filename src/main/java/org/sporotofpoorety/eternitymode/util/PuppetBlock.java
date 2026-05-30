package org.sporotofpoorety.eternitymode.util;


import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import org.sporotofpoorety.eternitymode.util.PuppetEntity;




//An entity, UUID, relative pos, timer and glue flag (i might add more later)
public class PuppetBlock extends PuppetEntity 
{
    public BlockPos puppetBlockPos;
    public IBlockState puppetBlockState;
    public int puppetBlockMetadata;


    public PuppetBlock(Entity entity,
    double offsetX, double offsetY, double offsetZ, int controlTime, int controlState,
    BlockPos puppetBlockPos, IBlockState puppetBlockState, int puppetBlockMetadata) 
    {
        super(entity, offsetX, offsetY, offsetZ, controlTime, controlState);

        this.puppetBlockPos = puppetBlockPos;
        this.puppetBlockState = puppetBlockState;
        this.puppetBlockMetadata = puppetBlockMetadata;
    }
}
