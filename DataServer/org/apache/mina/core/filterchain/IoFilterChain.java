package org.apache.mina.core.filterchain;

import java.util.List;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

public abstract interface IoFilterChain
{
  public abstract IoSession getSession();

  public abstract Entry getEntry(String paramString);

  public abstract Entry getEntry(IoFilter paramIoFilter);

  public abstract Entry getEntry(Class<? extends IoFilter> paramClass);

  public abstract IoFilter get(String paramString);

  public abstract IoFilter get(Class<? extends IoFilter> paramClass);

  public abstract IoFilter.NextFilter getNextFilter(String paramString);

  public abstract IoFilter.NextFilter getNextFilter(IoFilter paramIoFilter);

  public abstract IoFilter.NextFilter getNextFilter(Class<? extends IoFilter> paramClass);

  public abstract List<Entry> getAll();

  public abstract List<Entry> getAllReversed();

  public abstract boolean contains(String paramString);

  public abstract boolean contains(IoFilter paramIoFilter);

  public abstract boolean contains(Class<? extends IoFilter> paramClass);

  public abstract void addFirst(String paramString, IoFilter paramIoFilter);

  public abstract void addLast(String paramString, IoFilter paramIoFilter);

  public abstract void addBefore(String paramString1, String paramString2, IoFilter paramIoFilter);

  public abstract void addAfter(String paramString1, String paramString2, IoFilter paramIoFilter);

  public abstract IoFilter replace(String paramString, IoFilter paramIoFilter);

  public abstract void replace(IoFilter paramIoFilter1, IoFilter paramIoFilter2);

  public abstract IoFilter replace(Class<? extends IoFilter> paramClass, IoFilter paramIoFilter);

  public abstract IoFilter remove(String paramString);

  public abstract void remove(IoFilter paramIoFilter);

  public abstract IoFilter remove(Class<? extends IoFilter> paramClass);

  public abstract void clear()
    throws Exception;

  public abstract void fireSessionCreated();

  public abstract void fireSessionOpened();

  public abstract void fireSessionClosed();

  public abstract void fireSessionIdle(IdleStatus paramIdleStatus);

  public abstract void fireMessageReceived(Object paramObject);

  public abstract void fireMessageSent(WriteRequest paramWriteRequest);

  public abstract void fireExceptionCaught(Throwable paramThrowable);

  public abstract void fireFilterWrite(WriteRequest paramWriteRequest);

  public abstract void fireFilterClose();

  public static abstract interface Entry
  {
    public abstract String getName();

    public abstract IoFilter getFilter();

    public abstract IoFilter.NextFilter getNextFilter();

    public abstract void addBefore(String paramString, IoFilter paramIoFilter);

    public abstract void addAfter(String paramString, IoFilter paramIoFilter);

    public abstract void replace(IoFilter paramIoFilter);

    public abstract void remove();
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.filterchain.IoFilterChain
 * JD-Core Version:    0.6.0
 */