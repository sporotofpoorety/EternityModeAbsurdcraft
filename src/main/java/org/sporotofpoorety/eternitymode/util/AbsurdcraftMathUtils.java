package org.sporotofpoorety.eternitymode.util;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;



public final class AbsurdcraftMathUtils 
{

    public static final double PHI = 1.618033988749895;


    public static ArrayList<Integer> pickDifferentX(int pickX, int fromA, int toB)
    {
        List<Integer> possibleNumbers = new ArrayList<>();

        for (int numAt = fromA; numAt <= toB; numAt++) 
        {
            possibleNumbers.add(numAt);
        }

        Collections.shuffle(possibleNumbers);

        ArrayList<Integer> pickedNums = new ArrayList<>(possibleNumbers.subList(0, pickX));

        return pickedNums;
    }




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

        Vec3d rightV = forwardV.crossProduct(upV).normalize();

        return new Vec3d[]{ rightV, upV, forwardV };
    }




//Generate angle spread
    public static ArrayList<Vec3d> fibonacciDirectionalSpread(Vec3d forwardOriginal, int pointCount, double coneEdgeRadians) 
    {
//Array of radial vectors
        ArrayList<Vec3d> radialPopulation = new ArrayList<>(pointCount);

//Normalize original forward direction just in case
        Vec3d forwardNormalized = forwardOriginal.normalize();
        radialPopulation.add(forwardNormalized);


//Orthonormal basis of the original forward direction
        Vec3d[] forwardBasis = makeOrthonormalBasis(forwardNormalized);
//Get its axes
        Vec3d orthoRight = forwardBasis[0];
        Vec3d orthoUp = forwardBasis[1];
        Vec3d orthoForward = forwardBasis[2];


//Golden angle approx 137.5 degrees
        final double goldenRadians = (3.0 - Math.sqrt(5.0)) * Math.PI;


//Cosine bounds for cone
        double edgeCosine = Math.cos(coneEdgeRadians);
        double middleCosine = 1.0;


//Now get to placing the points
        for (int projectileAt = 0; projectileAt < pointCount; projectileAt++) 
        {

//Evenly distribute samples in the middle of each stop
            double middleFactor = (projectileAt + 0.5) / pointCount;


//Iterate over cosine space
            double directionCosine =
//From the middle cosine
                middleCosine
//Iterate to edge cosine over middle-points 
                    + ((edgeCosine - middleCosine) * middleFactor);

//For sine you scale down logarithmically as cosine increases
            double directionSine =
                    Math.sqrt(1.0 - directionCosine * directionCosine);


// Rotate around the cone using the golden angle
            double coneAngleAt = projectileAt * goldenRadians;

            double localX = Math.cos(coneAngleAt) * directionSine;
            double localY = Math.sin(coneAngleAt) * directionSine;
            double localZ = directionCosine;


// Transform from local cone space to world space
            Vec3d worldDirection =
                orthoRight.scale(localX)
                .add(orthoUp.scale(localY))
                .add(orthoForward.scale(localZ))
                .normalize();


//Add direction to radial population
            radialPopulation.add(worldDirection);
        }


        return radialPopulation;
    }




    public static double verticalVelocityForArc(double targetHeightDiff, double intendedAirTime, double gravitySpeed)  
    {
        return (targetHeightDiff / intendedAirTime) 
            - (gravitySpeed * ((intendedAirTime - 1.0D) / 2.0D));
    }




