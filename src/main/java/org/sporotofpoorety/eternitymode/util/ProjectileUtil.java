package org.sporotofpoorety.eternitymode.util;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.sporotofpoorety.eternitymode.util.DirectionalSpreadUtil;



public final class ProjectileUtil 
{

//Spherical shotgun (linear)
    public static ArrayList<Vec3d> fibonacciSpreadAimed
    (double aimX, double aimY, double aimZ, 
    double targetX, double targetY, double targetZ,
    int pointCount, double coneRadians)
    {
//From one point to another
        return DirectionalSpreadUtil.fibonacciDirectionalSpread
            (new Vec3d(targetX - aimX, targetY - aimY, targetZ - aimZ), 
                pointCount, coneRadians);     
    }


//Spherical shotgun (predictive)
    public static ArrayList<Vec3d> fibonacciSpreadPredictive
    (World worldIn, Entity aimerEntity, Entity vecTarget, 
    double aimX, double aimY, double aimZ, double projVel,
    int pointCount, double coneRadians,
    boolean canPredictVertical, double verticalThreshold)
    {
//If target is valid
        if(vecTarget != null)
        {
//Get prediction vector
            Vec3d predictiveVec = AimUtil.calcPredictiveAimDynamicVertical
                (new Vec3d(vecTarget.posX - aimX, vecTarget.posY - aimY, vecTarget.posZ - aimZ), vecTarget, projVel, canPredictVertical, verticalThreshold);

//Fibonacci spread aimed from 
//aim pos to target entity's predicted movement
            return DirectionalSpreadUtil.fibonacciDirectionalSpread
                (new Vec3d(predictiveVec.x, predictiveVec.y, predictiveVec.z), 
                    pointCount, coneRadians);
        }
        else
        {
//Random base direction
            Vec3d randomDirection = new Vec3d(
                aimerEntity.world.rand.nextDouble(), aimerEntity.world.rand.nextDouble(), aimerEntity.world.rand.nextDouble());

//Make fibonacci spread
            return DirectionalSpreadUtil.fibonacciDirectionalSpread(randomDirection, pointCount, coneRadians);
        }
    }

}
