package org.logicalcobwebs.cglib.asm.attrs;

import org.logicalcobwebs.cglib.asm.Label;

public class StackMapType
{
  public static final int ITEM_Top = 0;
  public static final int ITEM_Integer = 1;
  public static final int ITEM_Float = 2;
  public static final int ITEM_Double = 3;
  public static final int ITEM_Long = 4;
  public static final int ITEM_Null = 5;
  public static final int ITEM_UninitializedThis = 6;
  public static final int ITEM_Object = 7;
  public static final int ITEM_Uninitialized = 8;
  public static final String[] ITEM_NAMES = { "Top", "Integer", "Float", "Double", "Long", "Null", "UninitializedThis", "Object", "Uninitialized" };
  private int type;
  private Label offset;
  private String object;

  private StackMapType(int paramInt)
  {
    this.type = paramInt;
  }

  public int getType()
  {
    return this.type;
  }

  public static StackMapType getTypeInfo(int paramInt)
  {
    return new StackMapType(paramInt);
  }

  public void setLabel(Label paramLabel)
  {
    this.offset = paramLabel;
  }

  public void setObject(String paramString)
  {
    this.object = paramString;
  }

  public Label getLabel()
  {
    return this.offset;
  }

  public String getObject()
  {
    return this.object;
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer(ITEM_NAMES[this.type]);
    if (this.type == 7)
      localStringBuffer.append(":").append(this.object);
    if (this.type == 8)
      localStringBuffer.append(":L").append(System.identityHashCode(this.offset));
    return localStringBuffer.toString();
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.attrs.StackMapType
 * JD-Core Version:    0.6.0
 */