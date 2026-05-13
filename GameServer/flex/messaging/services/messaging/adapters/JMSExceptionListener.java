package flex.messaging.services.messaging.adapters;

import java.util.EventListener;

public abstract interface JMSExceptionListener extends EventListener
{
  public abstract void exceptionThrown(JMSExceptionEvent paramJMSExceptionEvent);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.JMSExceptionListener
 * JD-Core Version:    0.6.0
 */