package flex.messaging.io;

public abstract interface TypeMarshaller
{
  public abstract Object createInstance(Object paramObject, Class paramClass);

  public abstract Object convert(Object paramObject, Class paramClass);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.TypeMarshaller
 * JD-Core Version:    0.6.0
 */