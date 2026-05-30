package org.sporotofpoorety.eternitymode.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import electroblob.wizardry.Wizardry;

import org.sporotofpoorety.eternitymode.entity.EntityMeteorBlock;




@SideOnly(Side.CLIENT)
public class RenderMeteorBlock extends Render<EntityMeteorBlock> 
{

    private static final ResourceLocation METEOR_TEXTURE = new ResourceLocation(Wizardry.MODID, "textures/blocks/meteor.png");


    public RenderMeteorBlock(RenderManager renderManagerIn) 
    {
        super(renderManagerIn);
        this.shadowSize = 2.0F; 
    }


    @Override
    public void doRender(EntityMeteorBlock entity, double x, double y, double z, float entityYaw, float partialTicks) 
    {
        this.bindTexture(METEOR_TEXTURE);
        

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting(); 
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        

        GlStateManager.translate((float)x, (float)y, (float)z);


        float scale = 8.0F;
        GlStateManager.translate(0.0F, scale / 2.0F, 0.0F); 
        GlStateManager.scale(scale, scale, scale);


        GlStateManager.disableCull();
        this.drawCube();
        GlStateManager.enableCull();


        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }


/*
    private void drawCube() 
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);


        float uMin = 0.0F;
        float uMax = 1.0F;
        float vMin = 0.0F;
        float vMax = 1.0F;


        float h = 0.5F;


        bufferbuilder.pos(-h, -h,  h).tex(uMin, vMax).endVertex();
        bufferbuilder.pos( h, -h,  h).tex(uMax, vMax).endVertex();
        bufferbuilder.pos( h,  h,  h).tex(uMax, vMin).endVertex();
        bufferbuilder.pos(-h,  h,  h).tex(uMin, vMin).endVertex();


        bufferbuilder.pos( h, -h, -h).tex(uMin, vMax).endVertex();
        bufferbuilder.pos(-h, -h, -h).tex(uMax, vMax).endVertex();
        bufferbuilder.pos(-h,  h, -h).tex(uMax, vMin).endVertex();
        bufferbuilder.pos( h,  h, -h).tex(uMin, vMin).endVertex();


        bufferbuilder.pos(-h,  h, -h).tex(uMin, vMax).endVertex();
        bufferbuilder.pos( h,  h, -h).tex(uMax, vMax).endVertex();
        bufferbuilder.pos( h,  h,  h).tex(uMax, vMin).endVertex();
        bufferbuilder.pos(-h,  h,  h).tex(uMin, vMin).endVertex();


        bufferbuilder.pos(-h, -h,  h).tex(uMin, vMax).endVertex();
        bufferbuilder.pos( h, -h,  h).tex(uMax, vMax).endVertex();
        bufferbuilder.pos( h, -h, -h).tex(uMax, vMin).endVertex();
        bufferbuilder.pos(-h, -h, -h).tex(uMin, vMin).endVertex();


        bufferbuilder.pos( h, -h,  h).tex(uMin, vMax).endVertex();
        bufferbuilder.pos( h, -h, -h).tex(uMax, vMax).endVertex();
        bufferbuilder.pos( h,  h, -h).tex(uMax, vMin).endVertex();
        bufferbuilder.pos( h,  h,  h).tex(uMin, vMin).endVertex();


        bufferbuilder.pos(-h, -h, -h).tex(uMin, vMax).endVertex();
        bufferbuilder.pos(-h, -h,  h).tex(uMax, vMax).endVertex();
        bufferbuilder.pos(-h,  h,  h).tex(uMax, vMin).endVertex();
        bufferbuilder.pos(-h,  h, -h).tex(uMin, vMin).endVertex();

        tessellator.draw();
    }
*/


