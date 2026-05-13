package org.apache.mina.filter.codec;

import org.apache.mina.core.session.IoSession;

public abstract class ProtocolDecoderAdapter
  implements ProtocolDecoder
{
  public void finishDecode(IoSession session, ProtocolDecoderOutput out)
    throws Exception
  {
  }

  public void dispose(IoSession session)
    throws Exception
  {
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.ProtocolDecoderAdapter
 * JD-Core Version:    0.6.0
 */