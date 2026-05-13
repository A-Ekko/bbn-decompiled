package org.apache.mina.core.future;

public abstract interface WriteFuture extends IoFuture
{
  public abstract boolean isWritten();

  public abstract Throwable getException();

  public abstract void setWritten();

  public abstract void setException(Throwable paramThrowable);

  public abstract WriteFuture await()
    throws InterruptedException;

  public abstract WriteFuture awaitUninterruptibly();

  public abstract WriteFuture addListener(IoFutureListener<?> paramIoFutureListener);

  public abstract WriteFuture removeListener(IoFutureListener<?> paramIoFutureListener);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.future.WriteFuture
 * JD-Core Version:    0.6.0
 */