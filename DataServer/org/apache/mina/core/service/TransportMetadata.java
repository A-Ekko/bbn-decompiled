package org.apache.mina.core.service;

import java.net.SocketAddress;
import java.util.Set;
import org.apache.mina.core.session.IoSessionConfig;

public abstract interface TransportMetadata
{
  public abstract String getProviderName();

  public abstract String getName();

  public abstract boolean isConnectionless();

  public abstract boolean hasFragmentation();

  public abstract Class<? extends SocketAddress> getAddressType();

  public abstract Set<Class<? extends Object>> getEnvelopeTypes();

  public abstract Class<? extends IoSessionConfig> getSessionConfigType();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.service.TransportMetadata
 * JD-Core Version:    0.6.0
 */