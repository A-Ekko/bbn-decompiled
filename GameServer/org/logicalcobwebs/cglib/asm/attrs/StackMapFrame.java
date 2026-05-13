package org.logicalcobwebs.cglib.asm.attrs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.logicalcobwebs.cglib.asm.ByteVector;
import org.logicalcobwebs.cglib.asm.ClassReader;
import org.logicalcobwebs.cglib.asm.ClassWriter;
import org.logicalcobwebs.cglib.asm.Label;

public class StackMapFrame
{
  public Label label;
  public List locals = new ArrayList();
  public List stack = new ArrayList();

  public int read(ClassReader paramClassReader, int paramInt1, char[] paramArrayOfChar, int paramInt2, Label[] paramArrayOfLabel)
  {
    int i = paramClassReader.readUnsignedShort(paramInt1);
    paramInt1 += 2;
    if (paramArrayOfLabel[i] == null)
      paramArrayOfLabel[i] = new Label();
    this.label = paramArrayOfLabel[i];
    paramInt1 = readTypeInfo(paramClassReader, paramInt1, this.locals, paramArrayOfLabel, paramArrayOfChar, paramClassReader.readUnsignedShort(paramInt2 + 2));
    paramInt1 = readTypeInfo(paramClassReader, paramInt1, this.stack, paramArrayOfLabel, paramArrayOfChar, paramClassReader.readUnsignedShort(paramInt2));
    return paramInt1;
  }

  public void write(ClassWriter paramClassWriter, int paramInt1, int paramInt2, ByteVector paramByteVector)
  {
    paramByteVector.putShort(this.label.getOffset());
    writeTypeInfo(paramByteVector, paramClassWriter, this.locals, paramInt2);
    writeTypeInfo(paramByteVector, paramClassWriter, this.stack, paramInt1);
  }

  public void getLabels(Set paramSet)
  {
    paramSet.add(this.label);
    getTypeInfoLabels(paramSet, this.locals);
    getTypeInfoLabels(paramSet, this.stack);
  }

  private void getTypeInfoLabels(Set paramSet, List paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      StackMapType localStackMapType = (StackMapType)localIterator.next();
      if (localStackMapType.getType() == 8)
        paramSet.add(localStackMapType.getLabel());
    }
  }

  private int readTypeInfo(ClassReader paramClassReader, int paramInt1, List paramList, Label[] paramArrayOfLabel, char[] paramArrayOfChar, int paramInt2)
  {
    int i = 0;
    if (paramInt2 > 65535)
    {
      i = paramClassReader.readInt(paramInt1);
      paramInt1 += 4;
    }
    else
    {
      i = paramClassReader.readUnsignedShort(paramInt1);
      paramInt1 += 2;
    }
    for (int j = 0; j < i; j++)
    {
      int k = paramClassReader.readByte(paramInt1++);
      StackMapType localStackMapType = StackMapType.getTypeInfo(k);
      paramList.add(localStackMapType);
      switch (k)
      {
      case 7:
        localStackMapType.setObject(paramClassReader.readClass(paramInt1, paramArrayOfChar));
        paramInt1 += 2;
        break;
      case 8:
        int m = paramClassReader.readUnsignedShort(paramInt1);
        paramInt1 += 2;
        if (paramArrayOfLabel[m] == null)
          paramArrayOfLabel[m] = new Label();
        localStackMapType.setLabel(paramArrayOfLabel[m]);
      }
    }
    return paramInt1;
  }

  private void writeTypeInfo(ByteVector paramByteVector, ClassWriter paramClassWriter, List paramList, int paramInt)
  {
    if (paramInt > 65535)
      paramByteVector.putInt(paramList.size());
    else
      paramByteVector.putShort(paramList.size());
    for (int i = 0; i < paramList.size(); i++)
    {
      StackMapType localStackMapType = (StackMapType)paramList.get(i);
      paramByteVector.putByte(localStackMapType.getType());
      switch (localStackMapType.getType())
      {
      case 7:
        paramByteVector.putShort(paramClassWriter.newClass(localStackMapType.getObject()));
        break;
      case 8:
        paramByteVector.putShort(localStackMapType.getLabel().getOffset());
      }
    }
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("Frame:L");
    localStringBuffer.append(System.identityHashCode(this.label));
    localStringBuffer.append(" locals").append(this.locals);
    localStringBuffer.append(" stack").append(this.stack);
    return localStringBuffer.toString();
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.attrs.StackMapFrame
 * JD-Core Version:    0.6.0
 */