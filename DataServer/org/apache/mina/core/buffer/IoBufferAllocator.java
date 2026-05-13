package org.apache.mina.core.buffer;

import java.nio.ByteBuffer;

public abstract interface IoBufferAllocator
{
  public abstract IoBuffer allocate(int paramInt, boolean paramBoolean);

  public abstract ByteBuffer allocateNioBuffer(int paramInt, boolean paramBoolean);

  public abstract IoBuffer wrap(ByteBuffer paramByteBuffer);

  public abstract void dispose();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.buffer.IoBufferAllocator
 * JD-Core Version:    0.6.0
 */