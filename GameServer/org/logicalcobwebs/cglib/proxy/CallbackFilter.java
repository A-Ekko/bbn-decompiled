package org.logicalcobwebs.cglib.proxy;

import java.lang.reflect.Method;

public abstract interface CallbackFilter
{
  public abstract int accept(Method paramMethod);

  public abstract boolean equals(Object paramObject);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.CallbackFilter
 * JD-Core Version:    0.6.0
 */