package org.logicalcobwebs.cglib.proxy;

public abstract interface LazyLoader extends Callback
{
  public abstract Object loadObject()
    throws Exception;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.LazyLoader
 * JD-Core Version:    0.6.0
 */