package org.sporotofpoorety.eternitymode.util;




public final class ArcUtil 
{

//Vertical velocity to land on target
    public static double verticalVelocityForArc(double targetHeightDiff, double intendedAirTime, double gravitySpeed)  
    {
        return (targetHeightDiff / intendedAirTime) 
            - (gravitySpeed * ((intendedAirTime - 1.0D) / 2.0D));
    }

}
