package org.sporotofpoorety.eternitymode.client.objmodel;


import net.minecraft.util.ResourceLocation;




public interface IModelCustomLoader 
{
   String getType();

   String[] getSuffixes();

   IModelCustom loadInstance(ResourceLocation var1) throws ModelFormatException;
}
