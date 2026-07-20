package org.sporotofpoorety.eternitymode.entity;


import com.google.common.base.Optional;
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
import org.sporotofpoorety.eternitymode.util.BlockUtil;




public class EntityThrownBlock extends EntityWithOwner
{

    protected static final DataParameter<BlockPos> ORIGIN = EntityDataManager.<BlockPos>createKey(EntityThrownBlock.class, DataSerializers.BLOCK_POS);
    protected static final DataParameter<Optional<IBlockState>> BASIS_STATE 
        = EntityDataManager.<Optional<IBlockState>>createKey(EntityThrownBlock.class, DataSerializers.OPTIONAL_BLOCK_STATE);

//  public IBlockState basisState;


    public NBTTagCompound tileEntityData;


    public boolean dontPlaceBlock;
    public boolean shouldDropItem;


    public boolean dealsDamage;
    public float thrownBlockDamage;


    public boolean hasManualOrigin;
    public boolean dontBreakInitialPos;




    public EntityThrownBlock(World worldIn)
    {
        super(worldIn);
        this.setSize(0.98F, 0.98F);

//      this.basisState = Blocks.STONE.getDefaultState();

        this.lifetimeMax = 600;
    }

    public EntityThrownBlock(World worldIn, double x, double y, double z, 
    EntityLivingBase owner, IBlockState fallingBlockState, 
    boolean dontPlaceBlock, boolean shouldDropItem, boolean dealsDamage, float thrownBlockDamage)
    {
        super(worldIn, x, y, z, owner);
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
//Default lifetime
        this.lifetimeMax = 600;


//      this.basisState = fallingBlockState;
        this.setBasisState(fallingBlockState);


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
        this.dataManager.register(BASIS_STATE, Optional.absent());
    }


    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);


//Save origin pos
        compound.setInteger("OriginPosX", this.getOrigin().getX());
        compound.setInteger("OriginPosY", this.getOrigin().getY());
        compound.setInteger("OriginPosZ", this.getOrigin().getZ());


//Get basis block (air as failsafe)
//      Block basisBlock = this.basisState == null ? Blocks.AIR : this.basisState.getBlock();
        Block basisBlock = this.getBasisState() == null ? Blocks.AIR : this.getBasisState().getBlock();
//Set block metadata
        compound.setByte("Data", (byte)basisBlock.getMetaFromState(this.getBasisState()));

//Get basis block name
        ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(basisBlock);
//Save basis block name
        compound.setString("Block", resourcelocation == null ? "" : resourcelocation.toString());


//Save tile entity data
        if (this.tileEntityData != null) { compound.setTag("TileEntityData", this.tileEntityData); }


        compound.setBoolean("DontPlaceBlock", this.dontPlaceBlock);
        compound.setBoolean("DropItem", this.shouldDropItem);


        compound.setBoolean("DealsDamage", this.dealsDamage);
        compound.setFloat("ThrownBlockDamage", this.thrownBlockDamage);


        compound.setBoolean("HasManualOrigin", this.hasManualOrigin);
        compound.setBoolean("DontBreakInitialPos", this.dontBreakInitialPos);
    }


    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);


//Restore origin pos
        if (compound.hasKey("OriginPosX")) 
        { 
            this.setOrigin(new BlockPos(compound.getInteger("OriginPosX"), compound.getInteger("OriginPosY"), compound.getInteger("OriginPosZ")));
        }


//Get block metadata
        int i = compound.getByte("Data") & 255;


//Get block basis from name, 
//then get blockstate from metadata
        if (compound.hasKey("Block", 8)) 
//          { this.basisState = Block.getBlockFromName(compound.getString("Block")).getStateFromMeta(i); }
            { this.setBasisState(Block.getBlockFromName(compound.getString("Block")).getStateFromMeta(i)); }


//Get tile entity data
        if (compound.hasKey("TileEntityData")) { this.tileEntityData = compound.getCompoundTag("TileEntityData"); }


        if (compound.hasKey("DontPlaceBlock")) { this.dontPlaceBlock = compound.getBoolean("DontPlaceBlock"); }
        if (compound.hasKey("DropItem")) { this.shouldDropItem = compound.getBoolean("DropItem"); }


        if (compound.hasKey("DealsDamage")) { this.dealsDamage = compound.getBoolean("DealsDamage"); }
        if (compound.hasKey("ThrownBlockDamage")) { this.thrownBlockDamage = compound.getFloat("ThrownBlockDamage"); }


        if (compound.hasKey("HasManualOrigin")) { this.hasManualOrigin = compound.getBoolean("HasManualOrigin"); }
        if (compound.hasKey("DontBreakInitialPos")) { this.dontBreakInitialPos = compound.getBoolean("DontBreakInitialPos"); }
    }




    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
//Get basis block
        Block basisBlock = this.getBasisState() == null ? Blocks.AIR : this.getBasisState().getBlock();


//If state is invalid, set dead
        if (this.getBasisState() == null || this.getBasisState().getMaterial() == Material.AIR)
        {
            this.setDead();
            return;
        }


