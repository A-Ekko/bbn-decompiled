package org.logicalcobwebs.cglib.asm.attrs;

import java.util.ArrayList;
import java.util.HashSet;
import org.logicalcobwebs.cglib.asm.Attribute;
import org.logicalcobwebs.cglib.asm.ByteVector;
import org.logicalcobwebs.cglib.asm.ClassReader;
import org.logicalcobwebs.cglib.asm.ClassWriter;
import org.logicalcobwebs.cglib.asm.Label;

public class StackMapAttribute extends Attribute
{
  static final int MAX_SIZE = 65535;
  public ArrayList frames = new ArrayList();

  public StackMapAttribute()
  {
    super("StackMap");
  }

  public StackMapFrame getFrame(Label paramLabel)
  {
    for (int i = 0; i < this.frames.size(); i++)
    {
      StackMapFrame localStackMapFrame = (StackMapFrame)this.frames.get(i);
      if (localStackMapFrame.label == paramLabel)
        return localStackMapFrame;
    }
    return null;
  }

  protected Attribute read(ClassReader paramClassReader, int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3, Label[] paramArrayOfLabel)
  {
    StackMapAttribute localStackMapAttribute = new StackMapAttribute();
    int i = paramClassReader.readInt(paramInt3 + 4);
    int j = 0;
    if (i > 65535)
    {
      j = paramClassReader.readInt(paramInt1);
      paramInt1 += 4;
    }
    else
    {
      j = paramClassReader.readShort(paramInt1);
      paramInt1 += 2;
    }
    for (int k = 0; k < j; k++)
    {
      StackMapFrame localStackMapFrame = new StackMapFrame();
      paramInt1 = localStackMapFrame.read(paramClassReader, paramInt1, paramArrayOfChar, paramInt3, paramArrayOfLabel);
      localStackMapAttribute.frames.add(localStackMapFrame);
    }
    return localStackMapAttribute;
  }

  protected ByteVector write(ClassWriter paramClassWriter, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    ByteVector localByteVector = new ByteVector();
    if ((paramArrayOfByte != null) && (paramArrayOfByte.length > 65535))
      localByteVector.putInt(this.frames.size());
    else
      localByteVector.putShort(this.frames.size());
    for (int i = 0; i < this.frames.size(); i++)
      ((StackMapFrame)this.frames.get(i)).write(paramClassWriter, paramInt2, paramInt3, localByteVector);
    return localByteVector;
  }

  protected Label[] getLabels()
  {
    HashSet localHashSet = new HashSet();
    for (int i = 0; i < this.frames.size(); i++)
      ((StackMapFrame)this.frames.get(i)).getLabels(localHashSet);
    return (Label[])(Label[])localHashSet.toArray(new Label[localHashSet.size()]);
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("StackMap[");
    for (int i = 0; i < this.frames.size(); i++)
      localStringBuffer.append('\n').append('[').append(this.frames.get(i)).append(']');
    localStringBuffer.append("\n]");
    return localStringBuffer.toString();
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.attrs.StackMapAttribute
 * JD-Core Version:    0.6.0
 */