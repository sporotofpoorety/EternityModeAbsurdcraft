package org.sporotofpoorety.eternitymode.entity;


import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


import org.sporotofpoorety.eternitymode.entity.EntityWithOwner;




public class EntityThrownBlock extends EntityWithOwner
{

    protected static final DataParameter<BlockPos> ORIGIN = EntityDataManager.<BlockPos>createKey(EntityThrownBlock.class, DataSerializers.BLOCK_POS);
    public IBlockState basisState;


    public NBTTagCompound tileEntityData;


    public int fallTime;


    public boolean dontPlaceBlock;
    public boolean shouldDropItem;


    public boolean dealsDamage;
    public float thrownBlockDamage;




    public EntityThrownBlock(World worldIn)
    {
        super(worldIn);

        this.basisState = Blocks.STONE.getDefaultState();
    }

    public EntityThrownBlock(World worldIn, double x, double y, double z, 
    EntityLivingBase owner, IBlockState fallingBlockState, 
    boolean dontPlaceBlock, boolean shouldDropItem, boolean dealsDamage, float thrownBlockDamage)
    {
        super(worldIn, x, y, z, owner);
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);


        this.basisState = fallingBlockState;


        this.setOrigin(new BlockPos(this));


        this.dontPlaceBlock = dontPlaceBlock; 
        this.shouldDropItem = shouldDropItem; 

        this.dealsDamage = dealsDamage; 
        this.thrownBlockDamage = thrownBlockDamage; 
    }


    public void setBlockSolid(boolean solid)
    {
//Set gravity
        this.setNoGravity(!solid);
//Set clip
        this.noClip = !solid;
//Set deals damage
        this.dealsDamage = solid;
    }


    protected void entityInit()
    {
        this.dataManager.register(ORIGIN, BlockPos.ORIGIN);
    }


    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);


//Get basis block (air as failsafe)
        Block basisBlock = this.basisState == null ? Blocks.AIR : this.basisState.getBlock();
//Set block metadata
        compound.setByte("Data", (byte)basisBlock.getMetaFromState(this.basisState));

//Get basis block name
        ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(basisBlock);
//Save basis block name
        compound.setString("Block", resourcelocation == null ? "" : resourcelocation.toString());


//Save tile entity data
        if (this.tileEntityData != null) { compound.setTag("TileEntityData", this.tileEntityData); }


        compound.setInteger("FallTime", this.fallTime);


        compound.setBoolean("DontPlaceBlock", this.dontPlaceBlock);
        compound.setBoolean("DropItem", this.shouldDropItem);


        compound.setBoolean("DealsDamage", this.dealsDamage);
        compound.setFloat("ThrownBlockDamage", this.thrownBlockDamage);
    }


    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);


//Get block metadata
        int i = compound.getByte("Data") & 255;


//Get block basis from name, 
//then get state basis from metadata
        if (compound.hasKey("Block", 8)) 
            { this.basisState = Block.getBlockFromName(compound.getString("Block")).getStateFromMeta(i); }

//If block basis air or null
        Block basisBlock = this.basisState.getBlock();
        if (basisBlock == null || basisBlock.getDefaultState().getMaterial() == Material.AIR)
//Sand as failsafe
            { this.basisState = Blocks.SAND.getDefaultState(); }


//Get tile entity data
        if (compound.hasKey("TileEntityData")) { this.tileEntityData = compound.getCompoundTag("TileEntityData"); }


        if (compound.hasKey("FallTime")) { this.fallTime = compound.getInteger("FallTime"); }


        if (compound.hasKey("DontPlaceBlock")) { this.dontPlaceBlock = compound.getBoolean("DontPlaceBlock"); }
        if (compound.hasKey("DropItem")) { this.shouldDropItem = compound.getBoolean("DropItem"); }


        if (compound.hasKey("DealsDamage")) { this.dealsDamage = compound.getBoolean("DealsDamage"); }
        if (compound.hasKey("ThrownBlockDamage")) { this.thrownBlockDamage = compound.getFloat("ThrownBlockDamage"); }
    }




    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
//      if(this.owner == null) { System.out.println("Owner not"); } else { System.out.println("Owner yes"); }
//      if(this.controller == null) { System.out.println("Controller not"); } else { System.out.println("Controller yes"); }

//Get basis block
        Block basisBlock = this.basisState == null ? Blocks.AIR : this.basisState.getBlock();


//If state is invalid, set dead
        if (this.basisState == null || this.basisState.getMaterial() == Material.AIR)
        {
            this.setDead();
            return;
        }


//Basic owned entity update
        super.onUpdate();




//If first spawned
        if (this.fallTime++ == 0)
        {
            BlockPos blockPosAt = new BlockPos(this);

//If initial blockpos 
//corresponds to saved block
            if (this.world.getBlockState(blockPosAt).getBlock() == basisBlock)
//Break that blockpos
                { this.world.setBlockToAir(blockPosAt); }

//If it doesn't, kill this entity, as it's invalid
            else if (!this.world.isRemote)
                { this.setDead(); return; }
        }




