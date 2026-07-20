package org.sporotofpoorety.eternitymode.client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;


import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import org.sporotofpoorety.eternitymode.util.OrthonormalBasis;




//NOTE TO SELF: CALLER IS RESPONSIBLE FOR PRE-BIDNING TEXTURE (BY DEFAULT USE BEACON)
@SideOnly(Side.CLIENT)
public class LaserRenderer 
{

//Vanilla beacon texture
    public static final ResourceLocation LASER_DEFAULT_TEXTURE 
        = TileEntityBeaconRenderer.TEXTURE_BEACON_BEAM;




    public static void renderLaser
    (
//Laser start point
        double startX, double startY, double startZ,
//Laser forward dir
        double forwardX, double forwardY, double forwardZ,
//Laser length
        double laserLen,
//Can push inner forward
        double innerPush,
//Can extend outer
        double outerExtend,
//Radii of the layers
        double innerRadius, double outerRadius,
//Colors
        float red, float green, float blue,
//Outer glow, test with 0.125
        float outerAlpha,
//For rotation and texture scrolling
        long worldTime,
//For smooth anim
        float partialTicks,
//How much of the texture the laser covers 
        double laserCoversHowMuchOfTexture,
//Ticks to fully scroll texture
        double timeToScrollOneTexturesWorth,
//Time to fully rotate inner
        double timeToRotateInner
    ) 
    {

//Get original forward
        Vec3d forwardOriginal = new Vec3d(forwardX, forwardY, forwardZ);
//If vec length, laser length or radius invalid, abort
        if (forwardOriginal.length() < 0.001D || laserLen <= 0.0D || innerRadius <= 0.0D || outerRadius <= 0.0D) { return; }




//Get laser orthobasis
        Vec3d[] orthoLaser = OrthonormalBasis.makeOrthonormalBasis(forwardOriginal);
        Vec3d rightVec = orthoLaser[0];
        Vec3d upwardVec = orthoLaser[1];
        Vec3d forwardVec = orthoLaser[2];




//Get world time including partial ticks
        double worldTimeWithPartial = (double)worldTime + (double)partialTicks;
//Scroll whole texture in a specific amount of time
//Also the scrolling is negative to invert UV's top-bottom into bottom-top
        double textureScrolledHowMuchNeg = (-1.0D) * ((worldTimeWithPartial % timeToScrollOneTexturesWorth) / timeToScrollOneTexturesWorth);
//Slowly rotates the inner cross-section
        double innerRotatedHowMuch = (2.0D * Math.PI) * ((worldTimeWithPartial % timeToRotateInner) / timeToRotateInner);




//135-45-315-225 angles + rotation angle
//These produce a square shaped rotating cross-section...
        double[] innerLaserCornerAnglesCurrent = 
        {
// 0.75PI
            2.356194490192345D + innerRotatedHowMuch,
// 0.25PI  
            0.7853981633974483D + innerRotatedHowMuch,
// 1.75PI
            5.497787143782138D + innerRotatedHowMuch,
// 1.25PI
            3.9269908169872414D + innerRotatedHowMuch
        };




//Inner laser's 
//corner offsets (in absolute coords)
        Vec3d[] innerCornersAbsoluteOffsets = new Vec3d[4];
//For each inner corner
        for (int cornerAt = 0; cornerAt < 4; cornerAt++) 
        {
            innerCornersAbsoluteOffsets[cornerAt] 
//Add ortho-right times angle and radius
                = rightVec.scale(Math.cos(innerLaserCornerAnglesCurrent[cornerAt]) * innerRadius * Math.sqrt(2.0D))
//Add ortho-upward times angle and radius
                .add(upwardVec.scale(Math.sin(innerLaserCornerAnglesCurrent[cornerAt]) * innerRadius * Math.sqrt(2.0D)));
        }




//Outer laser's
//corner orthocoord values
        double[] outerCornersOrthoRightness = {-outerRadius, outerRadius, outerRadius, -outerRadius};
        double[] outerCornersOrthoUpwardness = {outerRadius, outerRadius, -outerRadius, -outerRadius};
//Outer laser's
//corner offsets (in absolute coords)
        Vec3d[] outerCornersAbsoluteOffsets = new Vec3d[4];
//For each outer corner
        for (int cornerAt = 0; cornerAt < 4; cornerAt++) 
        {
            outerCornersAbsoluteOffsets[cornerAt]
//Add ortho-right times value
                = rightVec.scale(outerCornersOrthoRightness[cornerAt])
//Add ortho-upward times value
                .add(upwardVec.scale(outerCornersOrthoUpwardness[cornerAt]));
        }




//Set up necessary OpenGL state
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
        GlStateManager.disableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
//Clamp/repeat texture, wrapping seamlessly
        GlStateManager.glTexParameteri(3553, 10242, 10497);
        GlStateManager.glTexParameteri(3553, 10243, 10497); 




//Get the tessellator and buffer builder
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();




//Opaque inner laser with additive blending and rotation

//Appears behind opaque objects
        GlStateManager.depthMask(true);

//Again using negative V scrolling to go bottom-top and not top-bottom
        double innerBackTextureV = 1.0D + textureScrolledHowMuchNeg;
        double innerFrontTextureV = innerBackTextureV - laserCoversHowMuchOfTexture;
//Texture coverage for inner tips
        double innerTipBottomTextureV = innerBackTextureV;
        double innerTipTopTextureV = innerTipBottomTextureV - (laserCoversHowMuchOfTexture * (innerRadius * 2.0D / laserLen));

//Prepare buffer builder
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);


//Build inner start tip 
        buildQuad
        (
            bufferBuilder,
            new Vec3d(startX, startY, startZ).add(forwardVec.scale(innerPush)), innerRadius * 2.0D, 
            rightVec.scale(Math.cos(0.5D * Math.PI + innerRotatedHowMuch))
                .add(upwardVec.scale(Math.sin(0.5D * Math.PI + innerRotatedHowMuch))), 
            innerCornersAbsoluteOffsets[3], 
            innerCornersAbsoluteOffsets[2],
            innerTipBottomTextureV, innerTipTopTextureV, 
            red, green, blue, 1.0F
        );
//Build inner end tip
        buildQuad
        (
            bufferBuilder,
            new Vec3d(startX, startY, startZ).add(forwardVec.scale(innerPush)), innerRadius * 2.0D, 
            rightVec.scale(Math.cos(0.5D * Math.PI + innerRotatedHowMuch))
                .add(upwardVec.scale(Math.sin(0.5D * Math.PI + innerRotatedHowMuch))),
            innerCornersAbsoluteOffsets[3].add(forwardVec.scale(laserLen)), 
            innerCornersAbsoluteOffsets[2].add(forwardVec.scale(laserLen)),
            innerTipBottomTextureV, innerTipTopTextureV, 
            red, green, blue, 1.0F
        );
//Build inner beam
        for (int sideAt = 0; sideAt < 4; sideAt++) 
        {
            int backLineStartCornerIndex = sideAt;
            int backLineEndCornerIndex = (sideAt + 1) % 4;

            buildQuad
            (
                bufferBuilder,
                new Vec3d(startX, startY, startZ).add(forwardVec.scale(innerPush)), laserLen, 
                forwardVec, 
                innerCornersAbsoluteOffsets[backLineStartCornerIndex], 
                innerCornersAbsoluteOffsets[backLineEndCornerIndex],
                innerBackTextureV, innerFrontTextureV, 
                red, green, blue, 1.0F
            );
        }
//Draw inner laser
        tessellator.draw();




//Thicker, translucent outer laser

//Standard alpha blending
        GlStateManager.tryBlendFuncSeparate
        (
            SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA,
            SourceFactor.ONE, DestFactor.ZERO
        );
//Appears transparently over everything
        GlStateManager.depthMask(false);

//Again using negative V scrolling to go bottom-top and not top-bottom
        double outerBackTextureV = 1.0D + textureScrolledHowMuchNeg;
        double outerFrontTextureV = outerBackTextureV - laserCoversHowMuchOfTexture;
//Texture coverage for outer tips
        double outerTipBottomTextureV = outerBackTextureV;
        double outerTipTopTextureV = outerTipBottomTextureV - (laserCoversHowMuchOfTexture * (outerRadius * 2.0D / laserLen));

//Prepare buffer builder
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);


