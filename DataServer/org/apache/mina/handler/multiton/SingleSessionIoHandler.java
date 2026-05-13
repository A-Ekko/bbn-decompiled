package org.apache.mina.handler.multiton;

import org.apache.mina.core.session.IdleStatus;

public abstract interface SingleSessionIoHandler
{
  public abstract void sessionCreated()
    throws Exception;

  public abstract void sessionOpened()
    throws Exception;

  public abstract void sessionClosed()
    throws Exception;

  public abstract void sessionIdle(IdleStatus paramIdleStatus)
    throws Exception;

  public abstract void exceptionCaught(Throwable paramThrowable)
    throws Exception;

  public abstract void messageReceived(Object paramObject)
    throws Exception;

  public abstract void messageSent(Object paramObject)
    throws Exception;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.handler.multiton.SingleSessionIoHandler
 * JD-Core Version:    0.6.0
 */