package flex.messaging.config;

public abstract interface ConfigurationParser
{
  public abstract void parse(String paramString, ConfigurationFileResolver paramConfigurationFileResolver, ServicesConfiguration paramServicesConfiguration);

  public abstract void reportTokens();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.ConfigurationParser
 * JD-Core Version:    0.6.0
 */