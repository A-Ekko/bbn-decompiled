package org.logicalcobwebs.proxool;

import java.sql.Connection;
import java.sql.SQLException;

public abstract interface ConnectionListenerIF
{
  public static final int MAXIMUM_ACTIVE_TIME_EXPIRED = 1;
  public static final int MANUAL_EXPIRY = 2;
  public static final int VALIDATION_FAIL = 3;
  public static final int SHUTDOWN = 4;
  public static final int RESET_FAIL = 5;
  public static final int HOUSE_KEEPER_TEST_FAIL = 6;
  public static final int MAXIMUM_CONNECTION_LIFETIME_EXCEEDED = 7;
  public static final int FATAL_SQL_EXCEPTION_DETECTED = 8;

  public abstract void onBirth(Connection paramConnection)
    throws SQLException;

  public abstract void onDeath(Connection paramConnection, int paramInt)
    throws SQLException;

  public abstract void onExecute(String paramString, long paramLong);

  public abstract void onFail(String paramString, Exception paramException);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ConnectionListenerIF
 * JD-Core Version:    0.6.0
 */