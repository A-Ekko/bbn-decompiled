package flex.messaging.config;

import javax.servlet.ServletConfig;

public abstract interface ConfigurationManager
{
  public static final String LOG_CATEGORY = "Configuration";

  public abstract MessagingConfiguration getMessagingConfiguration(ServletConfig paramServletConfig);

  public abstract void reportTokens();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.ConfigurationManager
 * JD-Core Version:    0.6.0
 */