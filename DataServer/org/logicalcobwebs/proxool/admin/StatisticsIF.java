package org.logicalcobwebs.proxool.admin;

import java.util.Date;

public abstract interface StatisticsIF
{
  public abstract long getPeriod();

  public abstract double getAverageActiveTime();

  public abstract double getAverageActiveCount();

  public abstract long getServedCount();

  public abstract long getRefusedCount();

  public abstract Date getStartDate();

  public abstract Date getStopDate();

  public abstract double getServedPerSecond();

  public abstract double getRefusedPerSecond();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.admin.StatisticsIF
 * JD-Core Version:    0.6.0
 */