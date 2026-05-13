package flex.messaging;

public abstract interface MessageClientListener
{
  public abstract void messageClientCreated(MessageClient paramMessageClient);

  public abstract void messageClientDestroyed(MessageClient paramMessageClient);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.MessageClientListener
 * JD-Core Version:    0.6.0
 */