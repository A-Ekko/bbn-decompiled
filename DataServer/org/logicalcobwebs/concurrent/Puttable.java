package org.logicalcobwebs.concurrent;

public abstract interface Puttable
{
  public abstract void put(Object paramObject)
    throws InterruptedException;

  public abstract boolean offer(Object paramObject, long paramLong)
    throws InterruptedException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.concurrent.Puttable
 * JD-Core Version:    0.6.0
 */