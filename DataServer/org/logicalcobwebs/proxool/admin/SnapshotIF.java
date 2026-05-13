package org.logicalcobwebs.proxool.admin;

import java.util.Date;
import org.logicalcobwebs.proxool.ConnectionInfoIF;

public abstract interface SnapshotIF
{
  public abstract Date getDateStarted();

  public abstract long getServedCount();

  public abstract long getRefusedCount();

  public abstract int getActiveConnectionCount();

  public abstract int getAvailableConnectionCount();

  public abstract int getOfflineConnectionCount();

  public abstract int getMaximumConnectionCount();

  public abstract Date getSnapshotDate();

  public abstract ConnectionInfoIF[] getConnectionInfos();

  public abstract ConnectionInfoIF getConnectionInfo(long paramLong);

  public abstract boolean isDetail();

  public abstract long getConnectionCount();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.admin.SnapshotIF
 * JD-Core Version:    0.6.0
 */