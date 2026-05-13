package org.apache.mina.core.session;

import org.apache.mina.core.future.IoFuture;

public abstract interface IoSessionInitializer<T extends IoFuture>
{
  public abstract void initializeSession(IoSession paramIoSession, T paramT);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.session.IoSessionInitializer
 * JD-Core Version:    0.6.0
 */