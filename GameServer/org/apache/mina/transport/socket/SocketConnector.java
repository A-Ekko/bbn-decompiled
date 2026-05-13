package org.apache.mina.transport.socket;

import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoConnector;

public abstract interface SocketConnector extends IoConnector
{
  public abstract InetSocketAddress getDefaultRemoteAddress();

  public abstract void setDefaultRemoteAddress(InetSocketAddress paramInetSocketAddress);

  public abstract SocketSessionConfig getSessionConfig();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.transport.socket.SocketConnector
 * JD-Core Version:    0.6.0
 */