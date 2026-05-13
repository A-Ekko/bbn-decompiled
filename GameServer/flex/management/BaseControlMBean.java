package flex.management;

import java.io.IOException;
import javax.management.ObjectName;

public abstract interface BaseControlMBean
{
  public abstract String getId()
    throws IOException;

  public abstract String getType()
    throws IOException;

  public abstract ObjectName getParent()
    throws IOException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.BaseControlMBean
 * JD-Core Version:    0.6.0
 */