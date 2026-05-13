package org.apache.mina.filter.codec.demux;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public abstract class MessageDecoderAdapter
  implements MessageDecoder
{
  public void finishDecode(IoSession session, ProtocolDecoderOutput out)
    throws Exception
  {
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.demux.MessageDecoderAdapter
 * JD-Core Version:    0.6.0
 */