/*
//How to get predictive aim?


//First, keep in mind that vectors
//use dot product instead of multiplication


//Calculate convergence time for projectile and target

1.
Pvel*T
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




    public static double simpleSummationDecimal(double stepInitial, double stepIncrement, int stepCount)
    {
        double currentStep = stepInitial;
        double accumulatedTotal = 0.0D;


        for(int stepAt = 1; stepAt <= stepCount; stepAt++)
        {
            currentStep += stepIncrement;

            accumulatedTotal += currentStep;       
        }


        return accumulatedTotal;
    }




//Make cone of vectors,
//constructed using trigonometry
    public static ArrayList<Vec3d> shootCone(Vec3d trigAdjacent, double coneAngle, int pointCount)
    {
        ArrayList<Vec3d> conePoints = new ArrayList<>(pointCount);


//Orthonormal basis of the original forward direction
        Vec3d[] forwardBasis = makeOrthonormalBasis(trigAdjacent);
//Get its axes
        Vec3d orthoRight = forwardBasis[0];
        Vec3d orthoUp = forwardBasis[1];
        Vec3d orthoForward = forwardBasis[2];

        
//For each point in cone
        double circumStep = (2.0D * Math.PI) / pointCount;
        for(int pointAt = 0; pointAt < pointCount; pointAt++)
        {
            double circumAt = circumStep * pointAt;

//Get the circular direction
            Vec3d circumAtDir = orthoRight.scale(Math.cos(circumAt)).add(orthoUp.scale(Math.sin(circumAt)));
//Scale circular direction 
//by length of cone and tan of cone's angle
            Vec3d trigOpposite = circumAtDir.scale(trigAdjacent.length()).scale(Math.tan(coneAngle / 2.0D)); 

//Get hypothenuse of cone point,
//using forward direction as cos and circular direction as sin
            Vec3d conePointHypothenuse = trigAdjacent.add(trigOpposite);
      
//Add hypothenuse to cone points
            conePoints.add(conePointHypothenuse);
        }


//Return total cone points
        return conePoints;
    }




/*
//For this, provide a starting vector,
//a number of stops and radians for the ring and the spreads
    public static ArrayList<Vec3d> radialPopulationAlgorithm(Vec3d startingVector, 
    int ringStopTotal, double ringStopRadians, int spreadStopTotal, double spreadStopRadians,
    boolean completeSphereMode)
    {
//Fill this list with radial vectors
        ArrayList<Vec3d> radialPopulation = new ArrayList<>();

//Get relative axes of starting vector
        Vec3d[] startingBasis = makeOrthonormalBasis(startingVector);




//Ring around starting vector's relative Y axis
        for(int ringVectorAt = 0; ringVectorAt <= ringStopTotal; ringVectorAt++)
        {
//Currently at this cos on the ring
            double ringCosAt = Math.cos(ringStopRadians * ringVectorAt);
//Currently at this sin on the ring
            double ringSinAt = Math.sin(ringStopRadians * ringVectorAt);        
        

//Current relative Z on the ring
            Vec3d ringPositiveStopZ = new Vec3d
            (
                startingBasis[2].x * ringCosAt, 
                startingBasis[2].y * ringCosAt, 
                startingBasis[2].z * ringCosAt
            );
//Mirror it
            Vec3d ringNegativeStopZ = new Vec3d
            (
                startingBasis[2].x * (ringCosAt * -1.0D), 
                startingBasis[2].y * (ringCosAt * -1.0D), 
                startingBasis[2].z * (ringCosAt * -1.0D)
            );
//Current relative X on the ring
            Vec3d ringPositiveStopX = new Vec3d
            (
                startingBasis[0].x * ringSinAt, 
                startingBasis[0].y * ringSinAt, 
                startingBasis[0].z * ringSinAt
            );
//Mirror it
            Vec3d ringNegativeStopX = new Vec3d
            (
                startingBasis[0].x * (ringSinAt * -1.0D), 
                startingBasis[0].y * (ringSinAt * -1.0D), 
                startingBasis[0].z * (ringSinAt * -1.0D)
            );

//Sum them to get the current ring stops
            Vec3d ringPositiveStopVector = ringPositiveStopZ.add(ringPositiveStopX);
            Vec3d ringNegativeStopVector = ringNegativeStopZ.add(ringNegativeStopX);




//Get current stop vectors orthonormal bases
            Vec3d[] ringPositiveStopBasis = makeOrthonormalBasis(ringPositiveStopVector);
            Vec3d[] ringNegativeStopBasis = makeOrthonormalBasis(ringNegativeStopVector);

//Spread around current ring stop's relative X axis
            for(int spreadVectorAt = 0; spreadVectorAt <= spreadStopTotal; spreadVectorAt++)
            {
//Currently at this cos on the spread
                double spreadCosAt = Math.cos(spreadStopRadians * spreadVectorAt);
//Currently at this sin on the spread
                double spreadSinAt = Math.sin(spreadStopRadians * spreadVectorAt);

//Current relative Z on the spread
                Vec3d spreadPositiveStopZ = new Vec3d
                (
                    ringPositiveStopBasis[2].x * spreadCosAt, 
                    ringPositiveStopBasis[2].y * spreadCosAt, 
                    ringPositiveStopBasis[2].z * spreadCosAt
                );
//Mirror it
                Vec3d spreadNegativeStopZ = new Vec3d
                (
                    ringPositiveStopBasis[2].x * (spreadCosAt * -1.0D), 
                    ringPositiveStopBasis[2].y * (spreadCosAt * -1.0D), 
                    ringPositiveStopBasis[2].z * (spreadCosAt * -1.0D)
                );
//Current relative Y on the spread
                Vec3d spreadPositiveStopY = new Vec3d
                (
                    ringPositiveStopBasis[1].x * spreadSinAt, 
                    ringPositiveStopBasis[1].y * spreadSinAt, 
                    ringPositiveStopBasis[1].z * spreadSinAt
                );
//Mirror it
                Vec3d spreadStopNegativeY = new Vec3d
                (
                    ringPositiveStopBasis[1].x * (spreadSinAt * -1.0D), 
                    ringPositiveStopBasis[1].y * (spreadSinAt * -1.0D), 
                    ringPositiveStopBasis[1].z * (spreadSinAt * -1.0D)
                );

//Sum them to get the current spread stops
                Vec3d spreadPositiveStopVector = spreadPositiveStopZ.add(spreadPositiveStopY);
                Vec3d spreadNegativeStopVector = spreadNegativeStopZ.add(spreadNegativeStopY);

//Add generated vectors to array list
                radialPopulation.add(spreadPositiveStopVector.normalize());
                radialPopulation.add(spreadNegativeStopVector.normalize());
            }

//Repeat the above but mirrored for current negative ring stop
            for(int spreadVectorAt = 0; spreadVectorAt <= spreadStopTotal; spreadVectorAt++)
            {
                double spreadCosAt = Math.cos(spreadStopRadians * spreadVectorAt);
                double spreadSinAt = Math.sin(spreadStopRadians * spreadVectorAt);

                Vec3d spreadPositiveStopZ = new Vec3d
                (
                    ringNegativeStopBasis[2].x * spreadCosAt, 
                    ringNegativeStopBasis[2].y * spreadCosAt, 
                    ringNegativeStopBasis[2].z * spreadCosAt
                );

                Vec3d spreadNegativeStopZ = new Vec3d
                (
                    ringNegativeStopBasis[2].x * (spreadCosAt * -1.0D), 
                    ringNegativeStopBasis[2].y * (spreadCosAt * -1.0D), 
                    ringNegativeStopBasis[2].z * (spreadCosAt * -1.0D)
                );

                Vec3d spreadPositiveStopY = new Vec3d
                (
                    ringNegativeStopBasis[1].x * spreadSinAt, 
                    ringNegativeStopBasis[1].y * spreadSinAt, 
                    ringNegativeStopBasis[1].z * spreadSinAt
                );

                Vec3d spreadStopNegativeY = new Vec3d
                (
                    ringNegativeStopBasis[1].x * (spreadSinAt * -1.0D), 
                    ringNegativeStopBasis[1].y * (spreadSinAt * -1.0D), 
                    ringNegativeStopBasis[1].z * (spreadSinAt * -1.0D)
                );

                Vec3d spreadPositiveStopVector = spreadPositiveStopZ.add(spreadPositiveStopY);
                Vec3d spreadNegativeStopVector = spreadNegativeStopZ.add(spreadNegativeStopY);

                radialPopulation.add(spreadPositiveStopVector.normalize());
                radialPopulation.add(spreadNegativeStopVector.normalize());
            }
        }




//Add vectors at exact orthonormal up and down without overlap
        if(completeSphereMode)
        {
            radialPopulation.add(startingBasis[1].normalize());
            radialPopulation.add(startingBasis[1].scale(-1.0D).normalize());          
        }

        return radialPopulation;
    }
*/
}
