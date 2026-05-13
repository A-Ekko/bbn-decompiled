package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;

public abstract interface ByteArray extends IoAbsoluteReader, IoAbsoluteWriter
{
  public abstract int first();

  public abstract int last();

  public abstract ByteOrder order();

  public abstract void order(ByteOrder paramByteOrder);

  public abstract void free();

  public abstract Iterable<IoBuffer> getIoBuffers();

  public abstract IoBuffer getSingleIoBuffer();

  public abstract boolean equals(Object paramObject);

  public abstract byte get(int paramInt);

  public abstract void get(int paramInt, IoBuffer paramIoBuffer);

  public abstract int getInt(int paramInt);

  public abstract Cursor cursor();

  public abstract Cursor cursor(int paramInt);

  public static abstract interface Cursor extends IoRelativeReader, IoRelativeWriter
  {
    public abstract int getIndex();

    public abstract void setIndex(int paramInt);

    public abstract int getRemaining();

    public abstract boolean hasRemaining();

    public abstract byte get();

    public abstract void get(IoBuffer paramIoBuffer);

    public abstract int getInt();
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.util.byteaccess.ByteArray
 * JD-Core Version:    0.6.0
 */