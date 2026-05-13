package org.logicalcobwebs.cglib.transform;

import org.logicalcobwebs.cglib.asm.ClassVisitor;

public abstract interface ClassTransformer extends ClassVisitor
{
  public abstract void setTarget(ClassVisitor paramClassVisitor);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.ClassTransformer
 * JD-Core Version:    0.6.0
 */