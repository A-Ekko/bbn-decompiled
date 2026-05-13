package org.apache.mina.core.future;

public abstract interface CloseFuture extends IoFuture
{
  public abstract boolean isClosed();

  public abstract void setClosed();

  public abstract CloseFuture await()
    throws InterruptedException;

  public abstract CloseFuture awaitUninterruptibly();

  public abstract CloseFuture addListener(IoFutureListener<?> paramIoFutureListener);

  public abstract CloseFuture removeListener(IoFutureListener<?> paramIoFutureListener);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.future.CloseFuture
 * JD-Core Version:    0.6.0
 */