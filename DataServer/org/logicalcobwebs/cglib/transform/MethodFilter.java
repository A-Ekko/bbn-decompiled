package org.logicalcobwebs.cglib.transform;

import org.logicalcobwebs.cglib.asm.Attribute;

public abstract interface MethodFilter
{
  public abstract boolean accept(int paramInt, String paramString1, String paramString2, String[] paramArrayOfString, Attribute paramAttribute);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.MethodFilter
 * JD-Core Version:    0.6.0
 */