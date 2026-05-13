package org.logicalcobwebs.proxool;

import java.util.Date;

public abstract interface ConnectionInfoIF extends Comparable
{
  public static final int MARK_FOR_USE = 0;
  public static final int MARK_FOR_EXPIRY = 1;
  public static final int STATUS_NULL = 0;
  public static final int STATUS_AVAILABLE = 1;
  public static final int STATUS_ACTIVE = 2;
  public static final int STATUS_OFFLINE = 3;

  public abstract long getBirthTime();

  public abstract Date getBirthDate();

  public abstract long getAge();

  public abstract long getId();

  public abstract int getMark();

  public abstract int getStatus();

  public abstract long getTimeLastStartActive();

  public abstract long getTimeLastStopActive();

  public abstract String getRequester();

  public abstract String getProxyHashcode();

  public abstract String getDelegateHashcode();

  public abstract String getDelegateUrl();

  public abstract String[] getSqlCalls();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ConnectionInfoIF
 * JD-Core Version:    0.6.0
 */