//Build outer start tip
        buildQuad
        (
            bufferBuilder,
            new Vec3d(startX, startY, startZ), outerRadius * 2.0D,
            upwardVec, 
            outerCornersAbsoluteOffsets[3], 
            outerCornersAbsoluteOffsets[2],
            outerTipBottomTextureV, outerTipTopTextureV,
            red, green, blue, outerAlpha
        );
//Build outer end tip
        buildQuad
        (
            bufferBuilder,
            new Vec3d(startX, startY, startZ), outerRadius * 2.0D, 
            upwardVec, 
            outerCornersAbsoluteOffsets[3].add(forwardVec.scale(laserLen + outerExtend)), 
            outerCornersAbsoluteOffsets[2].add(forwardVec.scale(laserLen + outerExtend)),
            outerTipBottomTextureV, outerTipTopTextureV,
            red, green, blue, outerAlpha
        );            
//Build outer beam
        for (int sideAt = 0; sideAt < 4; sideAt++) 
        {
            int backLineStartCornerIndex = sideAt;
            int backLineEndCornerIndex = (sideAt + 1) % 4;

            buildQuad
            (
                bufferBuilder,
                new Vec3d(startX, startY, startZ), laserLen + outerExtend, 
                forwardVec,
                outerCornersAbsoluteOffsets[backLineStartCornerIndex], 
                outerCornersAbsoluteOffsets[backLineEndCornerIndex],
                outerBackTextureV, outerFrontTextureV,
                red, green, blue, outerAlpha
            );
        }
