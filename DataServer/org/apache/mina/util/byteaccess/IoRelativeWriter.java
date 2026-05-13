package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;

public abstract interface IoRelativeWriter
{
  public abstract int getRemaining();

  public abstract boolean hasRemaining();

  public abstract void skip(int paramInt);

  public abstract ByteOrder order();

  public abstract void put(byte paramByte);

  public abstract void put(IoBuffer paramIoBuffer);

  public abstract void putShort(short paramShort);

  public abstract void putInt(int paramInt);

  public abstract void putLong(long paramLong);

  public abstract void putFloat(float paramFloat);

  public abstract void putDouble(double paramDouble);

  public abstract void putChar(char paramChar);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.util.byteaccess.IoRelativeWriter
 * JD-Core Version:    0.6.0
 */