    private void drawCube() 
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

// Full texture UVs
        float uMin = 0.0F;
        float uMax = 1.0F;
        float vMin = 0.0F;
        float vMax = 1.0F;

//Half size
        float h = 0.5F;

//FRONT FACE (Z+)
        bufferbuilder.pos(-h, -h,  h).tex(uMin, vMax).endVertex();
        bufferbuilder.pos( h, -h,  h).tex(uMax, vMax).endVertex();
        bufferbuilder.pos( h,  h,  h).tex(uMax, vMin).endVertex();
        bufferbuilder.pos(-h,  h,  h).tex(uMin, vMin).endVertex();

//BACK FACE (Z-)
        bufferbuilder.pos( h, -h, -h).tex(uMin, vMax).endVertex();
        bufferbuilder.pos(-h, -h, -h).tex(uMax, vMax).endVertex();
        bufferbuilder.pos(-h,  h, -h).tex(uMax, vMin).endVertex();
        bufferbuilder.pos( h,  h, -h).tex(uMin, vMin).endVertex();

//TOP FACE (Y+)
// Note: Z goes from -h (back) to h (front)
        bufferbuilder.pos(-h,  h, -h).tex(uMin, vMax).endVertex();
        bufferbuilder.pos( h,  h, -h).tex(uMax, vMax).endVertex();
        bufferbuilder.pos( h,  h,  h).tex(uMax, vMin).endVertex();
        bufferbuilder.pos(-h,  h,  h).tex(uMin, vMin).endVertex();

//BOTTOM FACE (Y-)
//Note: Z goes from h (front) to -h (back)
        bufferbuilder.pos(-h, -h,  h).tex(uMin, vMax).endVertex();
        bufferbuilder.pos( h, -h,  h).tex(uMax, vMax).endVertex();
        bufferbuilder.pos( h, -h, -h).tex(uMax, vMin).endVertex();
        bufferbuilder.pos(-h, -h, -h).tex(uMin, vMin).endVertex();

//RIGHT FACE (X+)
        bufferbuilder.pos( h, -h, -h).tex(uMin, vMax).endVertex();
        bufferbuilder.pos( h,  h, -h).tex(uMax, vMax).endVertex();
        bufferbuilder.pos( h,  h,  h).tex(uMax, vMin).endVertex();
        bufferbuilder.pos( h, -h,  h).tex(uMin, vMin).endVertex();

//LEFT FACE (X-)
        bufferbuilder.pos(-h, -h,  h).tex(uMin, vMax).endVertex();
        bufferbuilder.pos(-h,  h,  h).tex(uMax, vMax).endVertex();
        bufferbuilder.pos(-h,  h, -h).tex(uMax, vMin).endVertex();
        bufferbuilder.pos(-h, -h, -h).tex(uMin, vMin).endVertex();

        tessellator.draw();
    }


/*
    private void drawCube() 
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

// Full texture UVs
        float uMin = 0.0F;
        float uMax = 1.0F;
        float vMin = 0.0F;
        float vMax = 1.0F;


//FRONT FACE (Z+)
        bufferbuilder.pos(0.0F, 0.0F,  1.0F).tex(uMin, vMax).endVertex();
        bufferbuilder.pos( 1.0F, 0.0F,  1.0F).tex(uMax, vMax).endVertex();
        bufferbuilder.pos( 1.0F,  1.0F,  1.0F).tex(uMax, vMin).endVertex();
        bufferbuilder.pos(0.0F,  1.0F,  1.0F).tex(uMin, vMin).endVertex();

//BACK FACE (Z-)
        bufferbuilder.pos( 1.0F, 0.0F, 0.0F).tex(uMin, vMax).endVertex();
        bufferbuilder.pos(0.0F, 0.0F, 0.0F).tex(uMax, vMax).endVertex();
        bufferbuilder.pos(0.0F,  1.0F, 0.0F).tex(uMax, vMin).endVertex();
        bufferbuilder.pos( 1.0F,  1.0F, 0.0F).tex(uMin, vMin).endVertex();

//TOP FACE (Y+)
// Note: Z goes from 0.0F (back) to 1.0F (front)
        bufferbuilder.pos(0.0F,  1.0F, 0.0F).tex(uMin, vMax).endVertex();
        bufferbuilder.pos( 1.0F,  1.0F, 0.0F).tex(uMax, vMax).endVertex();
        bufferbuilder.pos( 1.0F,  1.0F,  1.0F).tex(uMax, vMin).endVertex();
        bufferbuilder.pos(0.0F,  1.0F,  1.0F).tex(uMin, vMin).endVertex();

//BOTTOM FACE (Y-)
//Note: Z goes from 1.0F (front) to 0.0F (back)
        bufferbuilder.pos(0.0F, 0.0F,  1.0F).tex(uMin, vMax).endVertex();
        bufferbuilder.pos( 1.0F, 0.0F,  1.0F).tex(uMax, vMax).endVertex();
        bufferbuilder.pos( 1.0F, 0.0F, 0.0F).tex(uMax, vMin).endVertex();
        bufferbuilder.pos(0.0F, 0.0F, 0.0F).tex(uMin, vMin).endVertex();

//RIG1.0FT FACE (X+)
        bufferbuilder.pos( 1.0F, 0.0F, 0.0F).tex(uMin, vMax).endVertex();
        bufferbuilder.pos( 1.0F,  1.0F, 0.0F).tex(uMax, vMax).endVertex();
        bufferbuilder.pos( 1.0F,  1.0F,  1.0F).tex(uMax, vMin).endVertex();
        bufferbuilder.pos( 1.0F, 0.0F,  1.0F).tex(uMin, vMin).endVertex();

//LEFT FACE (X-)
        bufferbuilder.pos(0.0F, 0.0F,  1.0F).tex(uMin, vMax).endVertex();
        bufferbuilder.pos(0.0F,  1.0F,  1.0F).tex(uMax, vMax).endVertex();
        bufferbuilder.pos(0.0F,  1.0F, 0.0F).tex(uMax, vMin).endVertex();
        bufferbuilder.pos(0.0F, 0.0F, 0.0F).tex(uMin, vMin).endVertex();

        tessellator.draw();
    }
*/


    @Override
    protected ResourceLocation getEntityTexture(EntityMeteorBlock entity) 
    {
        return METEOR_TEXTURE;
    }
}
