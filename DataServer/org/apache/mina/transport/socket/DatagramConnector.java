package org.apache.mina.transport.socket;

import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoConnector;

public abstract interface DatagramConnector extends IoConnector
{
  public abstract InetSocketAddress getDefaultRemoteAddress();

  public abstract void setDefaultRemoteAddress(InetSocketAddress paramInetSocketAddress);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.transport.socket.DatagramConnector
 * JD-Core Version:    0.6.0
 */