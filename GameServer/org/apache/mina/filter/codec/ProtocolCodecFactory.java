package org.apache.mina.filter.codec;

import org.apache.mina.core.session.IoSession;

public abstract interface ProtocolCodecFactory
{
  public abstract ProtocolEncoder getEncoder(IoSession paramIoSession)
    throws Exception;

  public abstract ProtocolDecoder getDecoder(IoSession paramIoSession)
    throws Exception;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.ProtocolCodecFactory
 * JD-Core Version:    0.6.0
 */