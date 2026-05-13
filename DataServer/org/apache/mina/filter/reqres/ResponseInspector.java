package org.apache.mina.filter.reqres;

public abstract interface ResponseInspector
{
  public abstract Object getRequestId(Object paramObject);

  public abstract ResponseType getResponseType(Object paramObject);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.reqres.ResponseInspector
 * JD-Core Version:    0.6.0
 */