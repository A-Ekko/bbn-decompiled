package flex.messaging.io;

import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.AmfTrace;
import java.io.IOException;
import java.io.OutputStream;

public abstract interface MessageSerializer
{
  public abstract void setVersion(int paramInt);

  public abstract void initialize(SerializationContext paramSerializationContext, OutputStream paramOutputStream, AmfTrace paramAmfTrace);

  public abstract void writeMessage(ActionMessage paramActionMessage)
    throws IOException;

  public abstract void writeObject(Object paramObject)
    throws IOException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.MessageSerializer
 * JD-Core Version:    0.6.0
 */