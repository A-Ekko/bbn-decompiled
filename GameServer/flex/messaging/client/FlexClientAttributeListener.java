package flex.messaging.client;

public abstract interface FlexClientAttributeListener
{
  public abstract void attributeAdded(FlexClientBindingEvent paramFlexClientBindingEvent);

  public abstract void attributeReplaced(FlexClientBindingEvent paramFlexClientBindingEvent);

  public abstract void attributeRemoved(FlexClientBindingEvent paramFlexClientBindingEvent);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.client.FlexClientAttributeListener
 * JD-Core Version:    0.6.0
 */