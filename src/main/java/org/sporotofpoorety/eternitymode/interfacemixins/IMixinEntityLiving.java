package org.sporotofpoorety.eternitymode.interfacemixins;

import net.minecraft.network.datasync.DataParameter;

public interface IMixinEntityLiving
{
    public boolean getAbsurdcraftStunned();
    public int getAbsurdcraftStunnedTimer();

    public void setAbsurdcraftStunned(boolean isStunned);
    public void setAbsurdcraftStunnedTimer(int time);
}
