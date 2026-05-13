package org.logicalcobwebs.cglib.asm.attrs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.logicalcobwebs.cglib.asm.Attribute;
import org.logicalcobwebs.cglib.asm.ByteVector;
import org.logicalcobwebs.cglib.asm.ClassReader;
import org.logicalcobwebs.cglib.asm.ClassWriter;
import org.logicalcobwebs.cglib.asm.Label;

public class LocalVariableTypeTableAttribute extends Attribute
{
  protected List types = new ArrayList();

  public LocalVariableTypeTableAttribute()
  {
    super("LocalVariableTypeTable");
  }

  public List getTypes()
  {
    return this.types;
  }

  protected Label[] getLabels()
  {
    HashSet localHashSet = new HashSet();
    for (int i = 0; i < this.types.size(); i++)
    {
      LocalVariableType localLocalVariableType = (LocalVariableType)this.types.get(i);
      localHashSet.add(localLocalVariableType.getStart());
      localHashSet.add(localLocalVariableType.getEnd());
    }
    return (Label[])(Label[])localHashSet.toArray(new Label[localHashSet.size()]);
  }

  protected Attribute read(ClassReader paramClassReader, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, Label[] paramArrayOfLabel)
  {
    int i = paramClassReader.readUnsignedShort(paramInt1);
    paramInt1 += 2;
    LocalVariableTypeTableAttribute localLocalVariableTypeTableAttribute = new LocalVariableTypeTableAttribute();
    for (int j = 0; j < i; j++)
    {
      LocalVariableType localLocalVariableType = new LocalVariableType();
      int k = paramClassReader.readUnsignedShort(paramInt1);
      int m = paramClassReader.readUnsignedShort(paramInt1 + 2);
      localLocalVariableType.start = getLabel(paramArrayOfLabel, k);
      localLocalVariableType.end = getLabel(paramArrayOfLabel, k + m);
      localLocalVariableType.name = paramClassReader.readUTF8(paramInt1 + 4, paramArrayOfChar);
      localLocalVariableType.signature = paramClassReader.readUTF8(paramInt1 + 6, paramArrayOfChar);
      localLocalVariableType.index = paramClassReader.readUnsignedShort(paramInt1 + 8);
      paramInt1 += 10;
      this.types.add(localLocalVariableType);
    }
    return localLocalVariableTypeTableAttribute;
  }

  protected ByteVector write(ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    ByteVector localByteVector = new ByteVector();
    localByteVector.putShort(this.types.size());
    for (int i = 0; i < this.types.size(); i++)
    {
      LocalVariableType localLocalVariableType = (LocalVariableType)this.types.get(i);
      int j = localLocalVariableType.getStart().getOffset();
      localByteVector.putShort(j);
      localByteVector.putShort(localLocalVariableType.getEnd().getOffset() - j);
      localByteVector.putUTF8(localLocalVariableType.getName());
      localByteVector.putUTF8(localLocalVariableType.getSignature());
      localByteVector.putShort(localLocalVariableType.getIndex());
    }
    return localByteVector;
  }

  private Label getLabel(Label[] paramArrayOfLabel, int paramInt)
  {
    Label localLabel = paramArrayOfLabel[paramInt];
    if (localLabel == null)
    {
      localLabel = new Label();
      paramArrayOfLabel[paramInt] = localLabel;
    }
    return localLabel;
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("LocalVariableTypeTable[");
    for (int i = 0; i < this.types.size(); i++)
      localStringBuffer.append('\n').append('[').append(this.types.get(i)).append(']');
    localStringBuffer.append("\n]");
    return localStringBuffer.toString();
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.attrs.LocalVariableTypeTableAttribute
 * JD-Core Version:    0.6.0
 */