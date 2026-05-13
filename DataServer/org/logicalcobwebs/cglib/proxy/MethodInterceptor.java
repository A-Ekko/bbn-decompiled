package org.logicalcobwebs.cglib.proxy;

import java.lang.reflect.Method;

public abstract interface MethodInterceptor extends Callback
{
  public abstract Object intercept(Object paramObject, Method paramMethod, Object[] paramArrayOfObject, MethodProxy paramMethodProxy)
    throws Throwable;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.MethodInterceptor
 * JD-Core Version:    0.6.0
 */