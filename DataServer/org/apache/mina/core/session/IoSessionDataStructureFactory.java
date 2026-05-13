package org.apache.mina.core.session;

import org.apache.mina.core.write.WriteRequestQueue;

public abstract interface IoSessionDataStructureFactory
{
  public abstract IoSessionAttributeMap getAttributeMap(IoSession paramIoSession)
    throws Exception;

  public abstract WriteRequestQueue getWriteRequestQueue(IoSession paramIoSession)
    throws Exception;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.session.IoSessionDataStructureFactory
 * JD-Core Version:    0.6.0
 */