package flex.messaging.io;

import flex.messaging.io.amf.ActionContext;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.AmfTrace;
import java.io.IOException;
import java.io.InputStream;

public abstract interface MessageDeserializer
{
  public abstract void initialize(SerializationContext paramSerializationContext, InputStream paramInputStream, AmfTrace paramAmfTrace);

  public abstract void readMessage(ActionMessage paramActionMessage, ActionContext paramActionContext)
    throws ClassNotFoundException, IOException;

  public abstract Object readObject()
    throws ClassNotFoundException, IOException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.MessageDeserializer
 * JD-Core Version:    0.6.0
 */