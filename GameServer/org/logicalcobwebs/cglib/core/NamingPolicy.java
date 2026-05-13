package org.logicalcobwebs.cglib.core;

public abstract interface NamingPolicy
{
  public abstract String getClassName(String paramString1, String paramString2, Object paramObject, Predicate paramPredicate);

  public abstract boolean equals(Object paramObject);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.NamingPolicy
 * JD-Core Version:    0.6.0
 */