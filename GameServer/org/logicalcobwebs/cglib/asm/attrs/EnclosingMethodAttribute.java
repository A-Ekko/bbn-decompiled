package org.logicalcobwebs.cglib.asm.attrs;

import org.logicalcobwebs.cglib.asm.Attribute;
import org.logicalcobwebs.cglib.asm.ByteVector;
import org.logicalcobwebs.cglib.asm.ClassReader;
import org.logicalcobwebs.cglib.asm.ClassWriter;
import org.logicalcobwebs.cglib.asm.Label;

public class EnclosingMethodAttribute extends Attribute
{
  public String owner;
  public String name;
  public String desc;

  public EnclosingMethodAttribute()
  {
    super("EnclosingMethod");
  }

  public EnclosingMethodAttribute(String paramString1, String paramString2, String paramString3)
  {
    this();
    this.owner = paramString1;
    this.name = paramString2;
    this.desc = paramString3;
  }

  protected Attribute read(ClassReader paramClassReader, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, Label[] paramArrayOfLabel)
  {
    String str1 = paramClassReader.readClass(paramInt1, paramArrayOfChar);
    int i = paramClassReader.getItem(paramClassReader.readUnsignedShort(paramInt1 + 2));
    String str2 = null;
    String str3 = null;
    if (i != 0)
    {
      str2 = paramClassReader.readUTF8(i, paramArrayOfChar);
      str3 = paramClassReader.readUTF8(i + 2, paramArrayOfChar);
    }
    return new EnclosingMethodAttribute(str1, str2, str3);
  }

  protected ByteVector write(ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    return new ByteVector().putShort(paramClassWriter.newClass(this.owner)).putShort((this.name == null) || (this.desc == null) ? 0 : paramClassWriter.newNameType(this.name, this.desc));
  }

  public String toString()
  {
    return "owner:" + this.owner + " name:" + this.name + " desc:" + this.desc;
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.attrs.EnclosingMethodAttribute
 * JD-Core Version:    0.6.0
 */