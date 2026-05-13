package flex.messaging.io.amf;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;

public abstract interface ActionMessageInput extends ObjectInput
{
  public abstract Object readObject()
    throws ClassNotFoundException, IOException;

  public abstract void setInputStream(InputStream paramInputStream);

  public abstract void setDebugTrace(AmfTrace paramAmfTrace);

  public abstract void reset();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.ActionMessageInput
 * JD-Core Version:    0.6.0
 */