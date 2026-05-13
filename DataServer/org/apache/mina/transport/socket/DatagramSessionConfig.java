package org.apache.mina.transport.socket;

import org.apache.mina.core.session.IoSessionConfig;

public abstract interface DatagramSessionConfig extends IoSessionConfig
{
  public abstract boolean isBroadcast();

  public abstract void setBroadcast(boolean paramBoolean);

  public abstract boolean isReuseAddress();

  public abstract void setReuseAddress(boolean paramBoolean);

  public abstract int getReceiveBufferSize();

  public abstract void setReceiveBufferSize(int paramInt);

  public abstract int getSendBufferSize();

  public abstract void setSendBufferSize(int paramInt);

  public abstract int getTrafficClass();

  public abstract void setTrafficClass(int paramInt);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.transport.socket.DatagramSessionConfig
 * JD-Core Version:    0.6.0
 */