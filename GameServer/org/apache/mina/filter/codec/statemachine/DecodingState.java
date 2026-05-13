package org.apache.mina.filter.codec.statemachine;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public abstract interface DecodingState
{
  public abstract DecodingState decode(IoBuffer paramIoBuffer, ProtocolDecoderOutput paramProtocolDecoderOutput)
    throws Exception;

  public abstract DecodingState finishDecode(ProtocolDecoderOutput paramProtocolDecoderOutput)
    throws Exception;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.statemachine.DecodingState
 * JD-Core Version:    0.6.0
 */