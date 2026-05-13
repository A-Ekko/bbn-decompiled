package flex.messaging.log;

import flex.messaging.config.ConfigMap;
import java.util.List;

public abstract interface Target
{
  public abstract void initialize(String paramString, ConfigMap paramConfigMap);

  public abstract List getFilters();

  public abstract void setFilters(List paramList);

  public abstract void addFilter(String paramString);

  public abstract void removeFilter(String paramString);

  public abstract short getLevel();

  public abstract void setLevel(short paramShort);

  public abstract void addLogger(Logger paramLogger);

  public abstract void removeLogger(Logger paramLogger);

  public abstract void logEvent(LogEvent paramLogEvent);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.log.Target
 * JD-Core Version:    0.6.0
 */