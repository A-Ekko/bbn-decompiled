package flex.messaging.io;

import java.util.List;

public abstract interface PropertyProxy
{
  public abstract Object getDefaultInstance();

  public abstract void setDefaultInstance(Object paramObject);

  public abstract Object createInstance(String paramString);

  public abstract List getPropertyNames();

  public abstract List getPropertyNames(Object paramObject);

  public abstract Class getType(String paramString);

  public abstract Class getType(Object paramObject, String paramString);

  public abstract Object getValue(String paramString);

  public abstract Object getValue(Object paramObject, String paramString);

  public abstract void setValue(String paramString, Object paramObject);

  public abstract void setValue(Object paramObject1, String paramString, Object paramObject2);

  public abstract Object instanceComplete(Object paramObject);

  public abstract void setAlias(String paramString);

  public abstract String getAlias();

  public abstract String getAlias(Object paramObject);

  public abstract void setDynamic(boolean paramBoolean);

  public abstract boolean isDynamic();

  public abstract boolean isExternalizable();

  public abstract boolean isExternalizable(Object paramObject);

  public abstract void setExternalizable(boolean paramBoolean);

  public abstract SerializationContext getSerializationContext();

  public abstract void setSerializationContext(SerializationContext paramSerializationContext);

  public abstract void setIncludeReadOnly(boolean paramBoolean);

  public abstract boolean getIncludeReadOnly();

  public abstract SerializationDescriptor getDescriptor();

  public abstract void setDescriptor(SerializationDescriptor paramSerializationDescriptor);

  public abstract Object clone();

  public abstract Object getInstanceToSerialize(Object paramObject);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.PropertyProxy
 * JD-Core Version:    0.6.0
 */