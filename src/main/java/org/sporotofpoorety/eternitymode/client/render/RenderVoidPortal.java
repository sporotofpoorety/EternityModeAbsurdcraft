package org.sporotofpoorety.eternitymode.client.render;

import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelOrbScary;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

import org.sporotofpoorety.eternitymode.entity.EntityVoidPortal;




public class RenderVoidPortal extends Render<EntityVoidPortal> {
   protected ModelSRP mainModel2 = new ModelOrbScary();
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/orbvoid.png");
   private static final ResourceLocation LIGHTNING_TEX = new ResourceLocation("srparasites:textures/entity/monster/orbvoid_armor.png");
   private static final float SPHERE_RADIUS = 0.317F;
   private static final int SPHERE_STACKS = 18;
   private static final int SPHERE_SLICES = 18;
   private final HashMap<Integer, Float> scaleSmooth = new HashMap();

   public RenderVoidPortal(RenderManager p_i47208_1_) {
      super(p_i47208_1_);
   }

   protected ResourceLocation getEntityTexture(EntityVoidPortal entity) {
      return TEXTURES;
   }

   public boolean shouldRender(EntityVoidPortal livingEntity, ICamera camera, double camX, double camY, double camZ) {
      return true;
   }

   public void doRender(EntityVoidPortal entity, double x, double y, double z, float entityYaw, float partialTicks) {
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
      this.doRenderCosmical(entity, x, y, z, entityYaw, partialTicks);
   }

   public void doRenderCosmical(EntityVoidPortal entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      boolean shouldSit = entity.isRiding() && entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit();
      this.mainModel2.isRiding = shouldSit;

      try {
         float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
         float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
         float f2 = f1 - f;
         float f3;
         if (shouldSit && entity.getRidingEntity() instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)entity.getRidingEntity();
            f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
            f2 = f1 - f;
            f3 = MathHelper.wrapDegrees(f2);
            if (f3 < -85.0F) {
               f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
               f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
               f += f3 * 0.2F;
            }

            f2 = f1 - f;
         }

         float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
         this.renderLivingAt(entity, x, y, z);
         f3 = this.handleRotationFloat(entity, partialTicks);
         this.applyRotations(entity, f3, f, partialTicks);
         float f4 = this.prepareScaleCosmical(entity, partialTicks);
         float f5 = 0.0F;
         float f6 = 0.0F;
         if (!entity.isRiding()) {
            f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
            f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
            if (f5 > 1.0F) {
               f5 = 1.0F;
            }

            f2 = f1 - f;
         }

         GlStateManager.enableAlpha();
         this.mainModel2.setLivingAnimations(entity, f6, f5, partialTicks);
         this.mainModel2.setRotationAngles(f6, f5, f3, f2, f7, f4, entity);
         this.mainModel2.setRotationAnglesCosmical(f6, f5, f3, f2, f7, f4, entity);
         if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
         } else {
            this.renderModelCosmical(entity, f6, f5, f3, f2, f7, f4, partialTicks);
            GlStateManager.depthMask(true);
         }

