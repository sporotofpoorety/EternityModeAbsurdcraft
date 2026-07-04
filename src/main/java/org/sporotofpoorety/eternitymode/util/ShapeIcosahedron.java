package org.sporotofpoorety.eternitymode.util;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;




public final class ShapeIcosahedron 
{

    public static final double PHI = 1.618033988749895;




//Vertices of the icosahedron
    public static final Vec3d[] ICOSAHEDRON_VERTICES = 
    {
// (0, ±1, ±φ)
        new Vec3d(0.0D, 1.0D, PHI), new Vec3d(0.0D, 1.0D, -PHI), new Vec3d(0.0D, -1.0D, PHI), new Vec3d(0.0D, -1.0D, -PHI),
// (±1, ±φ, 0)
        new Vec3d(1.0D, PHI, 0.0D), new Vec3d(-1.0D, PHI, 0.0D), new Vec3d(1.0D, -PHI, 0.0D), new Vec3d(-1.0D, -PHI, 0.0D),
// (±φ, 0, ±1)
        new Vec3d(PHI, 0.0D, 1.0D), new Vec3d(PHI, 0.0D, -1.0D), new Vec3d(-PHI, 0.0D, 1.0D), new Vec3d(-PHI, 0.0D, -1.0D)
    };




//Which vertices correspond to which faces
    public static final int[][] ICOSAHEDRON_FACE_COMPONENTS = 
    {
//Converting from outdated face center order
// 0 1 4 12 13
// 2 3 5 14 15
// 6 16 17 7 18
// 19 8 9 10 11
//
//0 IS 0 IS PHI, 0, 2PHI + 1 |||| 1 IS 1 IS -PHI, 0, 2PHI + 1 |||| 2 IS 4 IS 0, 2PHI + 1, PHI |||| 3 IS 12 IS + + + PHI + 1 |||| 4 IS 13 IS - + + PHI + 1
//5 IS 2 IS PHI, 0, -2PHI - 1 |||| 6 IS 3 IS -PHI, 0, -2PHI - 1 |||| 7 IS 5 IS 0, 2PHI + 1, -PHI |||| 8 IS 14 IS + + - PHI + 1 |||| 9 IS 15 IS - + - PHI + 1
//10 IS 6 IS 0, -2PHI - 1, PHI |||| 11 IS 16 IS + - + PHI + 1 |||| 12 IS X IS - - + PHI + 1 |||| 13 IS X IS 0, -2PHI - 1, -PHI |||| 14 IS 18 IS + - - PHI + 1
//15 IS 19 IS - - - PHI + 1 |||| 16 IS 8 IS 2PHI + 1, PHI, 0 |||| 17 IS 9 IS -2PHI - 1, PHI, 0 |||| 18 IS 10 IS 2PHI + 1, -PHI, 0 |||| 19 IS 11 IS -2PHI - 1, -PHI, 0
        {0, 2, 8}, {0, 2, 10}, {0, 4, 5}, {0, 4, 8}, {0, 5, 10},
        {1, 3, 9}, {1, 3, 11}, {1, 4, 5}, {1, 4, 9}, {1, 5, 11},
        {2, 6, 7}, {2, 6, 8}, {2, 7, 10}, {3, 6, 7}, {3, 6, 9},
        {3, 7, 11}, {4, 8, 9}, {5, 10, 11}, {6, 8, 9}, {7, 10, 11}
    };




//These actually match the face components order
    public static final Vec3d[] ICOSAHEDRON_FACE_CENTERS_SORTED = 
    {
        new Vec3d(PHI / 3.0D, 0.0D, (2.0D * PHI + 1.0D) / 3.0D),
        new Vec3d(-PHI / 3.0D, 0.0D, (2.0D * PHI + 1.0D) / 3.0D),
        new Vec3d(0.0D, (2.0D * PHI + 1.0D) / 3.0D, PHI / 3.0D),
        new Vec3d((PHI + 1.0D) / 3.0D, (PHI + 1.0D) / 3.0D, (PHI + 1.0D) / 3.0D),
        new Vec3d((-PHI - 1.0D) / 3.0D, (PHI + 1.0D) / 3.0D, (PHI + 1.0D) / 3.0D),

        new Vec3d(PHI / 3.0D, 0.0D, (-2.0D * PHI - 1.0D) / 3.0D),
        new Vec3d(-PHI / 3.0D, 0.0D, (-2.0D * PHI - 1.0D) / 3.0D),
        new Vec3d(0.0D, (2.0D * PHI + 1.0D) / 3.0D, -PHI / 3.0D),
        new Vec3d((PHI + 1.0D) / 3.0D, (PHI + 1.0D) / 3.0D, (-PHI - 1.0D) / 3.0D),
        new Vec3d((-PHI - 1.0D) / 3.0D, (PHI + 1.0D) / 3.0D, (-PHI - 1.0D) / 3.0D),

        new Vec3d(0.0D, (-2.0D * PHI - 1.0D) / 3.0D, PHI / 3.0D),
        new Vec3d((PHI + 1.0D) / 3.0D, (-PHI - 1.0D) / 3.0D, (PHI + 1.0D) / 3.0D),
        new Vec3d((-PHI - 1.0D) / 3.0D, (-PHI - 1.0D) / 3.0D, (PHI + 1.0D) / 3.0D),
        new Vec3d(0.0D, (-2.0D * PHI - 1.0D) / 3.0D, -PHI / 3.0D),
        new Vec3d((PHI + 1.0D) / 3.0D, (-PHI - 1.0D) / 3.0D, (-PHI - 1.0D) / 3.0D),

        new Vec3d((-PHI - 1.0D) / 3.0D, (-PHI - 1.0D) / 3.0D, (-PHI - 1.0D) / 3.0D),
        new Vec3d((2.0D * PHI + 1.0D) / 3.0D, PHI / 3.0D, 0.0D),
        new Vec3d((-2.0D * PHI - 1.0D) / 3.0D, PHI / 3.0D, 0.0D),
        new Vec3d((2.0D * PHI + 1.0D) / 3.0D, -PHI / 3.0D, 0.0D),
        new Vec3d((-2.0D * PHI - 1.0D) / 3.0D, -PHI / 3.0D, 0.0D)
    };




//Which vertex pairs form unique lines (avoid redundancy)
    public static final int[][] ICOSAHEDRON_UNIQUE_LINES = 
    {
        {0, 2}, {0, 4}, {0, 5}, {0, 8}, {0, 10},
        {1, 3}, {1, 4}, {1, 5}, {1, 9}, {1, 11},
        {2, 6}, {2, 7}, {2, 8}, {2, 10},
        {3, 6}, {3, 7}, {3, 9}, {3, 11},
        {4, 5}, {4, 8}, {4, 9},
        {5, 10}, {5, 11},
        {6, 7}, {6, 8}, {6, 9},
        {7, 10}, {7, 11},
        {8, 9},
        {10, 11}
    };




//Ok now i can get to actually making the icosahedron
    public static ArrayList<Vec3d> constructIcosahedron(double scale, int pointsBetween)
    {
//Total icosahedron to form
        ArrayList<Vec3d> totalIcosahedron = new ArrayList<>();


//Form each vertex
        for(Vec3d vertex : ICOSAHEDRON_VERTICES)
        {
            totalIcosahedron.add(vertex.scale(scale));
        }

//Form each line between vertices
        for(int[] uniqueLine : ICOSAHEDRON_UNIQUE_LINES)
        {
//Get each line's points
            Vec3d startPoint = ICOSAHEDRON_VERTICES[uniqueLine[0]].scale(scale);
            Vec3d endPoint = ICOSAHEDRON_VERTICES[uniqueLine[1]].scale(scale);
//And each line's measurement
            Vec3d lineMeasure = endPoint.subtract(startPoint);


//Points after initial
            int pointsAfterInitial = pointsBetween + 1;
//Distance between points
            Vec3d pointDistance = lineMeasure.scale(1.0D / ((double) pointsAfterInitial));

//Only draw points between vertices (no redundancy)
            for(int pointAt = 1; pointAt < pointsAfterInitial; pointAt++)
            {
//Draw point
                Vec3d drawnPoint = startPoint.add(pointDistance.scale(pointAt));
                totalIcosahedron.add(drawnPoint);
            }
        }


//Return total icosahedron
        return totalIcosahedron;
    }




//Now construct great stellated dodecahedron with the icosahedron
    public static ArrayList<Vec3d> constructGreatStellatedDodecahedron
    (double scale, int pointsBetweenIcoLines,
    double spikeExtent, int pointsBetweenSpikeLines)
    {
//Total great stellated dodecahedron to form
        ArrayList<Vec3d> totalShape = new ArrayList<>();


//Form and add icosahedron
        ArrayList<Vec3d> icosahedronParts = constructIcosahedron(scale, pointsBetweenIcoLines);
        for(Vec3d icoPoint : icosahedronParts) { totalShape.add(icoPoint); }
        

//For each icosahedron face's center
        for(int faceCenterAt = 0; faceCenterAt < 20; faceCenterAt++)
        {
//Get that face center
            Vec3d faceCenter = ICOSAHEDRON_FACE_CENTERS_SORTED[faceCenterAt].scale(scale);

//Extend it by scaled spikeExtent
            Vec3d spikeTip = faceCenter.add(faceCenter.normalize().scale(spikeExtent).scale(scale));
//Add tip to total shape
            totalShape.add(spikeTip);


//Now draw lines
//between spike tip and its pyramid base
            for(int faceVertexAt = 0; faceVertexAt < 3; faceVertexAt++)
            {
//Get each face vertex
                Vec3d faceVertex = ICOSAHEDRON_VERTICES[ICOSAHEDRON_FACE_COMPONENTS[faceCenterAt][faceVertexAt]].scale(scale);
//Get distance between vertex and spike tip
                Vec3d lineToSpike = spikeTip.subtract(faceVertex);


//Points after initial
                int pointsAfterInitial = pointsBetweenSpikeLines + 1;
//Distance between points
                Vec3d pointDistance = lineToSpike.scale(1.0D / ((double) pointsAfterInitial));


//Only draw points between vertices (no redundancy)
                for(int pointAt = 1; pointAt < pointsAfterInitial; pointAt++)
                {
//Draw point
                    Vec3d drawnPoint = faceVertex.add(pointDistance.scale(pointAt));
                    totalShape.add(drawnPoint);
                }
            } 
        }


//Return total great stellated dodecahedron
        return totalShape;
    }




//PS: This order was calced separately and did NOT properly match the face components order
/*
    public static final Vec3d[] ICOSAHEDRON_FACE_CENTERS_OUTDATED = 
    {
//Group 1: Centers with a 0 coordinate and one with 2*PHI + 1
        new Vec3d(PHI / 3.0D, 0.0D, (2.0D * PHI + 1.0D) / 3.0D),
        new Vec3d(-PHI / 3.0D, 0.0D, (2.0D * PHI + 1.0D) / 3.0D),
        new Vec3d(PHI / 3.0D, 0.0D, (-2.0D * PHI - 1.0D) / 3.0D),
        new Vec3d(-PHI / 3.0D, 0.0D, (-2.0D * PHI - 1.0D) / 3.0D),

        new Vec3d(0.0D, (2.0D * PHI + 1.0D) / 3.0D, PHI / 3.0D),
        new Vec3d(0.0D, (2.0D * PHI + 1.0D) / 3.0D, -PHI / 3.0D),
        new Vec3d(0.0D, (-2.0D * PHI - 1.0D) / 3.0D, PHI / 3.0D),
        new Vec3d(0.0D, (-2.0D * PHI - 1.0D) / 3.0D, -PHI / 3.0D),

        new Vec3d((2.0D * PHI + 1.0D) / 3.0D, PHI / 3.0D, 0.0D),
        new Vec3d((-2.0D * PHI - 1.0D) / 3.0D, PHI / 3.0D, 0.0D),
        new Vec3d((2.0D * PHI + 1.0D) / 3.0D, -PHI / 3.0D, 0.0D),
        new Vec3d((-2.0D * PHI - 1.0D) / 3.0D, -PHI / 3.0D, 0.0D),

//Group 2: Centers with every coordinate equal to ±(PHI + 1.0D)
        new Vec3d((PHI + 1.0D) / 3.0D, (PHI + 1.0D) / 3.0D, (PHI + 1.0D) / 3.0D),
        new Vec3d((-PHI - 1.0D) / 3.0D, (PHI + 1.0D) / 3.0D, (PHI + 1.0D) / 3.0D),
        new Vec3d((PHI + 1.0D) / 3.0D, (PHI + 1.0D) / 3.0D, (-PHI - 1.0D) / 3.0D),
        new Vec3d((-PHI - 1.0D) / 3.0D, (PHI + 1.0D) / 3.0D, (-PHI - 1.0D) / 3.0D),
        new Vec3d((PHI + 1.0D) / 3.0D, (-PHI - 1.0D) / 3.0D, (PHI + 1.0D) / 3.0D),
        new Vec3d((-PHI - 1.0D) / 3.0D, (-PHI - 1.0D) / 3.0D, (PHI + 1.0D) / 3.0D),
        new Vec3d((PHI + 1.0D) / 3.0D, (-PHI - 1.0D) / 3.0D, (-PHI - 1.0D) / 3.0D),
        new Vec3d((-PHI - 1.0D) / 3.0D, (-PHI - 1.0D) / 3.0D, (-PHI - 1.0D) / 3.0D)
    };
*/




}
