package flex.management;

public abstract interface Manageable
{
  public abstract boolean isManaged();

  public abstract void setManaged(boolean paramBoolean);

  public abstract BaseControl getControl();

  public abstract void setControl(BaseControl paramBaseControl);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.Manageable
 * JD-Core Version:    0.6.0
 */