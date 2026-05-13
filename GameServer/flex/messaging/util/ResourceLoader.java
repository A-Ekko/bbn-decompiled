package flex.messaging.util;

import java.util.Locale;
import java.util.Map;

public abstract interface ResourceLoader
{
  public abstract void init(Map paramMap);

  public abstract void setDefaultLocale(String paramString);

  public abstract void setDefaultLocale(Locale paramLocale);

  public abstract Locale getDefaultLocale();

  public abstract String getString(String paramString);

  public abstract String getString(String paramString, Object[] paramArrayOfObject);

  public abstract String getString(String paramString, Locale paramLocale);

  public abstract String getString(String paramString, Locale paramLocale, Object[] paramArrayOfObject);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.ResourceLoader
 * JD-Core Version:    0.6.0
 */