package flex.messaging;

public abstract interface FlexSessionAttributeListener
{
  public abstract void attributeAdded(FlexSessionBindingEvent paramFlexSessionBindingEvent);

  public abstract void attributeRemoved(FlexSessionBindingEvent paramFlexSessionBindingEvent);

  public abstract void attributeReplaced(FlexSessionBindingEvent paramFlexSessionBindingEvent);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.FlexSessionAttributeListener
 * JD-Core Version:    0.6.0
 */