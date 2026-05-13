package org.logicalcobwebs.concurrent;

public abstract interface ReadWriteLock
{
  public abstract Sync readLock();

  public abstract Sync writeLock();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.concurrent.ReadWriteLock
 * JD-Core Version:    0.6.0
 */