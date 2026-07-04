package org.sporotofpoorety.eternitymode.client.render;


import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import org.sporotofpoorety.eternitymode.Tags;
import org.sporotofpoorety.eternitymode.client.objmodel.AdvancedModelLoader;
import org.sporotofpoorety.eternitymode.client.objmodel.IModelCustom;
import org.sporotofpoorety.eternitymode.entity.EntityDemonScythe;




@SideOnly(Side.CLIENT)
public class RenderDemonScythe extends Render<EntityDemonScythe>
{
    private static final ResourceLocation textureEmpty = new ResourceLocation("eternitymode:textures/entity/empty.png");
    private static final IModelCustom mainObj = AdvancedModelLoader.loadModel(new ResourceLocation("eternitymode:textures/entity/demonscythe.obj"));

    public RenderDemonScythe(RenderManager renderManager) 
    {
        super(renderManager);
    }

    public void doRender(EntityDemonScythe entity, double x, double y, double z, float entityYaw, float partialTicks) 
    {
        this.bindEntityTexture(entity);
        GlStateManager.pushMatrix();
        GlStateManager.color(0.8F, 0.0F, 0.8F);
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks + 180.0F, 1.0F, 0.0F, 0.0F);
        float t = (float)entity.ticksExisted;
        GlStateManager.rotate(t * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(Math.sin((double)t) * (double)5.0F), 0.0F, 0.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        float lastx = OpenGlHelper.lastBrightnessX;
        float lasty = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GL11.glBlendFunc(770, 1);
        mainObj.renderPart("core");
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastx, lasty);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.enableLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    protected ResourceLocation getEntityTexture(EntityDemonScythe entity) 
    {
        return textureEmpty;
    }
}
