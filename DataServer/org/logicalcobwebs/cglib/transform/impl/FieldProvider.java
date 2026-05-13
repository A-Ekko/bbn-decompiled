package org.logicalcobwebs.cglib.transform.impl;

public abstract interface FieldProvider
{
  public abstract String[] getFieldNames();

  public abstract Class[] getFieldTypes();

  public abstract void setField(int paramInt, Object paramObject);

  public abstract Object getField(int paramInt);

  public abstract void setField(String paramString, Object paramObject);

  public abstract Object getField(String paramString);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.impl.FieldProvider
 * JD-Core Version:    0.6.0
 */