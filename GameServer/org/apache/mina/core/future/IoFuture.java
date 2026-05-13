package org.apache.mina.core.future;

import java.util.concurrent.TimeUnit;
import org.apache.mina.core.session.IoSession;

public abstract interface IoFuture
{
  public abstract IoSession getSession();

  public abstract IoFuture await()
    throws InterruptedException;

  public abstract boolean await(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException;

  public abstract boolean await(long paramLong)
    throws InterruptedException;

  public abstract IoFuture awaitUninterruptibly();

  public abstract boolean awaitUninterruptibly(long paramLong, TimeUnit paramTimeUnit);

  public abstract boolean awaitUninterruptibly(long paramLong);

  @Deprecated
  public abstract void join();

  @Deprecated
  public abstract boolean join(long paramLong);

  public abstract boolean isDone();

  public abstract IoFuture addListener(IoFutureListener<?> paramIoFutureListener);

  public abstract IoFuture removeListener(IoFutureListener<?> paramIoFutureListener);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.future.IoFuture
 * JD-Core Version:    0.6.0
 */