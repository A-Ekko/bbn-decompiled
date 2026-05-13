package flex.messaging.client;

public abstract interface FlexClientListener
{
  public abstract void clientCreated(FlexClient paramFlexClient);

  public abstract void clientDestroyed(FlexClient paramFlexClient);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.client.FlexClientListener
 * JD-Core Version:    0.6.0
 */