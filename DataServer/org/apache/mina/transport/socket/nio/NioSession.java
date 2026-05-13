package org.apache.mina.transport.socket.nio;

import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import org.apache.mina.core.session.AbstractIoSession;

public abstract class NioSession extends AbstractIoSession
{
  abstract ByteChannel getChannel();

  abstract SelectionKey getSelectionKey();

  abstract void setSelectionKey(SelectionKey paramSelectionKey);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.transport.socket.nio.NioSession
 * JD-Core Version:    0.6.0
 */