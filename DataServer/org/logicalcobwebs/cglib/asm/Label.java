package org.logicalcobwebs.cglib.asm;

public class Label
{
  CodeWriter owner;
  int line;
  boolean resolved;
  int position;
  boolean resized;
  private int referenceCount;
  private int[] srcAndRefPositions;
  int beginStackSize;
  int maxStackSize;
  Edge successors;
  Label next;
  boolean pushed;

  public int getOffset()
  {
    if (!this.resolved)
      throw new IllegalStateException("Label offset position has not been resolved yet");
    return this.position;
  }

  void put(CodeWriter paramCodeWriter, ByteVector paramByteVector, int paramInt, boolean paramBoolean)
  {
    if (this.resolved)
    {
      if (paramBoolean)
        paramByteVector.putInt(this.position - paramInt);
      else
        paramByteVector.putShort(this.position - paramInt);
    }
    else if (paramBoolean)
    {
      addReference(-1 - paramInt, paramByteVector.length);
      paramByteVector.putInt(-1);
    }
    else
    {
      addReference(paramInt, paramByteVector.length);
      paramByteVector.putShort(-1);
    }
  }

  private void addReference(int paramInt1, int paramInt2)
  {
    if (this.srcAndRefPositions == null)
      this.srcAndRefPositions = new int[6];
    if (this.referenceCount >= this.srcAndRefPositions.length)
    {
      int[] arrayOfInt = new int[this.srcAndRefPositions.length + 6];
      System.arraycopy(this.srcAndRefPositions, 0, arrayOfInt, 0, this.srcAndRefPositions.length);
      this.srcAndRefPositions = arrayOfInt;
    }
    this.srcAndRefPositions[(this.referenceCount++)] = paramInt1;
    this.srcAndRefPositions[(this.referenceCount++)] = paramInt2;
  }

  boolean resolve(CodeWriter paramCodeWriter, int paramInt, byte[] paramArrayOfByte)
  {
    int i = 0;
    this.resolved = true;
    this.position = paramInt;
    int j = 0;
    while (j < this.referenceCount)
    {
      int k = this.srcAndRefPositions[(j++)];
      int m = this.srcAndRefPositions[(j++)];
      int n;
      if (k >= 0)
      {
        n = paramInt - k;
        if ((n < -32768) || (n > 32767))
        {
          int i1 = paramArrayOfByte[(m - 1)] & 0xFF;
          if (i1 <= 168)
            paramArrayOfByte[(m - 1)] = (byte)(i1 + 49);
          else
            paramArrayOfByte[(m - 1)] = (byte)(i1 + 20);
          i = 1;
        }
        paramArrayOfByte[(m++)] = (byte)(n >>> 8);
        paramArrayOfByte[m] = (byte)n;
      }
      else
      {
        n = paramInt + k + 1;
        paramArrayOfByte[(m++)] = (byte)(n >>> 24);
        paramArrayOfByte[(m++)] = (byte)(n >>> 16);
        paramArrayOfByte[(m++)] = (byte)(n >>> 8);
        paramArrayOfByte[m] = (byte)n;
      }
    }
    return i;
  }

  public String toString()
  {
    return "L" + System.identityHashCode(this);
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.Label
 * JD-Core Version:    0.6.0
 */