package org.sporotofpoorety.eternitymode.entity.ai;


import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;




//Limiting this to players for max performance
public class EntityAIRelentlessTargetPlayers extends EntityAIBase 
{

    public final EntityLiving attacker;

    public final double horizontalRangeSq;
//Can be set to -1 to be infinite
//  protected final double verticalRange; 

    public EntityPlayer targetPlayer;




    public EntityAIRelentlessTargetPlayers(EntityLiving attacker, double horizontalRange) //double verticalRange) 
    {
        this.attacker = attacker;
//Auto square
        this.horizontalRangeSq = (horizontalRange * horizontalRange);
//      this.verticalRange = verticalRange;
        this.setMutexBits(1);
    }




//Conditions to start searching for targets
    @Override
    public boolean shouldExecute() 
    {
//Only every 20 ticks
        if(this.attacker.ticksExisted % 20 != 0) { return false; }

//Current closest candidate
        EntityPlayer nearestCandidate = null;
//Current nearest distance
        double bestDistSq = Double.MAX_VALUE;


//Iterate on existing players
        for (EntityPlayer playerCandidate : attacker.world.playerEntities)
        {
//Player has to meet conditions
            if(!(this.targetPlayerConditions(playerCandidate))) { continue; }

//Get target vertical distance
//          double verticalDistance = Math.abs(playerCandidate.posY - attacker.posY);
//Then check
//          if (verticalRange >= 0 && verticalDistance > verticalRange) { continue; }

//Get target overall distanceSq
            double horizontalDistanceSq = ((playerCandidate.posX - attacker.posX) * (playerCandidate.posX - attacker.posX))
                + ((playerCandidate.posZ - attacker.posZ) * (playerCandidate.posZ - attacker.posZ)); 
//          double distSq = attacker.getDistanceSq(playerCandidate);

//If candidate within horizontal range
            if (horizontalDistanceSq <= (horizontalRangeSq)
//And current closest evaluated
            && horizontalDistanceSq < bestDistSq) 
            {
//Set to closest evaluated distance
                bestDistSq = horizontalDistanceSq;
//Set to closest evaluated candidate
                nearestCandidate = (EntityPlayer) playerCandidate;
            }
        }


//If any candidate got selected
        if (nearestCandidate != null) 
        {
//Set target to nearest candidate
            this.targetPlayer = nearestCandidate;
//And return true
            return true;
        }


//Else return false
        return false;
    }




//If targeting executes
    @Override
    public void startExecuting() 
    {
//Set attacker target
        attacker.setAttackTarget(targetPlayer);
    }




//Conditions to continue targeting
    @Override
    public boolean shouldContinueExecuting()
    {
        if(!this.targetPlayerConditions(this.targetPlayer)) { return false; }
//Already null-guarded now periodically check target distance
        if((attacker.ticksExisted % 20 == 0)
        && (horizontalRangeSq < (targetPlayer.posX - attacker.posX) * (targetPlayer.posX - attacker.posX)
            + (targetPlayer.posZ - attacker.posZ) * (targetPlayer.posZ - attacker.posZ)))
        { return false; }
        

        return true;
    }




//On task reset clear both task and owner's targets
    @Override
    public void resetTask()
    {
        this.attacker.setAttackTarget((EntityLivingBase) null);
        this.targetPlayer = null;
    }




    public boolean targetPlayerConditions(EntityPlayer target)
    {   
//If target null or dead or creative mode or same team
        if(target == null
        || !target.isEntityAlive()
        || target.capabilities.disableDamage
        || attacker.isOnSameTeam(target))
        {
//Don't target
            return false;
        }

        return true;
    }
}

