package org.logicalcobwebs.cglib.asm.attrs;

import org.logicalcobwebs.cglib.asm.Attribute;
import org.logicalcobwebs.cglib.asm.ByteVector;
import org.logicalcobwebs.cglib.asm.ClassReader;
import org.logicalcobwebs.cglib.asm.ClassWriter;
import org.logicalcobwebs.cglib.asm.Label;

public class AnnotationDefaultAttribute extends Attribute
{
  public Object defaultValue;

  public AnnotationDefaultAttribute()
  {
    super("AnnotationDefault");
  }

  public AnnotationDefaultAttribute(Object paramObject)
  {
    this();
    this.defaultValue = paramObject;
  }

  protected Attribute read(ClassReader paramClassReader, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, Label[] paramArrayOfLabel)
  {
    return new AnnotationDefaultAttribute(Annotation.readValue(paramClassReader, new int[] { paramInt1 }, paramArrayOfChar));
  }

  protected ByteVector write(ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    return Annotation.writeValue(new ByteVector(), this.defaultValue, paramClassWriter);
  }

  public String toString()
  {
    return "default " + this.defaultValue;
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.attrs.AnnotationDefaultAttribute
 * JD-Core Version:    0.6.0
 */