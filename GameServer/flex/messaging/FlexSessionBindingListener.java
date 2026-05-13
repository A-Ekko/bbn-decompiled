package flex.messaging;

public abstract interface FlexSessionBindingListener
{
  public abstract void valueBound(FlexSessionBindingEvent paramFlexSessionBindingEvent);

  public abstract void valueUnbound(FlexSessionBindingEvent paramFlexSessionBindingEvent);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.FlexSessionBindingListener
 * JD-Core Version:    0.6.0
 */