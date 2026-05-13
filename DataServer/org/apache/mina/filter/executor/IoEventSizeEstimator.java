package org.apache.mina.filter.executor;

import org.apache.mina.core.session.IoEvent;

public abstract interface IoEventSizeEstimator
{
  public abstract int estimateSize(IoEvent paramIoEvent);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.executor.IoEventSizeEstimator
 * JD-Core Version:    0.6.0
 */