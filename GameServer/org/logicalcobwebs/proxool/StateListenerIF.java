package org.logicalcobwebs.proxool;

public abstract interface StateListenerIF
{
  public static final int STATE_QUIET = 0;
  public static final int STATE_BUSY = 1;
  public static final int STATE_OVERLOADED = 2;
  public static final int STATE_DOWN = 3;

  public abstract void upStateChanged(int paramInt);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.StateListenerIF
 * JD-Core Version:    0.6.0
 */