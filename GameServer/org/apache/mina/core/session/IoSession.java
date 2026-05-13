package org.apache.mina.core.session;

import java.net.SocketAddress;
import java.util.Set;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;

public abstract interface IoSession
{
  public abstract long getId();

  public abstract IoService getService();

  public abstract IoHandler getHandler();

  public abstract IoSessionConfig getConfig();

  public abstract IoFilterChain getFilterChain();

  public abstract WriteRequestQueue getWriteRequestQueue();

  public abstract TransportMetadata getTransportMetadata();

  public abstract ReadFuture read();

  public abstract WriteFuture write(Object paramObject);

  public abstract WriteFuture write(Object paramObject, SocketAddress paramSocketAddress);

  public abstract CloseFuture close(boolean paramBoolean);

  @Deprecated
  public abstract CloseFuture close();

  @Deprecated
  public abstract Object getAttachment();

  @Deprecated
  public abstract Object setAttachment(Object paramObject);

  public abstract Object getAttribute(Object paramObject);

  public abstract Object getAttribute(Object paramObject1, Object paramObject2);

  public abstract Object setAttribute(Object paramObject1, Object paramObject2);

  public abstract Object setAttribute(Object paramObject);

  public abstract Object setAttributeIfAbsent(Object paramObject1, Object paramObject2);

  public abstract Object setAttributeIfAbsent(Object paramObject);

  public abstract Object removeAttribute(Object paramObject);

  public abstract boolean removeAttribute(Object paramObject1, Object paramObject2);

  public abstract boolean replaceAttribute(Object paramObject1, Object paramObject2, Object paramObject3);

  public abstract boolean containsAttribute(Object paramObject);

  public abstract Set<Object> getAttributeKeys();

  public abstract boolean isConnected();

  public abstract boolean isClosing();

  public abstract CloseFuture getCloseFuture();

  public abstract SocketAddress getRemoteAddress();

  public abstract SocketAddress getLocalAddress();

  public abstract SocketAddress getServiceAddress();

  public abstract void setCurrentWriteRequest(WriteRequest paramWriteRequest);

  public abstract void suspendRead();

  public abstract void suspendWrite();

  public abstract void resumeRead();

  public abstract void resumeWrite();

  public abstract boolean isReadSuspended();

  public abstract boolean isWriteSuspended();

  public abstract void updateThroughput(long paramLong, boolean paramBoolean);

  public abstract long getReadBytes();

  public abstract long getWrittenBytes();

  public abstract long getReadMessages();

  public abstract long getWrittenMessages();

  public abstract double getReadBytesThroughput();

  public abstract double getWrittenBytesThroughput();

  public abstract double getReadMessagesThroughput();

  public abstract double getWrittenMessagesThroughput();

  public abstract int getScheduledWriteMessages();

  public abstract long getScheduledWriteBytes();

  public abstract Object getCurrentWriteMessage();

  public abstract WriteRequest getCurrentWriteRequest();

  public abstract long getCreationTime();

  public abstract long getLastIoTime();

  public abstract long getLastReadTime();

  public abstract long getLastWriteTime();

  public abstract boolean isIdle(IdleStatus paramIdleStatus);

  public abstract boolean isReaderIdle();

  public abstract boolean isWriterIdle();

  public abstract boolean isBothIdle();

  public abstract int getIdleCount(IdleStatus paramIdleStatus);

  public abstract int getReaderIdleCount();

  public abstract int getWriterIdleCount();

  public abstract int getBothIdleCount();

  public abstract long getLastIdleTime(IdleStatus paramIdleStatus);

  public abstract long getLastReaderIdleTime();

  public abstract long getLastWriterIdleTime();

  public abstract long getLastBothIdleTime();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.session.IoSession
 * JD-Core Version:    0.6.0
 */