package org.apache.mina.handler.multiton;

import org.apache.mina.core.session.IoSession;

public abstract interface SingleSessionIoHandlerFactory
{
  public abstract SingleSessionIoHandler getHandler(IoSession paramIoSession)
    throws Exception;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.handler.multiton.SingleSessionIoHandlerFactory
 * JD-Core Version:    0.6.0
 */