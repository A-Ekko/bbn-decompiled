package org.logicalcobwebs.proxool;

import java.util.Date;

public abstract interface ConnectionPoolStatisticsIF
{
  public abstract long getConnectionsServedCount();

  public abstract long getConnectionsRefusedCount();

  public abstract int getActiveConnectionCount();

  public abstract int getAvailableConnectionCount();

  public abstract int getOfflineConnectionCount();

  public abstract Date getDateStarted();

  public abstract long getConnectionCount();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ConnectionPoolStatisticsIF
 * JD-Core Version:    0.6.0
 */