package org.logicalcobwebs.proxool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract interface ProxyConnectionIF extends ConnectionInfoIF
{
  public abstract boolean setStatus(int paramInt1, int paramInt2);

  public abstract boolean setStatus(int paramInt);

  public abstract void markForExpiry(String paramString);

  public abstract boolean isMarkedForExpiry();

  public abstract String getReasonForMark();

  public abstract Connection getConnection();

  public abstract boolean isNull();

  public abstract boolean isAvailable();

  public abstract boolean isActive();

  public abstract boolean isOffline();

  public abstract void reallyClose()
    throws SQLException;

  public abstract void setRequester(String paramString);

  public abstract void close()
    throws SQLException;

  public abstract void registerClosedStatement(Statement paramStatement);

  public abstract boolean isReallyClosed()
    throws SQLException;

  public abstract ConnectionPoolDefinitionIF getDefinition();

  public abstract String getLastSqlCall();

  public abstract int getReasonCode();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ProxyConnectionIF
 * JD-Core Version:    0.6.0
 */