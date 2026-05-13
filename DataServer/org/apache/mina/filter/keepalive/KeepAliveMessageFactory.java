package org.apache.mina.filter.keepalive;

import org.apache.mina.core.session.IoSession;

public abstract interface KeepAliveMessageFactory
{
  public abstract boolean isRequest(IoSession paramIoSession, Object paramObject);

  public abstract boolean isResponse(IoSession paramIoSession, Object paramObject);

  public abstract Object getRequest(IoSession paramIoSession);

  public abstract Object getResponse(IoSession paramIoSession, Object paramObject);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.keepalive.KeepAliveMessageFactory
 * JD-Core Version:    0.6.0
 */