package org.logicalcobwebs.cglib.asm.attrs;

import org.logicalcobwebs.cglib.asm.Attribute;
import org.logicalcobwebs.cglib.asm.ByteVector;
import org.logicalcobwebs.cglib.asm.ClassReader;
import org.logicalcobwebs.cglib.asm.ClassWriter;
import org.logicalcobwebs.cglib.asm.Label;

public class SourceDebugExtensionAttribute extends Attribute
{
  public String debugExtension;

  public SourceDebugExtensionAttribute()
  {
    super("SourceDebugExtension");
  }

  public SourceDebugExtensionAttribute(String paramString)
  {
    this();
    this.debugExtension = paramString;
  }

  protected Attribute read(ClassReader paramClassReader, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, Label[] paramArrayOfLabel)
  {
    return new SourceDebugExtensionAttribute(readUTF8(paramClassReader, paramInt1, paramInt2));
  }

  protected ByteVector write(ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    byte[] arrayOfByte = putUTF8(this.debugExtension);
    return new ByteVector().putByteArray(arrayOfByte, 0, arrayOfByte.length);
  }

  private String readUTF8(ClassReader paramClassReader, int paramInt1, int paramInt2)
  {
    int i = paramInt1 + paramInt2;
    byte[] arrayOfByte = paramClassReader.b;
    char[] arrayOfChar = new char[paramInt2];
    int j = 0;
    while (paramInt1 < i)
    {
      int k = arrayOfByte[(paramInt1++)] & 0xFF;
      int m;
      switch (k >> 4)
      {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
        arrayOfChar[(j++)] = (char)k;
        break;
      case 12:
      case 13:
        m = arrayOfByte[(paramInt1++)];
        arrayOfChar[(j++)] = (char)((k & 0x1F) << 6 | m & 0x3F);
        break;
      case 8:
      case 9:
      case 10:
      case 11:
      default:
        m = arrayOfByte[(paramInt1++)];
        int n = arrayOfByte[(paramInt1++)];
        arrayOfChar[(j++)] = (char)((k & 0xF) << 12 | (m & 0x3F) << 6 | n & 0x3F);
      }
    }
    return new String(arrayOfChar, 0, j);
  }

  private byte[] putUTF8(String paramString)
  {
    int i = paramString.length();
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      m = paramString.charAt(k);
      if ((m >= 1) && (m <= 127))
        j++;
      else if (m > 2047)
        j += 3;
      else
        j += 2;
    }
    byte[] arrayOfByte = new byte[j];
    int m = 0;
    while (m < i)
    {
      int n = paramString.charAt(m);
      if ((n >= 1) && (n <= 127))
      {
        arrayOfByte[(m++)] = (byte)n;
      }
      else if (n > 2047)
      {
        arrayOfByte[(m++)] = (byte)(0xE0 | n >> 12 & 0xF);
        arrayOfByte[(m++)] = (byte)(0x80 | n >> 6 & 0x3F);
        arrayOfByte[(m++)] = (byte)(0x80 | n & 0x3F);
      }
      else
      {
        arrayOfByte[(m++)] = (byte)(0xC0 | n >> 6 & 0x1F);
        arrayOfByte[(m++)] = (byte)(0x80 | n & 0x3F);
      }
    }
    return arrayOfByte;
  }

  public String toString()
  {
    return this.debugExtension;
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.attrs.SourceDebugExtensionAttribute
 * JD-Core Version:    0.6.0
 */