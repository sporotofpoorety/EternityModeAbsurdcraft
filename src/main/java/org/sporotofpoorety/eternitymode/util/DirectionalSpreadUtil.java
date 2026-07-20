package org.sporotofpoorety.eternitymode.util;


import java.util.ArrayList;

import net.minecraft.util.math.Vec3d;

import org.sporotofpoorety.eternitymode.util.OrthonormalBasis;




public final class DirectionalSpreadUtil 
{

//Make cone of vectors,
//constructed using trigonometry
    public static ArrayList<Vec3d> shootCone(Vec3d trigAdjacent, double coneAngle, int pointCount)
    {
        ArrayList<Vec3d> conePoints = new ArrayList<>(pointCount);


//Orthonormal basis of the original forward direction
        Vec3d[] forwardBasis = OrthonormalBasis.makeOrthonormalBasis(trigAdjacent);
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




//Generate angle spread
    public static ArrayList<Vec3d> fibonacciDirectionalSpread(Vec3d forwardOriginal, int pointCount, double coneEdgeRadians) 
    {
//Array of radial vectors
        ArrayList<Vec3d> radialPopulation = new ArrayList<>(pointCount);

//Normalize original forward direction just in case
        Vec3d forwardNormalized = forwardOriginal.normalize();
        radialPopulation.add(forwardNormalized);


//Orthonormal basis of the original forward direction
        Vec3d[] forwardBasis = OrthonormalBasis.makeOrthonormalBasis(forwardNormalized);
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

}
