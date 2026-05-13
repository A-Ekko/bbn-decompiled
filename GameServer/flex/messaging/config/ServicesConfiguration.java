package flex.messaging.config;

import java.util.List;
import java.util.Map;

public abstract interface ServicesConfiguration
{
  public abstract void addChannelSettings(String paramString, ChannelSettings paramChannelSettings);

  public abstract ChannelSettings getChannelSettings(String paramString);

  public abstract Map getAllChannelSettings();

  public abstract void addDefaultChannel(String paramString);

  public abstract List getDefaultChannels();

  public abstract void addServiceSettings(ServiceSettings paramServiceSettings);

  public abstract ServiceSettings getServiceSettings(String paramString);

  public abstract List getAllServiceSettings();

  public abstract void setLoggingSettings(LoggingSettings paramLoggingSettings);

  public abstract LoggingSettings getLoggingSettings();

  public abstract ClusterSettings getClusterSettings(String paramString);

  public abstract ClusterSettings getDefaultCluster();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.ServicesConfiguration
 * JD-Core Version:    0.6.0
 */