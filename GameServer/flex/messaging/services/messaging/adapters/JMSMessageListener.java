package flex.messaging.services.messaging.adapters;

import java.util.EventListener;

public abstract interface JMSMessageListener extends EventListener
{
  public abstract void messageReceived(JMSMessageEvent paramJMSMessageEvent);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.JMSMessageListener
 * JD-Core Version:    0.6.0
 */