package flex.messaging.config;

import java.io.InputStream;

public abstract interface ConfigurationFileResolver
{
  public abstract InputStream getConfigurationFile(String paramString);

  public abstract InputStream getIncludedFile(String paramString);

  public abstract void popIncludedFile();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.ConfigurationFileResolver
 * JD-Core Version:    0.6.0
 */