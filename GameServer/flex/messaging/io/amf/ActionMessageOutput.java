package flex.messaging.io.amf;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.OutputStream;

public abstract interface ActionMessageOutput extends ObjectOutput
{
  public abstract void writeObject(Object paramObject)
    throws IOException;

  public abstract void writeObjectTraits(TraitsInfo paramTraitsInfo)
    throws IOException;

  public abstract void writeObjectProperty(String paramString, Object paramObject)
    throws IOException;

  public abstract void writeObjectEnd()
    throws IOException;

  public abstract void setDebugTrace(AmfTrace paramAmfTrace);

  public abstract void setOutputStream(OutputStream paramOutputStream);

  public abstract void reset();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.ActionMessageOutput
 * JD-Core Version:    0.6.0
 */