//Draw outer laser
        tessellator.draw();




//And finally clean up the OpenGL state, whew
        GlStateManager.enableNormalize();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }




//Build a quad for one side of a laser
    protected static void buildQuad
    (
//Buffer builder
        BufferBuilder bufferBuilder,
//Laser start, length and forward
        Vec3d startVec, double laserLen, 
//Laser forward vec
        Vec3d forwardVec,
//Back-line start and end absolute offsets
        Vec3d backLineStartAbsoluteOffset,
        Vec3d backLineEndAbsoluteOffset,
//Current texture V for back and front (HOLLOW PURPLE?)
        double backTextureV, double frontTextureV, 
//Colors and alpha
        float red, float green, float blue, float alpha
    ) 
    {

//Get world coords of back-line-start quad corner 
        Vec3d backLineStartAt = startVec.add(backLineStartAbsoluteOffset);

//Get world coords of back-line-end quad corner 
        Vec3d backLineEndAt = startVec.add(backLineEndAbsoluteOffset);

//Go forward to front-line-start corner
        Vec3d frontLineStartAt = backLineStartAt.add(forwardVec.scale(laserLen));

//Go forward to front-line-end corner
        Vec3d frontLineEndAt = backLineEndAt.add(forwardVec.scale(laserLen));




//Vertex order here is counterclockwise 
//Front-line-start
        bufferBuilder.pos(frontLineStartAt.x, frontLineStartAt.y, frontLineStartAt.z).tex(0.0, frontTextureV).color(red, green, blue, alpha).endVertex();
//Back-line-start
        bufferBuilder.pos(backLineStartAt.x, backLineStartAt.y, backLineStartAt.z).tex(0.0, backTextureV).color(red, green, blue, alpha).endVertex();
//Back-line-end
        bufferBuilder.pos(backLineEndAt.x, backLineEndAt.y, backLineEndAt.z).tex(1.0, backTextureV).color(red, green, blue, alpha).endVertex();
//Front-line-end
        bufferBuilder.pos(frontLineEndAt.x, frontLineEndAt.y, frontLineEndAt.z).tex(1.0, frontTextureV).color(red, green, blue, alpha).endVertex();

    }

}
