package org.apache.log4j.spi;

import java.io.Writer;

/** @deprecated */
class NullWriter extends Writer
{
  public void close()
  {
  }

  public void flush()
  {
  }

  public void write(char[] cbuf, int off, int len)
  {
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.spi.NullWriter
 * JD-Core Version:    0.6.0
 */