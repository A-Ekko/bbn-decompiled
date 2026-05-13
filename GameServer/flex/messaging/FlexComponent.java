package flex.messaging;

public abstract interface FlexComponent extends FlexConfigurable
{
  public abstract void start();

  public abstract void stop();

  public abstract boolean isStarted();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.FlexComponent
 * JD-Core Version:    0.6.0
 */