package org.sporotofpoorety.eternitymode.util;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;



public final class AimUtil 
{


/*
//How to get predictive aim?


//First, keep in mind that vectors
//use dot product instead of multiplication


//Calculate convergence time for projectile and target

1.
//Projectile distance traveled
Pvel*T
//To match magnitude of 
//initial distance + target travel
= |Tmv*T + d0|


//Square to get rid of that magnitude operator

2.
Pvel²*T²
= Tmv²*T² + 2(Tmv*T)(d0) + d0²

3.
0
= Tmv²*T² + 2(Tmv*T)(d0) + d0² - Pvel²*T² 

4.
0
= (Tmv² - Pvel²)T² + (2 * Tmv * d0)T + d0²




Discriminant
= (2 * Tmv * d0)² - 4(Tmv² - Pvel²)(d0²)


Roots
= ( -(2 * Tmv * d0) +- sqrt((2 * Tmv * d0)² - 4(Tmv² - Pvel²)(d0²)) )  /  2(Tmv² - Pvel²)
*/


//Predictive aim (dynamically vertical or not)
    public static Vec3d calcPredictiveAimDynamicVertical(Vec3d dist0, Entity target, double projVel, boolean canPredictVertical, double verticalThreshold)
    {
        boolean shouldPredictVertical = false;

//If can predict vertical
        if(canPredictVertical)
        {
//If target height above threshold to predict
            if(dist0.y >= verticalThreshold)
            {
                shouldPredictVertical = true;  
            }
//If target is player
            if(target instanceof EntityPlayer)
            {
//And player is flying
                if( ((EntityPlayer) target).isElytraFlying() )
                {
                    shouldPredictVertical = true;
                } 
            }
        }


//Get target movement
//      Vec3d targetMv = new Vec3d(target.posX - target.lastTickPosX, target.posY - target.lastTickPosY, target.posZ - target.lastTickPosZ);
        Vec3d targetMv = new Vec3d(target.motionX, target.motionY, target.motionZ);


        if(shouldPredictVertical) { return calcPredictiveAimVec(dist0, targetMv, projVel); }
        else { return calcPredictiveAimVecNoVertical(dist0, targetMv, projVel); }
    }


//Predictive aim
    public static Vec3d calcPredictiveAimVec(Vec3d dist0, Vec3d targetMv, double projVel)
    {
//Get time to converge to target
        double convergeTime = calcProjectileConvergeTime(dist0, targetMv, projVel);

//If valid converge time
        if(convergeTime > 0.0D)
        {
//Get target's future offset
            Vec3d targetFutureOffset = dist0.add(targetMv.scale(convergeTime));

//Get aim vector to that offset
            Vec3d predictiveAimVec = targetFutureOffset.normalize().scale(projVel);

            return predictiveAimVec;
        }
//If no positive converge time, 
//just use direct aim as a fallback
        else
        {
            Vec3d fallbackAim = dist0.normalize().scale(projVel);

            return fallbackAim;
        }
    }

//Predictive aim (no vertical)
    public static Vec3d calcPredictiveAimVecNoVertical(Vec3d dist0, Vec3d targetMv, double projVel)
    {
//Remove vertical component of target movement
        Vec3d targetMvNoVtc = new Vec3d(targetMv.x, 0.0D, targetMv.z);

//Get time to converge to target
        double convergeTime = calcProjectileConvergeTime(dist0, targetMvNoVtc, projVel);

//If valid converge time
        if(convergeTime > 0.0D)
        {
//Get target's future offset
            Vec3d targetFutureOffsetNoVtc = dist0.add(targetMvNoVtc.scale(convergeTime));

//Get aim vector to that offset
            Vec3d predictiveAimVec = targetFutureOffsetNoVtc.normalize().scale(projVel);

            return predictiveAimVec;
        }
//If no pos converge, 
//use fallback linear aim
        else
        {
            Vec3d fallbackAim = dist0.normalize().scale(projVel);

            return fallbackAim;
        }
    }


/*
Discriminant
= (2 * Tmv * d0)² - 4(Tmv² - Pvel²)(d0²)


Roots
= ( -(2 * Tmv * d0) +- sqrt((2 * Tmv * d0)² - 4(Tmv² - Pvel²)(d0²)) )  /  2(Tmv² - Pvel²)
*/
    public static double calcProjectileConvergeTime(Vec3d dist0, Vec3d targetMv, double projVel)
    {
        double a = (targetMv.dotProduct(targetMv) - projVel * projVel);
        double b = (2.0D * targetMv.dotProduct(dist0));
        double c = (dist0.dotProduct(dist0));

        double discriminant = (b * b) - (4.0D * a * c);


//Check for valid converge time
        if(discriminant >= 0.0D)
        {
            double rootFirst = ((-1.0D * b) + Math.sqrt(discriminant)) / (2.0D * a);
            double rootSecond = ((-1.0D * b) - Math.sqrt(discriminant)) / (2.0D * a);


//Pick lowest positive root
            if(rootFirst > 0.0D)
            {
                if(rootSecond > 0.0D)
                {
                    return (rootSecond < rootFirst) ? rootSecond : rootFirst; 
                }
                else
                {
                    return rootFirst;
                }
            }
            else
            {
                if(rootSecond > 0.0D)
                {
                    return rootSecond;
                }
//If neither are positive return no valid converge time
                else
                {
                    return -1.0D;
                }
            }
        }
        else
        {
            return -1.0D;
        }
    }

}
