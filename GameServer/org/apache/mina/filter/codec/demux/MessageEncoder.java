package org.apache.mina.filter.codec.demux;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public abstract interface MessageEncoder<T>
{
  public abstract void encode(IoSession paramIoSession, T paramT, ProtocolEncoderOutput paramProtocolEncoderOutput)
    throws Exception;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.demux.MessageEncoder
 * JD-Core Version:    0.6.0
 */