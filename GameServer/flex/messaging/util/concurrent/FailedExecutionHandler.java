package flex.messaging.util.concurrent;

public abstract interface FailedExecutionHandler
{
  public abstract void failedExecution(Runnable paramRunnable, Executor paramExecutor, Exception paramException);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.concurrent.FailedExecutionHandler
 * JD-Core Version:    0.6.0
 */