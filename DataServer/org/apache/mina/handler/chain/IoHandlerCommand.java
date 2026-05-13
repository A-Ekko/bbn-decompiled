package org.apache.mina.handler.chain;

import org.apache.mina.core.session.IoSession;

public abstract interface IoHandlerCommand
{
  public abstract void execute(NextCommand paramNextCommand, IoSession paramIoSession, Object paramObject)
    throws Exception;

  public static abstract interface NextCommand
  {
    public abstract void execute(IoSession paramIoSession, Object paramObject)
      throws Exception;
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.handler.chain.IoHandlerCommand
 * JD-Core Version:    0.6.0
 */