package org.apache.mina.transport.socket;

import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoAcceptor;

public abstract interface SocketAcceptor extends IoAcceptor
{
  public abstract InetSocketAddress getLocalAddress();

  public abstract InetSocketAddress getDefaultLocalAddress();

  public abstract void setDefaultLocalAddress(InetSocketAddress paramInetSocketAddress);

  public abstract boolean isReuseAddress();

  public abstract void setReuseAddress(boolean paramBoolean);

  public abstract int getBacklog();

  public abstract void setBacklog(int paramInt);

  public abstract SocketSessionConfig getSessionConfig();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.transport.socket.SocketAcceptor
 * JD-Core Version:    0.6.0
 */