         GlStateManager.disableRescaleNormal();
      } catch (Exception var19) {
      }

      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.enableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.enableCull();
      GlStateManager.popMatrix();
   }

   protected void renderModelCosmical(EntityVoidPortal e, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, float partialTicks) {
      boolean flag = false;
      boolean flag1 = !flag && !e.isInvisibleToPlayer(Minecraft.getMinecraft().player);
      if (flag || flag1) {
         if (!this.bindEntityTextureCosmical(e)) {
            return;
         }

         boolean cameraInside = this.isCameraInsideOrb(e, partialTicks);
         GlStateManager.enableDepth();
         GlStateManager.depthFunc(515);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         GlStateManager.alphaFunc(516, 0.003921569F);
         GlStateManager.depthMask(!cameraInside);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.renderTexturedSphere(0.317F, 18, 18);
         GlStateManager.depthMask(false);
         GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
         this.renderChargedAura(e, partialTicks);
         GlStateManager.disableBlend();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.depthMask(true);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }

   }

   private boolean isCameraInsideOrb(EntityVoidPortal e, float partialTicks) {
      Entity view = Minecraft.getMinecraft().getRenderViewEntity();
      if (view == null) {
         return false;
      } else {
         Vec3d eye = view.getPositionEyes(partialTicks);
         double GROW = 1.25D;
         AxisAlignedBB bb = e.getEntityBoundingBox().grow(1.25D);
         return bb.contains(eye);
      }
   }

   private void renderChargedAura(EntityVoidPortal e, float partialTicks) {
      this.bindTexture(LIGHTNING_TEX);
      GlStateManager.pushMatrix();
      float sca = 1.12F;
      GlStateManager.scale(sca, sca, sca);
      GlStateManager.disableLighting();
      GlStateManager.depthMask(false);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
      float prevBX = OpenGlHelper.lastBrightnessX;
      float prevBY = OpenGlHelper.lastBrightnessY;
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.enableTexture2D();
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      float t = (float)e.ticksExisted + partialTicks;
      float pulse = 0.25F + 0.15F * MathHelper.sin(t * 0.25F);
      GlStateManager.color(0.6F, 0.85F, 1.0F, pulse);
      float uScroll = t * 0.01F;
      float vScroll = t * 0.015F;
      this.renderSwirlSphere(0.317F, 18, 18, uScroll, vScroll);
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevBX, prevBY);
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
      GlStateManager.enableLighting();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   private void renderSwirlSphere(float radius, int stacks, int slices, float uOff, float vOff) {
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder bb = tess.getBuffer();
      float uScale = 2.0F;
      float vScale = 2.0F;

      for(int i = 0; i < stacks; ++i) {
         float v0 = (float)i / (float)stacks;
         float v1 = (float)(i + 1) / (float)stacks;
         double phi0 = 3.141592653589793D * (double)v0;
         double phi1 = 3.141592653589793D * (double)v1;
         bb.begin(5, DefaultVertexFormats.POSITION_TEX);

         for(int j = 0; j <= slices; ++j) {
            float u = (float)j / (float)slices;
            double theta = 6.283185307179586D * (double)u;
            double x0 = Math.sin(phi0) * Math.cos(theta);
            double y0 = Math.cos(phi0);
            double z0 = Math.sin(phi0) * Math.sin(theta);
            double x1 = Math.sin(phi1) * Math.cos(theta);
            double y1 = Math.cos(phi1);
            double z1 = Math.sin(phi1) * Math.sin(theta);
            float uu = u * 2.0F + uOff;
            float vv0 = (1.0F - v0) * 2.0F + vOff;
            float vv1 = (1.0F - v1) * 2.0F + vOff;
            bb.pos(x0 * (double)radius, y0 * (double)radius, z0 * (double)radius).tex((double)uu, (double)vv0).endVertex();
            bb.pos(x1 * (double)radius, y1 * (double)radius, z1 * (double)radius).tex((double)uu, (double)vv1).endVertex();
         }

         tess.draw();
      }

   }

   private void renderTexturedSphere(float radius, int stacks, int slices) {
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder bb = tess.getBuffer();

      for(int i = 0; i < stacks; ++i) {
         float v0 = (float)i / (float)stacks;
         float v1 = (float)(i + 1) / (float)stacks;
         double phi0 = 3.141592653589793D * (double)v0;
         double phi1 = 3.141592653589793D * (double)v1;
         bb.begin(5, DefaultVertexFormats.POSITION_TEX);

         for(int j = 0; j <= slices; ++j) {
            float u = (float)j / (float)slices;
            double theta = 6.283185307179586D * (double)u;
            double x0 = Math.sin(phi0) * Math.cos(theta);
            double y0 = Math.cos(phi0);
            double z0 = Math.sin(phi0) * Math.sin(theta);
            double x1 = Math.sin(phi1) * Math.cos(theta);
            double y1 = Math.cos(phi1);
            double z1 = Math.sin(phi1) * Math.sin(theta);
            bb.pos(x0 * (double)radius, y0 * (double)radius, z0 * (double)radius).tex((double)u, (double)(1.0F - v0)).endVertex();
            bb.pos(x1 * (double)radius, y1 * (double)radius, z1 * (double)radius).tex((double)u, (double)(1.0F - v1)).endVertex();
         }

         tess.draw();
      }

   }

   protected boolean bindEntityTextureCosmical(EntityVoidPortal entity) {
      ResourceLocation resourcelocation = this.getEntityTexture(entity);
      if (resourcelocation == null) {
         return false;
      } else {
         this.bindTexture(resourcelocation);
         return true;
      }
   }

   protected float prepareScaleCosmical(EntityVoidPortal entitylivingbaseIn, float partialTicks) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      this.preRenderCallbackCosmical(entitylivingbaseIn, partialTicks);
      return 0.0625F;
   }

   protected void preRenderCallbackCosmical(EntityVoidPortal e, float partialTickTime) {
      float age = (float)e.ticksExisted + partialTickTime;
      float base = Math.max(e.width * 2.0F, e.height * 1.9F) * 0.8F;
      float GROW_TICKS = 35.0F;
      float g = MathHelper.clamp(age / 35.0F, 0.0F, 1.0F);
      g = g * g * (3.0F - 2.0F * g);
      float normalScale = base * (0.35F + 0.95F * g);
      float tau = MathHelper.clamp(age / 35.0F, 0.0F, 1.0F);
      float BOUNCE_AMP = 0.28F;
      float DAMP = 5.0F;
      float FREQ = 10.0F;
      float bounce = 0.28F * (float)Math.exp((double)(-5.0F * tau)) * MathHelper.sin(10.0F * tau * 3.1415927F);
      float targetScale = normalScale * (1.0F + bounce);
      if (targetScale < 0.001F) {
         targetScale = 0.001F;
      }

      int id = e.getEntityId();
      Float prev = (Float)this.scaleSmooth.get(id);
      if (prev == null) {
         prev = targetScale;
      }

      float smooth = prev + (targetScale - prev) * 0.2F;
      this.scaleSmooth.put(id, smooth);
      if (e.isDead) {
         this.scaleSmooth.remove(id);
      }

      GlStateManager.scale(smooth, smooth, smooth);
      float hoverAmp = 0.05F * (0.2F + 0.8F * g);
      float hover = MathHelper.sin(age * 0.1F + (float)id * 0.3F) * hoverAmp;
      float yaw = age * 1.4F % 360.0F;
      float pitch = 8.0F * MathHelper.sin(age * 0.07F + (float)id);
      GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
   }

   protected float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
      float f;
      for(f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F) {
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return prevYawOffset + partialTicks * f;
   }

   protected void renderLivingAt(EntityVoidPortal entityLivingBaseIn, double x, double y, double z) {
      GlStateManager.translate((float)x, (float)y, (float)z);
   }

   protected float handleRotationFloat(EntityVoidPortal livingBase, float partialTicks) {
      return (float)livingBase.ticksExisted + partialTicks;
   }

   protected void applyRotations(EntityVoidPortal entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      GlStateManager.rotate(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
      if (entityLiving.deathTime > 0) {
         float f = ((float)entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
         f = MathHelper.sqrt(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         GlStateManager.rotate(f * 90.0F, 0.0F, 0.0F, 1.0F);
      } else {
         String s = TextFormatting.getTextWithoutFormattingCodes(entityLiving.getName());
         if (s != null && ("Dinnerbone".equals(s) || "Grumm".equals(s))) {
            GlStateManager.translate(0.0F, entityLiving.height + 0.1F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
         }
      }

   }
}
