package flex.management.runtime.messaging.log;

import flex.management.BaseControlMBean;

public abstract interface LogControlMBean extends BaseControlMBean
{
  public abstract String[] getTargets();

  public abstract String[] getTargetFilters(String paramString);

  public abstract String[] getCategories();

  public abstract Integer getTargetLevel(String paramString);

  public abstract void changeTargetLevel(String paramString1, String paramString2);

  public abstract void addFilterForTarget(String paramString1, String paramString2);

  public abstract void removeFilterForTarget(String paramString1, String paramString2);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.log.LogControlMBean
 * JD-Core Version:    0.6.0
 */