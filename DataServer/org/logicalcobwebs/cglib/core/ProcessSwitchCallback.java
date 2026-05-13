package org.logicalcobwebs.cglib.core;

import org.logicalcobwebs.cglib.asm.Label;

public abstract interface ProcessSwitchCallback
{
  public abstract void processCase(int paramInt, Label paramLabel)
    throws Exception;

  public abstract void processDefault()
    throws Exception;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.ProcessSwitchCallback
 * JD-Core Version:    0.6.0
 */