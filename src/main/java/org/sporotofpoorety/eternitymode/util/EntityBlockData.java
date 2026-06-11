package org.sporotofpoorety.eternitymode.util;


import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;




public class EntityBlockData 
{
    public BlockPos blockOrigin; 
    public IBlockState basisState;
 
    public boolean dontPlaceBlock;
    public boolean shouldDropItem;
    public boolean dealsDamage;
    public float thrownBlockDamage;

    public boolean isSolid = true;


    public EntityBlockData(BlockPos blockOrigin, IBlockState basisState, 
    boolean dontPlaceBlock, boolean shouldDropItem, boolean dealsDamage, float thrownBlockDamage) 
    {
        this.blockOrigin = blockOrigin; 
        this.basisState = basisState;
     
        this.dontPlaceBlock = dontPlaceBlock;
        this.shouldDropItem = shouldDropItem;
        this.dealsDamage = dealsDamage;
        this.thrownBlockDamage = thrownBlockDamage;
    }
}
