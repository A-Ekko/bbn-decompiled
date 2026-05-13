package org.apache.mina.filter.codec;

import org.apache.mina.core.future.WriteFuture;

public abstract interface ProtocolEncoderOutput
{
  public abstract void write(Object paramObject);

  public abstract void mergeAll();

  public abstract WriteFuture flush();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.ProtocolEncoderOutput
 * JD-Core Version:    0.6.0
 */