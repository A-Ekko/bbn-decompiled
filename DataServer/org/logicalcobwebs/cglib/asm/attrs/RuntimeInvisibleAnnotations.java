package org.logicalcobwebs.cglib.asm.attrs;

import java.util.ArrayList;
import java.util.List;
import org.logicalcobwebs.cglib.asm.Attribute;
import org.logicalcobwebs.cglib.asm.ByteVector;
import org.logicalcobwebs.cglib.asm.ClassReader;
import org.logicalcobwebs.cglib.asm.ClassWriter;
import org.logicalcobwebs.cglib.asm.Label;

public class RuntimeInvisibleAnnotations extends Attribute
{
  public List annotations = new ArrayList();

  public RuntimeInvisibleAnnotations()
  {
    super("RuntimeInvisibleAnnotations");
  }

  protected Attribute read(ClassReader paramClassReader, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, Label[] paramArrayOfLabel)
  {
    RuntimeInvisibleAnnotations localRuntimeInvisibleAnnotations = new RuntimeInvisibleAnnotations();
    Annotation.readAnnotations(localRuntimeInvisibleAnnotations.annotations, paramClassReader, paramInt1, paramArrayOfChar);
    return localRuntimeInvisibleAnnotations;
  }

  protected ByteVector write(ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    return Annotation.writeAnnotations(new ByteVector(), this.annotations, paramClassWriter);
  }

  public String toString()
  {
    return Annotation.stringAnnotations(this.annotations);
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.attrs.RuntimeInvisibleAnnotations
 * JD-Core Version:    0.6.0
 */