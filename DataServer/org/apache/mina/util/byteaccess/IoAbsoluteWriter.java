package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;

public abstract interface IoAbsoluteWriter
{
  public abstract int first();

  public abstract int last();

  public abstract ByteOrder order();

  public abstract void put(int paramInt, byte paramByte);

  public abstract void put(int paramInt, IoBuffer paramIoBuffer);

  public abstract void putShort(int paramInt, short paramShort);

  public abstract void putInt(int paramInt1, int paramInt2);

  public abstract void putLong(int paramInt, long paramLong);

  public abstract void putFloat(int paramInt, float paramFloat);

  public abstract void putDouble(int paramInt, double paramDouble);

  public abstract void putChar(int paramInt, char paramChar);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.util.byteaccess.IoAbsoluteWriter
 * JD-Core Version:    0.6.0
 */