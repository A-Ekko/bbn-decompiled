package org.slf4j;

public abstract interface IMarkerFactory
{
  public abstract Marker getMarker(String paramString);

  public abstract boolean exists(String paramString);

  public abstract boolean detachMarker(String paramString);

  public abstract Marker getDetachedMarker(String paramString);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.slf4j.IMarkerFactory
 * JD-Core Version:    0.6.0
 */