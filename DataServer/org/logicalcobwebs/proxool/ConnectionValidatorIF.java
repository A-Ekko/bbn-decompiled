package org.logicalcobwebs.proxool;

import java.sql.Connection;

public abstract interface ConnectionValidatorIF
{
  public abstract boolean validate(ConnectionPoolDefinitionIF paramConnectionPoolDefinitionIF, Connection paramConnection);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ConnectionValidatorIF
 * JD-Core Version:    0.6.0
 */