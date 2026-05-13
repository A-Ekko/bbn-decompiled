package org.logicalcobwebs.concurrent;

public abstract interface Takable
{
  public abstract Object take()
    throws InterruptedException;

  public abstract Object poll(long paramLong)
    throws InterruptedException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.concurrent.Takable
 * JD-Core Version:    0.6.0
 */