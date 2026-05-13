package org.apache.log4j.xml;

import java.util.Properties;
import org.w3c.dom.Element;

public abstract interface UnrecognizedElementHandler
{
  public abstract boolean parseUnrecognizedElement(Element paramElement, Properties paramProperties)
    throws Exception;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.xml.UnrecognizedElementHandler
 * JD-Core Version:    0.6.0
 */