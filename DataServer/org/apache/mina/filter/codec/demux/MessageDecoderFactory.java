package org.apache.mina.filter.codec.demux;

public abstract interface MessageDecoderFactory
{
  public abstract MessageDecoder getDecoder()
    throws Exception;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.demux.MessageDecoderFactory
 * JD-Core Version:    0.6.0
 */