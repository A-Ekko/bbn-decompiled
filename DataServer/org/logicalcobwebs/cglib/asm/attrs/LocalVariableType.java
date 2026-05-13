package org.logicalcobwebs.cglib.asm.attrs;

import org.logicalcobwebs.cglib.asm.Label;

public class LocalVariableType
{
  public int index;
  public String name;
  public String signature;
  public Label start;
  public Label end;

  public int getIndex()
  {
    return this.index;
  }

  public String getName()
  {
    return this.name;
  }

  public String getSignature()
  {
    return this.signature;
  }

  public Label getStart()
  {
    return this.start;
  }

  public Label getEnd()
  {
    return this.end;
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer(this.index).append(" : ").append(this.name).append(" ").append(this.signature).append("[L").append(System.identityHashCode(this.start)).append(" - L").append(System.identityHashCode(this.end)).append("]");
    return localStringBuffer.toString();
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.asm.attrs.LocalVariableType
 * JD-Core Version:    0.6.0
 */