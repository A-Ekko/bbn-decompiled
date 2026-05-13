package org.slf4j.spi;

import java.util.Map;

public abstract interface MDCAdapter
{
  public abstract void put(String paramString1, String paramString2);

  public abstract String get(String paramString);

  public abstract void remove(String paramString);

  public abstract void clear();

  public abstract Map getCopyOfContextMap();

  public abstract void setContextMap(Map paramMap);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.slf4j.spi.MDCAdapter
 * JD-Core Version:    0.6.0
 */