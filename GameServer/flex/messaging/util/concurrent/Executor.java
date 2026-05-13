package flex.messaging.util.concurrent;

public abstract interface Executor
{
  public abstract void execute(Runnable paramRunnable);

  public abstract FailedExecutionHandler getFailedExecutionHandler();

  public abstract void setFailedExecutionHandler(FailedExecutionHandler paramFailedExecutionHandler);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.concurrent.Executor
 * JD-Core Version:    0.6.0
 */