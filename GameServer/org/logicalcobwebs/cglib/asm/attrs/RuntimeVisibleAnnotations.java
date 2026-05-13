package org.logicalcobwebs.cglib.asm.attrs;

import java.util.ArrayList;
import java.util.List;
import org.logicalcobwebs.cglib.asm.Attribute;
import org.logicalcobwebs.cglib.asm.ByteVector;
import org.logicalcobwebs.cglib.asm.ClassReader;
import org.logicalcobwebs.cglib.asm.ClassWriter;
import org.logicalcobwebs.cglib.asm.Label;

public class RuntimeVisibleAnnotations extends Attribute
{
  public List annotations = new ArrayList();

  public RuntimeVisibleAnnotations()
  {
    super("RuntimeVisibleAnnotations");
  }

  protected Attribute read(ClassReader paramClassReader, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, Label[] paramArrayOfLabel)
  {
    RuntimeVisibleAnnotations localRuntimeVisibleAnnotations = new RuntimeVisibleAnnotations();
    Annotation.readAnnotations(localRuntimeVisibleAnnotations.annotations, paramClassReader, paramInt1, paramArrayOfChar);
    return localRuntimeVisibleAnnotations;
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

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.attrs.RuntimeVisibleAnnotations
 * JD-Core Version:    0.6.0
 */