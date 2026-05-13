package org.apache.mina.filter.codec;

import org.apache.mina.core.session.IoSession;

public abstract class ProtocolEncoderAdapter
  implements ProtocolEncoder
{
  public void dispose(IoSession session)
    throws Exception
  {
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.ProtocolEncoderAdapter
 * JD-Core Version:    0.6.0
 */