package org.apache.mina.core.filterchain;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

public abstract interface IoFilter
{
  public abstract void init()
    throws Exception;

  public abstract void destroy()
    throws Exception;

  public abstract void onPreAdd(IoFilterChain paramIoFilterChain, String paramString, NextFilter paramNextFilter)
    throws Exception;

  public abstract void onPostAdd(IoFilterChain paramIoFilterChain, String paramString, NextFilter paramNextFilter)
    throws Exception;

  public abstract void onPreRemove(IoFilterChain paramIoFilterChain, String paramString, NextFilter paramNextFilter)
    throws Exception;

  public abstract void onPostRemove(IoFilterChain paramIoFilterChain, String paramString, NextFilter paramNextFilter)
    throws Exception;

  public abstract void sessionCreated(NextFilter paramNextFilter, IoSession paramIoSession)
    throws Exception;

  public abstract void sessionOpened(NextFilter paramNextFilter, IoSession paramIoSession)
    throws Exception;

  public abstract void sessionClosed(NextFilter paramNextFilter, IoSession paramIoSession)
    throws Exception;

  public abstract void sessionIdle(NextFilter paramNextFilter, IoSession paramIoSession, IdleStatus paramIdleStatus)
    throws Exception;

  public abstract void exceptionCaught(NextFilter paramNextFilter, IoSession paramIoSession, Throwable paramThrowable)
    throws Exception;

  public abstract void messageReceived(NextFilter paramNextFilter, IoSession paramIoSession, Object paramObject)
    throws Exception;

  public abstract void messageSent(NextFilter paramNextFilter, IoSession paramIoSession, WriteRequest paramWriteRequest)
    throws Exception;

  public abstract void filterClose(NextFilter paramNextFilter, IoSession paramIoSession)
    throws Exception;

  public abstract void filterWrite(NextFilter paramNextFilter, IoSession paramIoSession, WriteRequest paramWriteRequest)
    throws Exception;

  public static abstract interface NextFilter
  {
    public abstract void sessionCreated(IoSession paramIoSession);

    public abstract void sessionOpened(IoSession paramIoSession);

    public abstract void sessionClosed(IoSession paramIoSession);

    public abstract void sessionIdle(IoSession paramIoSession, IdleStatus paramIdleStatus);

    public abstract void exceptionCaught(IoSession paramIoSession, Throwable paramThrowable);

    public abstract void messageReceived(IoSession paramIoSession, Object paramObject);

    public abstract void messageSent(IoSession paramIoSession, WriteRequest paramWriteRequest);

    public abstract void filterWrite(IoSession paramIoSession, WriteRequest paramWriteRequest);

    public abstract void filterClose(IoSession paramIoSession);
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.filterchain.IoFilter
 * JD-Core Version:    0.6.0
 */