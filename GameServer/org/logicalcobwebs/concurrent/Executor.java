package org.logicalcobwebs.concurrent;

public abstract interface Executor
{
  public abstract void execute(Runnable paramRunnable)
    throws InterruptedException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.concurrent.Executor
 * JD-Core Version:    0.6.0
 */