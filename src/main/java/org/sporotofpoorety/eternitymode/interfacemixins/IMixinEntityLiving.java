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
    public int getAbsurdcraftStunnedTimer();
    public ConcurrentLinkedQueue<QueuedActionAtPos> getQueuedActions();


    public void setRealTicksExisted(int realTicks);
    public void setAbsurdcraftStunned(boolean isStunned);
    public void setAbsurdcraftStunnedTimer(int time);
    public void addQueuedAction(QueuedActionAtPos queuedAction);
}
