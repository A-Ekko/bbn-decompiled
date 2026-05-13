package org.apache.mina.transport.socket;

import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSessionRecycler;

public abstract interface DatagramAcceptor extends IoAcceptor
{
  public abstract InetSocketAddress getLocalAddress();

  public abstract InetSocketAddress getDefaultLocalAddress();

  public abstract void setDefaultLocalAddress(InetSocketAddress paramInetSocketAddress);

  public abstract IoSessionRecycler getSessionRecycler();

  public abstract void setSessionRecycler(IoSessionRecycler paramIoSessionRecycler);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.transport.socket.DatagramAcceptor
 * JD-Core Version:    0.6.0
 */