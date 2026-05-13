package flex.messaging.services.messaging.adapters;

import javax.jms.JMSException;

abstract interface MessageReceiver
{
  public abstract void startReceive()
    throws JMSException;

  public abstract void stopReceive();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.MessageReceiver
 * JD-Core Version:    0.6.0
 */