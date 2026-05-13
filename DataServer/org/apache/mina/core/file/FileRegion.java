package org.apache.mina.core.file;

import java.nio.channels.FileChannel;

public abstract interface FileRegion
{
  public abstract FileChannel getFileChannel();

  public abstract long getPosition();

  public abstract void update(long paramLong);

  public abstract long getRemainingBytes();

  public abstract long getWrittenBytes();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.file.FileRegion
 * JD-Core Version:    0.6.0
 */