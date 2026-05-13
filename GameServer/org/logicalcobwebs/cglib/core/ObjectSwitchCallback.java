package org.logicalcobwebs.cglib.core;

import org.logicalcobwebs.cglib.asm.Label;

public abstract interface ObjectSwitchCallback
{
  public abstract void processCase(Object paramObject, Label paramLabel)
    throws Exception;

  public abstract void processDefault()
    throws Exception;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.ObjectSwitchCallback
 * JD-Core Version:    0.6.0
 */