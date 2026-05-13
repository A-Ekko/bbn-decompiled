package org.apache.mina.core.service;

import java.util.Map;
import java.util.Set;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.session.IoSessionDataStructureFactory;

public abstract interface IoService
{
  public abstract TransportMetadata getTransportMetadata();

  public abstract void addListener(IoServiceListener paramIoServiceListener);

  public abstract void removeListener(IoServiceListener paramIoServiceListener);

  public abstract boolean isDisposing();

  public abstract boolean isDisposed();

  public abstract void dispose();

  public abstract IoHandler getHandler();

  public abstract void setHandler(IoHandler paramIoHandler);

  public abstract Map<Long, IoSession> getManagedSessions();

  public abstract int getManagedSessionCount();

  public abstract IoSessionConfig getSessionConfig();

  public abstract IoFilterChainBuilder getFilterChainBuilder();

  public abstract void setFilterChainBuilder(IoFilterChainBuilder paramIoFilterChainBuilder);

  public abstract DefaultIoFilterChainBuilder getFilterChain();

  public abstract boolean isActive();

  public abstract long getActivationTime();

  public abstract Set<WriteFuture> broadcast(Object paramObject);

  public abstract IoSessionDataStructureFactory getSessionDataStructureFactory();

  public abstract void setSessionDataStructureFactory(IoSessionDataStructureFactory paramIoSessionDataStructureFactory);

  public abstract int getScheduledWriteBytes();

  public abstract int getScheduledWriteMessages();

  public abstract IoServiceStatistics getStatistics();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.service.IoService
 * JD-Core Version:    0.6.0
 */