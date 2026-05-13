package org.apache.mina.core.future;

public abstract interface ReadFuture extends IoFuture
{
  public abstract Object getMessage();

  public abstract boolean isRead();

  public abstract boolean isClosed();

  public abstract Throwable getException();

  public abstract void setRead(Object paramObject);

  public abstract void setClosed();

  public abstract void setException(Throwable paramThrowable);

  public abstract ReadFuture await()
    throws InterruptedException;

  public abstract ReadFuture awaitUninterruptibly();

  public abstract ReadFuture addListener(IoFutureListener<?> paramIoFutureListener);

  public abstract ReadFuture removeListener(IoFutureListener<?> paramIoFutureListener);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.future.ReadFuture
 * JD-Core Version:    0.6.0
 */