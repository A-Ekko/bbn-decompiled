package flex.management.runtime;

import flex.management.BaseControlMBean;
import java.io.IOException;

public abstract interface AdminConsoleDisplayRegistrarMBean extends BaseControlMBean
{
  public abstract Integer[] getSupportedTypes()
    throws IOException;

  public abstract String[] listForType(int paramInt)
    throws IOException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.AdminConsoleDisplayRegistrarMBean
 * JD-Core Version:    0.6.0
 */