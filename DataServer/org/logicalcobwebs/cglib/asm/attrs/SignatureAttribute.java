package org.logicalcobwebs.cglib.asm.attrs;

import org.logicalcobwebs.cglib.asm.Attribute;
import org.logicalcobwebs.cglib.asm.ByteVector;
import org.logicalcobwebs.cglib.asm.ClassReader;
import org.logicalcobwebs.cglib.asm.ClassWriter;
import org.logicalcobwebs.cglib.asm.Label;

public class SignatureAttribute extends Attribute
{
  public String signature;

  public SignatureAttribute()
  {
    super("Signature");
  }

  public SignatureAttribute(String paramString)
  {
    this();
    this.signature = paramString;
  }

  protected Attribute read(ClassReader paramClassReader, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, Label[] paramArrayOfLabel)
  {
    return new SignatureAttribute(paramClassReader.readUTF8(paramInt1, paramArrayOfChar));
  }

  protected ByteVector write(ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    return new ByteVector().putShort(paramClassWriter.newUTF8(this.signature));
  }

  public String toString()
  {
    return this.signature;
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.attrs.SignatureAttribute
 * JD-Core Version:    0.6.0
 */