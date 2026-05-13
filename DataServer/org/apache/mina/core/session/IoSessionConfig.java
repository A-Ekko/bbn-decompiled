package org.apache.mina.core.session;

public abstract interface IoSessionConfig
{
  public abstract int getReadBufferSize();

  public abstract void setReadBufferSize(int paramInt);

  public abstract int getMinReadBufferSize();

  public abstract void setMinReadBufferSize(int paramInt);

  public abstract int getMaxReadBufferSize();

  public abstract void setMaxReadBufferSize(int paramInt);

  public abstract int getThroughputCalculationInterval();

  public abstract long getThroughputCalculationIntervalInMillis();

  public abstract void setThroughputCalculationInterval(int paramInt);

  public abstract int getIdleTime(IdleStatus paramIdleStatus);

  public abstract long getIdleTimeInMillis(IdleStatus paramIdleStatus);

  public abstract void setIdleTime(IdleStatus paramIdleStatus, int paramInt);

  public abstract int getReaderIdleTime();

  public abstract long getReaderIdleTimeInMillis();

  public abstract void setReaderIdleTime(int paramInt);

  public abstract int getWriterIdleTime();

  public abstract long getWriterIdleTimeInMillis();

  public abstract void setWriterIdleTime(int paramInt);

  public abstract int getBothIdleTime();

  public abstract long getBothIdleTimeInMillis();

  public abstract void setBothIdleTime(int paramInt);

  public abstract int getWriteTimeout();

  public abstract long getWriteTimeoutInMillis();

  public abstract void setWriteTimeout(int paramInt);

  public abstract boolean isUseReadOperation();

  public abstract void setUseReadOperation(boolean paramBoolean);

  public abstract void setAll(IoSessionConfig paramIoSessionConfig);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.session.IoSessionConfig
 * JD-Core Version:    0.6.0
 */