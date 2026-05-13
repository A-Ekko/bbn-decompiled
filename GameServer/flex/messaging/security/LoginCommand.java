package flex.messaging.security;

import java.security.Principal;
import java.util.List;
import javax.servlet.ServletConfig;

public abstract interface LoginCommand
{
  public abstract void start(ServletConfig paramServletConfig);

  public abstract void stop();

  public abstract Principal doAuthentication(String paramString, Object paramObject);

  public abstract boolean doAuthorization(Principal paramPrincipal, List paramList);

  public abstract boolean logout(Principal paramPrincipal);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.security.LoginCommand
 * JD-Core Version:    0.6.0
 */