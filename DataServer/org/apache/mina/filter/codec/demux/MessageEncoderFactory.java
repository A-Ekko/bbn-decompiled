package org.apache.mina.filter.codec.demux;

public abstract interface MessageEncoderFactory<T>
{
  public abstract MessageEncoder<T> getEncoder()
    throws Exception;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.demux.MessageEncoderFactory
 * JD-Core Version:    0.6.0
 */