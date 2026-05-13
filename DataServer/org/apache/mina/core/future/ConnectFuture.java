package org.apache.mina.core.future;

import org.apache.mina.core.session.IoSession;

public abstract interface ConnectFuture extends IoFuture
{
  public abstract IoSession getSession();

  public abstract Throwable getException();

  public abstract boolean isConnected();

  public abstract boolean isCanceled();

  public abstract void setSession(IoSession paramIoSession);

  public abstract void setException(Throwable paramThrowable);

  public abstract void cancel();

  public abstract ConnectFuture await()
    throws InterruptedException;

  public abstract ConnectFuture awaitUninterruptibly();

  public abstract ConnectFuture addListener(IoFutureListener<?> paramIoFutureListener);

  public abstract ConnectFuture removeListener(IoFutureListener<?> paramIoFutureListener);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.future.ConnectFuture
 * JD-Core Version:    0.6.0
 */