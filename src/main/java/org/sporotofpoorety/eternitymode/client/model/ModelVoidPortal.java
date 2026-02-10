package org.sporotofpoorety.eternitymode.client.model;

import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;




public class ModelVoidPortal extends ModelSRP {
   public ModelRenderer mainbody;
   public ModelRenderer kd;
   public ModelRenderer kd_1;
   public ModelRenderer mainbody_1;
   public ModelRenderer mainbody_2;

   public ModelVoidPortal() {
      this.textureWidth = 128;
      this.textureHeight = 128;
      this.kd = new ModelRenderer(this, 0, 0);
      this.kd.setRotationPoint(0.0F, 28.0F, 0.0F);
      this.kd.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
      this.setRotateAngle(this.kd, 0.7853982F, 0.0F, 0.0F);
      this.mainbody_1 = new ModelRenderer(this, 48, 16);
      this.mainbody_1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.mainbody_1.addBox(-8.0F, -12.0F, -8.0F, 16, 16, 16, 0.0F);
      this.setRotateAngle(this.mainbody_1, 0.0F, 0.7853982F, 0.0F);
      this.mainbody = new ModelRenderer(this, 0, 0);
      this.mainbody.setRotationPoint(0.0F, 28.0F, 0.0F);
      this.mainbody.addBox(-8.0F, -12.0F, -8.0F, 16, 16, 16, 0.0F);
      this.mainbody_2 = new ModelRenderer(this, 0, 32);
      this.mainbody_2.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.mainbody_2.addBox(-8.0F, -12.0F, -8.0F, 16, 16, 16, 0.0F);
      this.setRotateAngle(this.mainbody_2, 0.0F, 0.7853982F, 0.0F);
      this.kd_1 = new ModelRenderer(this, 4, 0);
      this.kd_1.setRotationPoint(0.0F, 28.0F, 0.0F);
      this.kd_1.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
      this.setRotateAngle(this.kd_1, -0.7853982F, 0.0F, 0.0F);
      this.kd.addChild(this.mainbody_1);
      this.kd_1.addChild(this.mainbody_2);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      double scale = 0.5D;
      GlStateManager.pushMatrix();
      GlStateManager.translate(this.mainbody.offsetX, this.mainbody.offsetY, this.mainbody.offsetZ);
      GlStateManager.translate(this.mainbody.rotationPointX * f5, this.mainbody.rotationPointY * f5, this.mainbody.rotationPointZ * f5);
      GlStateManager.scale(scale, scale, scale);
      GlStateManager.translate(-this.mainbody.offsetX, -this.mainbody.offsetY, -this.mainbody.offsetZ);
      GlStateManager.translate(-this.mainbody.rotationPointX * f5, -this.mainbody.rotationPointY * f5, -this.mainbody.rotationPointZ * f5);
      this.kd.render(f5);
      this.mainbody.render(f5);
      this.kd_1.render(f5);
      GlStateManager.popMatrix();
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      float f1 = -0.35F;
      this.mainbody.offsetY = f1;
      this.kd.offsetY = f1;
      this.kd_1.offsetY = f1;
   }
}
