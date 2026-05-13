package org.logicalcobwebs.cglib.asm.attrs;

import java.util.ArrayList;
import java.util.List;
import org.logicalcobwebs.cglib.asm.Attribute;
import org.logicalcobwebs.cglib.asm.ByteVector;
import org.logicalcobwebs.cglib.asm.ClassReader;
import org.logicalcobwebs.cglib.asm.ClassWriter;
import org.logicalcobwebs.cglib.asm.Label;

public class RuntimeVisibleParameterAnnotations extends Attribute
{
  public List parameters = new ArrayList();

  public RuntimeVisibleParameterAnnotations()
  {
    super("RuntimeVisibleParameterAnnotations");
  }

  protected Attribute read(ClassReader paramClassReader, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, Label[] paramArrayOfLabel)
  {
    RuntimeVisibleParameterAnnotations localRuntimeVisibleParameterAnnotations = new RuntimeVisibleParameterAnnotations();
    Annotation.readParameterAnnotations(localRuntimeVisibleParameterAnnotations.parameters, paramClassReader, paramInt1, paramArrayOfChar);
    return localRuntimeVisibleParameterAnnotations;
  }

  protected ByteVector write(ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    return Annotation.writeParametersAnnotations(new ByteVector(), this.parameters, paramClassWriter);
  }

  public String toString()
  {
    return Annotation.stringParameterAnnotations(this.parameters);
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.attrs.RuntimeVisibleParameterAnnotations
 * JD-Core Version:    0.6.0
 */