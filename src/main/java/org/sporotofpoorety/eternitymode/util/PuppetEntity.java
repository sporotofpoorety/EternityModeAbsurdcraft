package org.sporotofpoorety.eternitymode.util;


import java.util.UUID;


import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;




//An entity, UUID, relative pos, timer and glue flag (i might add more later)
public class PuppetEntity 
{
    public Entity entity;
    public UUID puppetUUID;

    public boolean previousValidatePuppetFailed;

    public double offsetX, offsetY, offsetZ;

    public int controlTime;
    public int controlState;

    public Vec3d storedVec = new Vec3d(0.0D, 0.0D, 0.0D);
    public double storedDistance;


    public PuppetEntity(Entity entity,
    double offsetX, double offsetY, double offsetZ, int controlTime, int controlState) 
    {
        this.entity = entity;
        this.puppetUUID = entity.getUniqueID();

        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }
}
