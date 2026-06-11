package org.sporotofpoorety.eternitymode.util;


import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import org.sporotofpoorety.eternitymode.entity.EntityWithOwner;




public final class PuppetNBT 
{

//Write puppet entities to NBT
    public static void nbtWritePuppetList(EntityWithOwner masterEntity, NBTTagCompound compound)
    {
//Check for type of puppets in list
        if(masterEntity.puppetsStoredType.equals("block"))
        {
            nbtWritePuppetListBlock(masterEntity, compound);
        }
        else
        {
            nbtWritePuppetListDefault(masterEntity, compound);
        }
    }


//Read puppet list from NBT
    public static void nbtReadPuppetList(EntityWithOwner masterEntity, NBTTagCompound compound)
    {
//First clear puppet list
        masterEntity.puppetEntities.clear();

//Check for type of puppets in list
        if (compound.hasKey("PuppetEntityArray")) 
        {
            if(masterEntity.puppetsStoredType.equals("block"))
            {
                nbtReadPuppetListBlock(masterEntity, compound);
            }
            else
            {
                nbtReadPuppetListDefault(masterEntity, compound);
            }
        }
    }




//Write default puppet entities to NBT
    public static void nbtWritePuppetListDefault(EntityWithOwner masterEntity, NBTTagCompound compound)
    {
//Puppet array to store
        NBTTagList puppetListToStore = new NBTTagList();

//For each puppet entity  
        for (PuppetEntity puppetEntity : masterEntity.puppetEntities) 
        {
//Make puppet map
            NBTTagCompound puppetToStore = new NBTTagCompound();
                puppetToStore.setDouble("PuppetOffsetX", puppetEntity.offsetX);
                puppetToStore.setDouble("PuppetOffsetY", puppetEntity.offsetY);
                puppetToStore.setDouble("PuppetOffsetZ", puppetEntity.offsetZ);
                puppetToStore.setInteger("PuppetTime", puppetEntity.controlTime);
                puppetToStore.setInteger("PuppetState", puppetEntity.controlState);
                if(puppetEntity.storedVec != null) 
                {
                    puppetToStore.setDouble("PuppetVecX", puppetEntity.storedVec.x);
                    puppetToStore.setDouble("PuppetVecY", puppetEntity.storedVec.y);
                    puppetToStore.setDouble("PuppetVecZ", puppetEntity.storedVec.z);
                }
                puppetToStore.setDouble("PuppetDistance", puppetEntity.storedDistance);
//Append it to puppet array
            puppetListToStore.appendTag(puppetToStore);
        }
        
        compound.setTag("PuppetEntityArray", puppetListToStore);   
    }


//Read default puppet list from NBT
    public static void nbtReadPuppetListDefault(EntityWithOwner masterEntity, NBTTagCompound compound)
    {
//It's a list of maps specifically
        NBTTagList storedPuppetList = compound.getTagList("PuppetEntityArray", 10);
        
//For each stored puppet
        for (int i = 0; i < storedPuppetList.tagCount(); i++) 
        {
//Fetch it as compound
            NBTTagCompound storedPuppet = storedPuppetList.getCompoundTagAt(i);

//Make corresponding puppet entity (but null)
            PuppetEntity puppet = new PuppetEntity
            (
                null,
                storedPuppet.getDouble("PuppetOffsetX"),
                storedPuppet.getDouble("PuppetOffsetY"),
                storedPuppet.getDouble("PuppetOffsetZ"),
                storedPuppet.getInteger("PuppetTime"),
                storedPuppet.getInteger("PuppetState")
            );
            if(storedPuppet.hasKey("PuppetVecX")) 
            { 
                puppet.storedVec 
                    = new Vec3d(storedPuppet.getDouble("PuppetVecX"), storedPuppet.getDouble("PuppetVecY"), storedPuppet.getDouble("PuppetVecZ"));
            }
            if(storedPuppet.hasKey("PuppetDistance")) { puppet.storedDistance = storedPuppet.getDouble("PuppetDistance"); }

//Store in the puppet list
            masterEntity.puppetEntities.add(puppet);
        }
    }






//Write block puppet entities to NBT
    public static void nbtWritePuppetListBlock(EntityWithOwner masterEntity, NBTTagCompound compound)
    {
//Puppet block array to store
        NBTTagList puppetBlockListToStore = new NBTTagList();

//For each puppet block entity  
        for (PuppetEntity puppetEntity : masterEntity.puppetEntities) 
        {
            PuppetBlock puppetBlock = (PuppetBlock) puppetEntity;

//Make puppet block map
            NBTTagCompound puppetBlockToStore = new NBTTagCompound();
                puppetBlockToStore.setDouble("PuppetOffsetX", puppetBlock.offsetX);
                puppetBlockToStore.setDouble("PuppetOffsetY", puppetBlock.offsetY);
                puppetBlockToStore.setDouble("PuppetOffsetZ", puppetBlock.offsetZ);
                puppetBlockToStore.setInteger("PuppetTime", puppetBlock.controlTime);
                puppetBlockToStore.setInteger("PuppetState", puppetBlock.controlState);

                puppetBlockToStore.setInteger("OriginPosX", puppetBlock.blockData.blockOrigin.getX());
                puppetBlockToStore.setInteger("OriginPosY", puppetBlock.blockData.blockOrigin.getY());
                puppetBlockToStore.setInteger("OriginPosZ", puppetBlock.blockData.blockOrigin.getZ());

                Block basisBlock = puppetBlock.blockData.basisState == null ? Blocks.AIR : puppetBlock.blockData.basisState.getBlock();
                puppetBlockToStore.setByte("Data", (byte)basisBlock.getMetaFromState(puppetBlock.blockData.basisState));
                ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(basisBlock);
                puppetBlockToStore.setString("Block", resourcelocation == null ? "" : resourcelocation.toString());

                puppetBlockToStore.setBoolean("DontPlaceBlock", puppetBlock.blockData.dontPlaceBlock); 
                puppetBlockToStore.setBoolean("ShouldDropItem", puppetBlock.blockData.shouldDropItem); 
                puppetBlockToStore.setBoolean("DealsDamage", puppetBlock.blockData.dealsDamage); 
                puppetBlockToStore.setFloat("ThrownBlockDamage", puppetBlock.blockData.thrownBlockDamage); 
                puppetBlockToStore.setBoolean("IsSolid", puppetBlock.blockData.isSolid); 

                if(puppetBlock.storedVec != null) 
                {
                    puppetBlockToStore.setDouble("PuppetVecX", puppetBlock.storedVec.x);
                    puppetBlockToStore.setDouble("PuppetVecY", puppetBlock.storedVec.y);
                    puppetBlockToStore.setDouble("PuppetVecZ", puppetBlock.storedVec.z);
                }
                puppetBlockToStore.setDouble("PuppetDistance", puppetBlock.storedDistance);


//Append it to puppet block array
            puppetBlockListToStore.appendTag(puppetBlockToStore);
        }
        

        compound.setTag("PuppetEntityArray", puppetBlockListToStore);   
    }


//Read block puppet list from NBT
    public static void nbtReadPuppetListBlock(EntityWithOwner masterEntity, NBTTagCompound compound)
    {
//It's a list of maps specifically
        NBTTagList storedPuppetBlockList = compound.getTagList("PuppetEntityArray", 10);
        
//For each stored puppet block
        for (int i = 0; i < storedPuppetBlockList.tagCount(); i++) 
        {
//Fetch it as compound
            NBTTagCompound storedPuppetBlock = storedPuppetBlockList.getCompoundTagAt(i);


//Recreate stored IBlockState 
//from metadata and resource location (please work)
            int storedMeta = storedPuppetBlock.getByte("Data") & 255;
            IBlockState storedState = Block.getBlockFromName(storedPuppetBlock.getString("Block")).getStateFromMeta(storedMeta); 

//Make corresponding puppet block (but null)
            PuppetBlock puppetBlock = new PuppetBlock
            (
                null,
                storedPuppetBlock.getDouble("PuppetOffsetX"),
                storedPuppetBlock.getDouble("PuppetOffsetY"),
                storedPuppetBlock.getDouble("PuppetOffsetZ"),
                storedPuppetBlock.getInteger("PuppetTime"),
                storedPuppetBlock.getInteger("PuppetState"),

                new BlockPos(storedPuppetBlock.getInteger("OriginPosX"), storedPuppetBlock.getInteger("OriginPosY"), storedPuppetBlock.getInteger("OriginPosZ")),

                storedState,

                storedPuppetBlock.getBoolean("DontPlaceBlock"),
                storedPuppetBlock.getBoolean("ShouldDropItem"),
                storedPuppetBlock.getBoolean("DealsDamage"),
                storedPuppetBlock.getFloat("ThrownBlockDamage"),
                storedPuppetBlock.getBoolean("IsSolid")
            );

            if(storedPuppetBlock.hasKey("PuppetVecX")) 
            { 
                puppetBlock.storedVec 
                    = new Vec3d(storedPuppetBlock.getDouble("PuppetVecX"), storedPuppetBlock.getDouble("PuppetVecY"), storedPuppetBlock.getDouble("PuppetVecZ"));
            }
            if(storedPuppetBlock.hasKey("PuppetDistance")) { puppetBlock.storedDistance = storedPuppetBlock.getDouble("PuppetDistance"); }


//Store in the puppet block list
            masterEntity.puppetEntities.add(puppetBlock);
        }
    }
}


