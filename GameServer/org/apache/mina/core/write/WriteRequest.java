package org.apache.mina.core.write;

import java.net.SocketAddress;
import org.apache.mina.core.future.WriteFuture;

public abstract interface WriteRequest
{
  public abstract WriteRequest getOriginalRequest();

  public abstract WriteFuture getFuture();

  public abstract Object getMessage();

  public abstract SocketAddress getDestination();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.write.WriteRequest
 * JD-Core Version:    0.6.0
 */