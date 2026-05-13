package flex.messaging;

public abstract interface FlexSessionListener
{
  public abstract void sessionCreated(FlexSession paramFlexSession);

  public abstract void sessionDestroyed(FlexSession paramFlexSession);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.FlexSessionListener
 * JD-Core Version:    0.6.0
 */