package org.logicalcobwebs.cglib.transform.impl;

import org.logicalcobwebs.cglib.asm.Type;

public abstract interface InterceptFieldFilter
{
  public abstract boolean acceptRead(Type paramType, String paramString);

  public abstract boolean acceptWrite(Type paramType, String paramString);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.impl.InterceptFieldFilter
 * JD-Core Version:    0.6.0
 */