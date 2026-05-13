package org.apache.mina.filter.codec;

import org.apache.mina.core.session.IoSession;

public abstract interface ProtocolEncoder
{
  public abstract void encode(IoSession paramIoSession, Object paramObject, ProtocolEncoderOutput paramProtocolEncoderOutput)
    throws Exception;

  public abstract void dispose(IoSession paramIoSession)
    throws Exception;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.ProtocolEncoder
 * JD-Core Version:    0.6.0
 */