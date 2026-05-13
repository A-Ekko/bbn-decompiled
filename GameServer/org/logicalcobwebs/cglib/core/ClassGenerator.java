package org.logicalcobwebs.cglib.core;

import org.logicalcobwebs.cglib.asm.ClassVisitor;

public abstract interface ClassGenerator
{
  public abstract void generateClass(ClassVisitor paramClassVisitor)
    throws Exception;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.ClassGenerator
 * JD-Core Version:    0.6.0
 */