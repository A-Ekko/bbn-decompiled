package flex.messaging.io;

import java.sql.SQLException;
import java.util.Map;

public abstract interface PageableRowSet
{
  public static final String PAGE = "Page";
  public static final String CURSOR = "Cursor";

  public abstract String[] getColumnNames()
    throws SQLException;

  public abstract Map getRecords(int paramInt1, int paramInt2)
    throws SQLException;

  public abstract int getRowCount();

  public abstract int getInitialDownloadCount();

  public abstract String getID();

  public abstract String getServiceName();

  public abstract void setServicename(String paramString);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.PageableRowSet
 * JD-Core Version:    0.6.0
 */