package org.logicalcobwebs.cglib.core;

public abstract interface GeneratorStrategy
{
  public abstract byte[] generate(ClassGenerator paramClassGenerator)
    throws Exception;

  public abstract boolean equals(Object paramObject);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.GeneratorStrategy
 * JD-Core Version:    0.6.0
 */