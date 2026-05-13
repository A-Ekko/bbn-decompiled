package org.logicalcobwebs.proxool;

import java.util.Properties;

public abstract interface ProxoolListenerIF
{
  public abstract void onRegistration(ConnectionPoolDefinitionIF paramConnectionPoolDefinitionIF, Properties paramProperties);

  public abstract void onShutdown(String paramString);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ProxoolListenerIF
 * JD-Core Version:    0.6.0
 */