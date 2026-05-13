package org.logicalcobwebs.cglib.proxy;

import java.lang.reflect.Method;

public abstract interface InvocationHandler extends Callback
{
  public abstract Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.InvocationHandler
 * JD-Core Version:    0.6.0
 */