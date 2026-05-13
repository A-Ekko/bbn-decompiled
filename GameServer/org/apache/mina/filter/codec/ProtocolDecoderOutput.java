package org.apache.mina.filter.codec;

import org.apache.mina.core.filterchain.IoFilter.NextFilter;
import org.apache.mina.core.session.IoSession;

public abstract interface ProtocolDecoderOutput
{
  public abstract void write(Object paramObject);

  public abstract void flush(IoFilter.NextFilter paramNextFilter, IoSession paramIoSession);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.ProtocolDecoderOutput
 * JD-Core Version:    0.6.0
 */