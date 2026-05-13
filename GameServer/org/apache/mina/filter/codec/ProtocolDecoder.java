package org.apache.mina.filter.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

public abstract interface ProtocolDecoder
{
  public abstract void decode(IoSession paramIoSession, IoBuffer paramIoBuffer, ProtocolDecoderOutput paramProtocolDecoderOutput)
    throws Exception;

  public abstract void finishDecode(IoSession paramIoSession, ProtocolDecoderOutput paramProtocolDecoderOutput)
    throws Exception;

  public abstract void dispose(IoSession paramIoSession)
    throws Exception;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.ProtocolDecoder
 * JD-Core Version:    0.6.0
 */