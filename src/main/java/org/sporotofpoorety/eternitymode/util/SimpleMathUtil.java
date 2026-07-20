package org.sporotofpoorety.eternitymode.util;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;




public final class SimpleMathUtil
{

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
