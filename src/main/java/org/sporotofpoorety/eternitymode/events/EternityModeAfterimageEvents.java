package org.sporotofpoorety.eternitymode.events;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays; 
import java.util.List;
import java.util.WeakHashMap;


import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;


import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.sporotofpoorety.eternitymode.interfacemixins.IMixinEntityLivingBase;




@SideOnly(Side.CLIENT)
public class EternityModeAfterimageEvents 
{

//Entity can be garbage collected
    public static final WeakHashMap<EntityLivingBase, Vec3d> EACH_LATEST_VECTOR = new WeakHashMap<>();
    public static final List<Afterimage> AFTER_IMAGES = new ArrayList<>();




//Manage entity afterimages and latest positions
    @SubscribeEvent
    public void onEntityUpdate(LivingUpdateEvent event) 
    {
        EntityLivingBase entity = event.getEntityLiving();


//Check if client side, and if mob should have afterimages
        if (entity.world.isRemote && (((IMixinEntityLivingBase) entity).getHasAfterimages()))
        {  
//And if entity is moving and visible
            if ((entity.motionX != 0 || entity.motionY != 0 || entity.motionZ != 0) && !entity.isInvisible()) 
            {
//Get entity position
                Vec3d currPos = entity.getPositionVector();


//If no vector stored for entity
                if (!EACH_LATEST_VECTOR.containsKey(entity)) 
                {
//Store its current position
                    EACH_LATEST_VECTOR.put(entity, currPos);
                }


//Get entity's stored vector
                Vec3d storedVec = EACH_LATEST_VECTOR.get(entity);
//If entity is now far enough from that vector
                if (entity.getDistance(storedVec.x, storedVec.y, storedVec.z) > Math.max(2.5D, entity.width / 2.0D)) 
                {
//Replace it with current pos
                    EACH_LATEST_VECTOR.put(entity, currPos);
//And make an afterimage at current pos        
                    AFTER_IMAGES.add(new Afterimage(entity, currPos));
                }
            }
        }
    }




//Actual afterimage render
    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) 
    {
//Remove afterimages that expired or have no entity
        AFTER_IMAGES.removeIf(img -> (img.lifeTime++ > img.maxLifeTime || img.entityRef.get() == null));

//Skip if afterimages empty
        if (AFTER_IMAGES.isEmpty()) { return; }


//Get client, push matrix, push attribute
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
//Depth mask, minimum alpha needed, no depth, yes blend
        GlStateManager.depthMask(false);
        GlStateManager.alphaFunc(516, 0.003921569f);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
//Blend function for afterimage
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA); 
        



//Renderer starts at player coords
//So get player
        EntityPlayerSP player = mc.player;
//Get player pos
        double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * 1F;
        double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * 1F;
        double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * 1F;

//Correct renderer to world coords
        GlStateManager.translate(-dx, -dy, -dz);




//Render afterimages, backwards iteration is safer
        for (int i = AFTER_IMAGES.size() - 1; i >= 0; i--) 
        {
            Afterimage img = AFTER_IMAGES.get(i);
            img.render(mc.getRenderManager());
        }


//Back to normal
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }




//Afterimage data structure
    public static class Afterimage 
    {

        public final WeakReference<EntityLivingBase> entityRef;
        public final Vec3d positionVec;
        public int lifeTime = 0;
        public final int maxLifeTime = 25;


        public Afterimage(EntityLivingBase entity, Vec3d pos) 
        {
            this.entityRef = new WeakReference<>(entity);
            this.positionVec = pos;
        }


//Render the afterimage
        public void render(RenderManager manager) 
        {
            EntityLivingBase entity = entityRef.get();
            if (entity == null) { return; }

//Lighting Setup
            int brightness = entity.getBrightnessForRender();
            if (entity.isBurning()) { brightness = 15728880; }
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(brightness % 65536), (float)(brightness / 65536));

//How much of the afterimage lifetime has passed
            float expirePercent = (float) lifeTime / (float) maxLifeTime;
//It will gradually become more transparent
            float alpha = 0.25F * (1F - expirePercent);

            GlStateManager.pushMatrix();
            GlStateManager.translate(positionVec.x, positionVec.y, positionVec.z);
            GlStateManager.color(0.25F, 0.25F, 0.25F, alpha);
            
//"false" partialTicks to prevent interpolation jitter
            double dx = positionVec.x - entity.posX;
            double dy = positionVec.y - entity.posY;
            double dz = positionVec.z - entity.posZ;
            manager.renderEntity(entity, dx, dy, dz, entity.rotationYaw, 0F, false); 
            
            GlStateManager.popMatrix();
        }

    }

}
