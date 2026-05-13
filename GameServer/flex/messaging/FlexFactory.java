package flex.messaging;

import flex.messaging.config.ConfigMap;

public abstract interface FlexFactory extends FlexConfigurable
{
  public static final String SCOPE_REQUEST = "request";
  public static final String SCOPE_SESSION = "session";
  public static final String SCOPE_APPLICATION = "application";
  public static final String SCOPE = "scope";
  public static final String SOURCE = "source";

  public abstract FactoryInstance createFactoryInstance(String paramString, ConfigMap paramConfigMap);

  public abstract Object lookup(FactoryInstance paramFactoryInstance);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.FlexFactory
 * JD-Core Version:    0.6.0
 */