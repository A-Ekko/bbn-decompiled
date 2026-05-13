package flex.messaging.util;

import edu.emory.mathcs.backport.java.util.concurrent.Future;

public abstract interface TimeoutCapable
{
  public abstract void cancelTimeout();

  public abstract long getLastUse();

  public abstract long getTimeoutPeriod();

  public abstract void setTimeoutFuture(Future paramFuture);

  public abstract void timeout();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.TimeoutCapable
 * JD-Core Version:    0.6.0
 */