//Basic owned entity update
        super.onUpdate();




//If first spawned
        if (this.realTicksExisted == 1 && !this.world.isRemote)
        {
//Get blockpos this is at
            BlockPos blockPosAt = new BlockPos(this);


//If not manual origin
            if(!this.hasManualOrigin)
            {
//And initial blockpos 
//doesn't correspond to saved block
                if (this.world.getBlockState(blockPosAt).getBlock() != basisBlock)
                {
//Kill this entity, as it's invalid
                    this.setDead(); return;
                }
            }

//If should destroy initial pos
            if(!this.dontBreakInitialPos)
            {
//Break that blockpos
//              this.world.setBlockToAir(blockPosAt);
                BlockUtil.destroyBlockPos(blockPosAt, this.world, 999999999.9F, false, false, 69420); 
            }
        }



        if(!this.world.isRemote)
        {
//If should do damage
            if (this.dealsDamage)
            {
                this.dealDamage();
            }

//Basic movement
            if(!this.dontMove) { this.performBasicMovement(); }
        }




//Despawn/place logic
        if (!this.world.isRemote)
        {
//If this is on ground and is solid
            if (this.onGround && !this.noClip)
            {
//Hit ground logic
                this.onHitGround();
            }


//If this not on ground
            else
            {
//But fell out of bounds
                BlockPos blockPosAt = new BlockPos(this);
                if ((blockPosAt.getY() < -128))
                {
//Then set dead
                    this.setDead();
                }
            }


//Failsafe ground hit or inside block
            if(this.realTicksExisted % 20 == 0)
            {
//Get blockstate of current pos and pos under
                IBlockState stateOfPos = this.world.getBlockState(new BlockPos((int) this.posX, (int) this.posY, (int) this.posZ));
                IBlockState stateOfUnder = this.world.getBlockState(new BlockPos((int) this.posX, (int) (this.posY - 1.0D), (int) this.posZ));

//If there is a solid block
//at or under this, and this is set to fall
                if((stateOfPos.getMaterial().isSolid() || stateOfUnder.getMaterial().isSolid()) 
                && !this.noClip && this.motionY < 0.0D)
                {
//Hit ground logic

                    this.onHitGround();
                }
            }
        }

    }


    public void dealDamage()
    {
//Get entities within AABB
        List<Entity> list = Lists.newArrayList(this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(0.22D)));

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


//On lifetime expire
    public void onLifetimeExpire()
    {
//Drop item if should
        Block basisBlock = this.getBasisState().getBlock();

        if(basisBlock != null)
        {
            if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops")
//If this is not an unbreakable block
            && this.getBasisState().getBlockHardness(this.world, this.getOrigin()) > 0.0F 
            && basisBlock != Blocks.BEDROCK && basisBlock != Blocks.END_PORTAL_FRAME)
            {
                this.entityDropItem(new ItemStack(basisBlock, 1, basisBlock.damageDropped(this.getBasisState())), 0.0F);
            }
        }

        this.setDead();
    }




    public void onHitGround()
    {
        Block basisBlock = this.getBasisState().getBlock();
        BlockPos blockPosAt = new BlockPos(this);
        IBlockState blockStateAt = this.world.getBlockState(blockPosAt);

        boolean isPowder = this.getBasisState().getBlock() == Blocks.CONCRETE_POWDER;
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
        if (!this.dontPlaceBlock
//Check hardness and block type
        && this.getBasisState().getBlockHardness(this.world, this.getOrigin()) > 0.0F 
        && basisBlock != Blocks.BEDROCK && basisBlock != Blocks.END_PORTAL_FRAME)
        {

//If allowed to place at pos
            if (this.world.mayPlace(basisBlock, blockPosAt, true, EnumFacing.UP, (Entity)null)
//And block can't keep falling
            && (!BlockFalling.canFallThrough(this.world.getBlockState(blockPosAt.down())) || isPowderInWater)
//Place saved block state 
            && this.world.setBlockState(blockPosAt, this.getBasisState(), 3))
            {

//Run custom logic for
//blocks like sand and gravel
                if (basisBlock instanceof BlockFalling)
                {
                    ((BlockFalling)basisBlock).onEndFalling(this.world, blockPosAt, this.getBasisState(), blockStateAt);
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
            else if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops")
//As long as this is not an unbreakable block
            && this.getBasisState().getBlockHardness(this.world, this.getOrigin()) > 0.0F 
            && basisBlock != Blocks.BEDROCK && basisBlock != Blocks.END_PORTAL_FRAME)
            {
                this.entityDropItem(new ItemStack(basisBlock, 1, basisBlock.damageDropped(this.getBasisState())), 0.0F);
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
    public IBlockState getBasisState()
    {
        return (IBlockState)((Optional)this.dataManager.get(BASIS_STATE)).orNull();
    }




//Setters


    public void setOrigin(BlockPos origin)
    {
        this.dataManager.set(ORIGIN, origin);
    }

    public void setBasisState(@Nullable IBlockState state)
    {
        this.dataManager.set(BASIS_STATE, Optional.fromNullable(state));
    }

}
