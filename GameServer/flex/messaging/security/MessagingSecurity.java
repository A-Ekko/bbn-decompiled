package flex.messaging.security;

import flex.messaging.services.messaging.Subtopic;

public abstract interface MessagingSecurity
{
  public abstract boolean allowSubscribe(Subtopic paramSubtopic);

  public abstract boolean allowSend(Subtopic paramSubtopic);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.security.MessagingSecurity
 * JD-Core Version:    0.6.0
 */