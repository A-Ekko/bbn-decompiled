package org.logicalcobwebs.proxool;

import java.util.Properties;

public abstract interface ConfigurationListenerIF
{
  public abstract void definitionUpdated(ConnectionPoolDefinitionIF paramConnectionPoolDefinitionIF, Properties paramProperties1, Properties paramProperties2);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ConfigurationListenerIF
 * JD-Core Version:    0.6.0
 */