//If should do damage
        if (this.dealsDamage)
        {
//Get entities within AABB
            List<Entity> list = Lists.newArrayList(this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox()));

//For each one, damage
            for (Entity entity : list)
            {
                if(entity != this.owner)
                {
                	if(this.owner == null)
                	{
                		entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this), this.thrownBlockDamage);
                	}
                	else
                	{
                		entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.owner), this.thrownBlockDamage);
                	}
                }
            }
        }




//Basic movement
        this.performBasicMovement();




        if (!this.world.isRemote)
        {
//Get block pos this is at
            BlockPos blockPosAt = new BlockPos(this);


//If this is on ground and is solid
            if (this.onGround && !this.noClip)
            {
//Hit ground logic
                this.onHitGround();
            }

//If this not on ground
            else
            {
//But fell out of bounds or for too long
                if (this.fallTime > 100 && !this.world.isRemote && (blockPosAt.getY() < 1 || blockPosAt.getY() > 256) || this.fallTime > 600)
                {
//Check for drop item
                    if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops"))
                    {
                        this.entityDropItem(new ItemStack(basisBlock, 1, basisBlock.damageDropped(this.basisState)), 0.0F);
                    }
//Then set dead
                    this.setDead();
                }
            }
        }
    }


//Controller validate failsafe
    @Override
    public void performControllerValidation()
    {
//If no controller
        if(!this.validateController())
        {
//Restore solidity
            this.setBlockSolid(true);
        }
    }




    public void onHitGround()
    {
        Block basisBlock = this.basisState.getBlock();
        BlockPos blockPosAt = new BlockPos(this);
        IBlockState blockStateAt = this.world.getBlockState(blockPosAt);

        boolean isPowder = this.basisState.getBlock() == Blocks.CONCRETE_POWDER;
        boolean isPowderInWater = isPowder && this.world.getBlockState(blockPosAt).getMaterial() == Material.WATER;


//If not powder in water
        if (!isPowderInWater 
//And this can fall through block under
        && BlockFalling.canFallThrough(this.world.getBlockState(new BlockPos(this.posX, this.posY - 0.1D, this.posZ))))
        {
//Don't hit ground yet
            this.onGround = false;
            return;
        }


//Bounce vfx
        this.motionX *= 0.7D;
        this.motionZ *= 0.7D;
        this.motionY *= -0.5D;





//Don't do placement logic on pistons (complex/buggy)
        if (blockStateAt.getBlock() == Blocks.PISTON_EXTENSION)
        {
            return;
        }


//Set dead
        this.setDead();


//Check if should place block
        if (!this.dontPlaceBlock)
        {

//If allowed to place at pos
            if (this.world.mayPlace(basisBlock, blockPosAt, true, EnumFacing.UP, (Entity)null)
//And block can't keep falling
            && (!BlockFalling.canFallThrough(this.world.getBlockState(blockPosAt.down())) || isPowderInWater)
//Place saved block state 
            && this.world.setBlockState(blockPosAt, this.basisState, 3))
            {

//Run custom logic for
//blocks like sand and gravel
                if (basisBlock instanceof BlockFalling)
                {
                    ((BlockFalling)basisBlock).onEndFalling(this.world, blockPosAt, this.basisState, blockStateAt);
                }


//Read tile entity data
                if (this.tileEntityData != null && basisBlock instanceof ITileEntityProvider)
                {
                    TileEntity tileentity = this.world.getTileEntity(blockPosAt);

                    if (tileentity != null)
                    {
                        NBTTagCompound nbttagcompound = tileentity.writeToNBT(new NBTTagCompound());

                        for (String s : this.tileEntityData.getKeySet())
                        {
                            NBTBase nbtbase = this.tileEntityData.getTag(s);

                            if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s))
                            {
                                nbttagcompound.setTag(s, nbtbase.copy());
                            }
                        }

                        tileentity.readFromNBT(nbttagcompound);
                        tileentity.markDirty();
                    }
                }
            }


//If not allowed to place block, drop it instead
            else if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops"))
            {
                this.entityDropItem(new ItemStack(basisBlock, 1, basisBlock.damageDropped(this.basisState)), 0.0F);
            }
        }


//If shouldn't place block, but is falling block
        else if (basisBlock instanceof BlockFalling)
        {
//Run on broken logic
            ((BlockFalling)basisBlock).onBroken(this.world, blockPosAt);
        }
    }




//When this falls
    public void fall(float distance, float damageMultiplier)
    {

    }




//Misc flags and behavior


    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return false;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    /**
     * Return whether this entity should be rendered as on fire.
     */
    public boolean canRenderOnFire()
    {
        return false;
    }

    public boolean ignoreItemEntityData()
    {
        return true;
    }




//Getters


    public BlockPos getOrigin()
    {
        return (BlockPos)this.dataManager.get(ORIGIN);
    }

    @Nullable
    public IBlockState getBlock()
    {
        return this.basisState;
    }




//Setters


    public void setOrigin(BlockPos origin)
    {
        this.dataManager.set(ORIGIN, origin);
    }

    public void setDealsDamage(boolean dealsDamage)
    {
        this.dealsDamage = dealsDamage;
    }

}
