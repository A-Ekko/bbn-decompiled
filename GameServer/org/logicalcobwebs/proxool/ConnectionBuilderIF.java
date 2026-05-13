package org.logicalcobwebs.proxool;

import java.sql.Connection;
import java.sql.SQLException;

public abstract interface ConnectionBuilderIF
{
  public abstract Connection buildConnection(ConnectionPoolDefinitionIF paramConnectionPoolDefinitionIF)
    throws SQLException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ConnectionBuilderIF
 * JD-Core Version:    0.6.0
 */