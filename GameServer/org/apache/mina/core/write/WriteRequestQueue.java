package org.apache.mina.core.write;

import org.apache.mina.core.session.IoSession;

public abstract interface WriteRequestQueue
{
  public abstract WriteRequest poll(IoSession paramIoSession);

  public abstract void offer(IoSession paramIoSession, WriteRequest paramWriteRequest);

  public abstract boolean isEmpty(IoSession paramIoSession);

  public abstract void clear(IoSession paramIoSession);

  public abstract void dispose(IoSession paramIoSession);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.write.WriteRequestQueue
 * JD-Core Version:    0.6.0
 */