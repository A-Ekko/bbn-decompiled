package org.logicalcobwebs.concurrent;

public abstract interface Channel extends Puttable, Takable
{
  public abstract void put(Object paramObject)
    throws InterruptedException;

  public abstract boolean offer(Object paramObject, long paramLong)
    throws InterruptedException;

  public abstract Object take()
    throws InterruptedException;

  public abstract Object poll(long paramLong)
    throws InterruptedException;

  public abstract Object peek();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.concurrent.Channel
 * JD-Core Version:    0.6.0
 */