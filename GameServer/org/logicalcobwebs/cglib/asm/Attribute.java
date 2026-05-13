package org.logicalcobwebs.cglib.asm;

public class Attribute
{
  public final String type;
  public Attribute next;

  protected Attribute(String paramString)
  {
    this.type = paramString;
  }

  public boolean isUnknown()
  {
    return getClass().getName().equals("org.logicalcobwebs.cglib.asm.Attribute");
  }

  protected Label[] getLabels()
  {
    return null;
  }

  protected Attribute read(ClassReader paramClassReader, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, Label[] paramArrayOfLabel)
  {
    return new Attribute(this.type);
  }

  protected ByteVector write(ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    return new ByteVector();
  }

  final int getCount()
  {
    int i = 0;
    for (Attribute localAttribute = this; localAttribute != null; localAttribute = localAttribute.next)
    {
      if (localAttribute.isUnknown())
        continue;
      i++;
    }
    return i;
  }

  final int getSize(ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 0;
    for (Attribute localAttribute = this; localAttribute != null; localAttribute = localAttribute.next)
    {
      ByteVector localByteVector = localAttribute.write(paramClassWriter, paramArrayOfByte, paramInt1, paramInt2, paramInt3);
      if (localByteVector.length <= 0)
        continue;
      paramClassWriter.newUTF8(localAttribute.type);
      i += localByteVector.length + 6;
    }
    return i;
  }

  final void put(ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, ByteVector paramByteVector)
  {
    if (this.next != null)
      this.next.put(paramClassWriter, paramArrayOfByte, paramInt1, paramInt2, paramInt3, paramByteVector);
    ByteVector localByteVector = write(paramClassWriter, paramArrayOfByte, paramInt1, paramInt2, paramInt3);
    if (localByteVector.length == 0)
    {
      if (paramClassWriter.checkAttributes)
        throw new IllegalArgumentException("Unknown attribute type " + this.type);
    }
    else
    {
      paramByteVector.putShort(paramClassWriter.newUTF8(this.type)).putInt(localByteVector.length);
      paramByteVector.putByteArray(localByteVector.data, 0, localByteVector.length);
    }
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.Attribute
 * JD-Core Version:    0.6.0
 */