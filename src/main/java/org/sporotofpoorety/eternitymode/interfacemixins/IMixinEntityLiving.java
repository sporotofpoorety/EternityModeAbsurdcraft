package org.sporotofpoorety.eternitymode.interfacemixins;


import java.util.concurrent.ConcurrentLinkedQueue;


import org.sporotofpoorety.eternitymode.util.QueuedActionAtPos;


public interface IMixinEntityLiving
{

    public void queuedActionsIterate();
    public void queuedActionBefore(QueuedActionAtPos queuedAction);
    public void queuedActionExecute(QueuedActionAtPos queuedAction);
    public void queuedActionsEmptyLogic();


    public int getRealTicksExisted();
    public boolean getAbsurdcraftStunned();
    public boolean getAbsurdcraftStunnedInitial();
    public int getAbsurdcraftStunnedTimer();
    public ConcurrentLinkedQueue<QueuedActionAtPos> getQueuedActions();


    public void setRealTicksExisted(int realTicks);
    public void duringAbsurdcraftStunned();
    public void duringAbsurdcraftStunnedExtra();
    public void onLoseAbsurdcraftStunned();
    public void onAbsurdcraftStunned();
    public void onAbsurdcraftStunnedExtra();
    public void setAbsurdcraftStunned(boolean isStunned);
    public void setAbsurdcraftStunnedInitial(boolean isStunnedInitial);
    public void setAbsurdcraftStunnedTimer(int time);
    public void setAbsurdcraftStunned(boolean isStunned, int time);
    public void addQueuedAction(QueuedActionAtPos queuedAction);
}
