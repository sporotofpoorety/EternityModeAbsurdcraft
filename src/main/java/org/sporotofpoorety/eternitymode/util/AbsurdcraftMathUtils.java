package org.sporotofpoorety.eternitymode.util;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;



public final class AbsurdcraftMathUtils {

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




    public static ArrayList<Vec3d> fibonacciDirectionalSpread(Vec3d forwardOriginal, int pointCount, double coneEdgeRadians) 
    {
//Array of radial vectors
        ArrayList<Vec3d> radialPopulation = new ArrayList<>(pointCount);

//Normalize original forward direction just in case
        Vec3d forwardNormalized = forwardOriginal.normalize();


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
        for (int projectileAt = 0; projectileAt < pointCount; projectileAt++) {

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




    public static Vec3d generatePredictiveAimVectorNoVertical(double aimX, double aimY, double aimZ,
    Entity vecTarget, double aimerSpeedFactor)
    {
//It turns out this is an 
//implicit equation, rather than a clean formula,
//i have to approximate the target's predicted distance through iteration



//Get distance to target's current distance
        Vec3d zerothApproxDist = new Vec3d
        (
            vecTarget.posX - aimX,
            vecTarget.posY - aimY,
            vecTarget.posZ - aimZ
        );

//Get arrival time to target's current distance
        double zerothApproxArrivalTime = Math.sqrt
        (
            (vecTarget.posX - aimX) * (vecTarget.posX - aimX)
            + (vecTarget.posZ - aimZ) * (vecTarget.posZ - aimZ)
        ) 
        / (aimerSpeedFactor * aimerSpeedFactor);



//Predict target distance based on motion
        Vec3d firstApproxDist = 
            zerothApproxDist.add
            (
                vecTarget.motionX * zerothApproxArrivalTime,
                0.0D,
                vecTarget.motionZ * zerothApproxArrivalTime
            );

//Get arrival time to target's predicted distance
        double firstApproxArrivalTime = (firstApproxDist.length()) / aimerSpeedFactor;



//Get final refined approximation of target's predicted distance
        Vec3d secondApproxDist = 
            zerothApproxDist.add
            (
                vecTarget.motionX * firstApproxArrivalTime,
                0.0D,
                vecTarget.motionZ * firstApproxArrivalTime
            );


        return secondApproxDist;
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
