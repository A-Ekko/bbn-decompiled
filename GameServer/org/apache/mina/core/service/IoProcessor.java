package org.apache.mina.core.service;

import org.apache.mina.core.session.IoSession;

public abstract interface IoProcessor<T extends IoSession>
{
  public abstract boolean isDisposing();

  public abstract boolean isDisposed();

  public abstract void dispose();

  public abstract void add(T paramT);

  public abstract void flush(T paramT);

  public abstract void updateTrafficControl(T paramT);

  public abstract void remove(T paramT);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.service.IoProcessor
 * JD-Core Version:    0.6.0
 */