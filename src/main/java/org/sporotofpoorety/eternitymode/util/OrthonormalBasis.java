package org.sporotofpoorety.eternitymode.util;


import net.minecraft.util.math.Vec3d;




public final class OrthonormalBasis 
{

//It's pretty complicated but it should work fine

    public static Vec3d[] makeOrthonormalBasis(Vec3d originalVector) 
    {
        Vec3d forwardV = originalVector.normalize();
        Vec3d upV;


//If original vector is more sideways
        if (Math.abs(forwardV.x) > Math.abs(forwardV.z)) 
        {
            upV = new Vec3d(-forwardV.y, forwardV.x, 0).normalize();
//If original vector is more towards
        } 
        else 
        {
            upV = new Vec3d(0, -forwardV.z, forwardV.y).normalize();
        }


//Now just get perpendicular
//axis from cross product, the X to the Y and Z
        Vec3d rightV = forwardV.crossProduct(upV).normalize();


//Return orthobasis
        return new Vec3d[]{ rightV, upV, forwardV };
    }

}
