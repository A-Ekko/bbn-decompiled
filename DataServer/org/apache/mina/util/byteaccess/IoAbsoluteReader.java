package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;

public abstract interface IoAbsoluteReader
{
  public abstract int first();

  public abstract int last();

  public abstract int length();

  public abstract ByteArray slice(int paramInt1, int paramInt2);

  public abstract ByteOrder order();

  public abstract byte get(int paramInt);

  public abstract void get(int paramInt, IoBuffer paramIoBuffer);

  public abstract short getShort(int paramInt);

  public abstract int getInt(int paramInt);

  public abstract long getLong(int paramInt);

  public abstract float getFloat(int paramInt);

  public abstract double getDouble(int paramInt);

  public abstract char getChar(int paramInt);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.util.byteaccess.IoAbsoluteReader
 * JD-Core Version:    0.6.0
 */