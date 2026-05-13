/*     */ package flex.messaging.io;
/*     */ 
/*     */ import flex.messaging.io.amf.ASObject;
/*     */ import flex.messaging.util.ClassUtil;
/*     */ import java.io.Externalizable;
/*     */ import java.io.Serializable;
/*     */ import java.util.List;
/*     */ 
/*     */ public abstract class AbstractProxy
/*     */   implements PropertyProxy, Serializable
/*     */ {
/*     */   protected Object defaultInstance;
/*     */   protected String alias;
/*     */   protected boolean dynamic;
/*     */   protected boolean externalizable;
/*     */   protected boolean includeReadOnly;
/*     */   protected SerializationDescriptor descriptor;
/*     */   protected SerializationContext context;
/*     */ 
/*     */   protected AbstractProxy(Object defaultInstance)
/*     */   {
/*  48 */     this.defaultInstance = defaultInstance;
/*  49 */     if (defaultInstance != null)
/*     */     {
/*  51 */       this.alias = defaultInstance.getClass().getName();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object getDefaultInstance()
/*     */   {
/*  57 */     return this.defaultInstance;
/*     */   }
/*     */ 
/*     */   public void setDefaultInstance(Object instance)
/*     */   {
/*  62 */     this.defaultInstance = instance;
/*     */   }
/*     */ 
/*     */   public static Class getClassFromClassName(String className)
/*     */   {
/*  71 */     TypeMarshallingContext typeContext = TypeMarshallingContext.getTypeMarshallingContext();
/*  72 */     return ClassUtil.createClass(className, typeContext.getClassLoader());
/*     */   }
/*     */ 
/*     */   public static Object createInstanceFromClassName(String className)
/*     */   {
/*  81 */     Class desiredClass = getClassFromClassName(className);
/*  82 */     return ClassUtil.createDefaultInstance(desiredClass, null);
/*     */   }
/*     */ 
/*     */   public Object createInstance(String className)
/*     */   {
/*     */     Object instance;
/*     */     Object instance;
/*  89 */     if ((className == null) || (className.length() == 0))
/*     */     {
/*  91 */       instance = new ASObject();
/*     */     }
/*  93 */     else if (className.startsWith(">"))
/*     */     {
/*  95 */       Object instance = new ASObject();
/*  96 */       ((ASObject)instance).setType(className);
/*     */     }
/*     */     else
/*     */     {
/* 100 */       SerializationContext context = getSerializationContext();
/* 101 */       if ((context.instantiateTypes) || (className.startsWith("flex.")))
/*     */       {
/* 103 */         return createInstanceFromClassName(className);
/*     */       }
/*     */ 
/* 108 */       instance = new ASObject();
/* 109 */       ((ASObject)instance).setType(className);
/*     */     }
/*     */ 
/* 112 */     return instance;
/*     */   }
/*     */ 
/*     */   public List getPropertyNames()
/*     */   {
/* 117 */     return getPropertyNames(getDefaultInstance());
/*     */   }
/*     */ 
/*     */   public Class getType(String propertyName)
/*     */   {
/* 122 */     return getType(getDefaultInstance(), propertyName);
/*     */   }
/*     */ 
/*     */   public Object getValue(String propertyName)
/*     */   {
/* 127 */     return getValue(getDefaultInstance(), propertyName);
/*     */   }
/*     */ 
/*     */   public void setValue(String propertyName, Object value)
/*     */   {
/* 132 */     setValue(getDefaultInstance(), propertyName, value);
/*     */   }
/*     */ 
/*     */   public void setAlias(String value)
/*     */   {
/* 137 */     this.alias = value;
/*     */   }
/*     */ 
/*     */   public String getAlias()
/*     */   {
/* 142 */     return this.alias;
/*     */   }
/*     */ 
/*     */   public void setDynamic(boolean value)
/*     */   {
/* 147 */     this.dynamic = value;
/*     */   }
/*     */ 
/*     */   public boolean isDynamic()
/*     */   {
/* 152 */     return this.dynamic;
/*     */   }
/*     */ 
/*     */   public boolean isExternalizable()
/*     */   {
/* 157 */     return this.externalizable;
/*     */   }
/*     */ 
/*     */   public void setExternalizable(boolean value)
/*     */   {
/* 162 */     this.externalizable = value;
/*     */   }
/*     */ 
/*     */   public boolean isExternalizable(Object instance)
/*     */   {
/* 167 */     return instance instanceof Externalizable;
/*     */   }
/*     */ 
/*     */   public SerializationContext getSerializationContext()
/*     */   {
/* 172 */     if (this.context == null)
/*     */     {
/* 174 */       return SerializationContext.getSerializationContext();
/*     */     }
/* 176 */     return this.context;
/*     */   }
/*     */ 
/*     */   public void setSerializationContext(SerializationContext value)
/*     */   {
/* 181 */     this.context = value;
/*     */   }
/*     */ 
/*     */   public void setIncludeReadOnly(boolean value)
/*     */   {
/* 186 */     this.includeReadOnly = value;
/*     */   }
/*     */ 
/*     */   public boolean getIncludeReadOnly()
/*     */   {
/* 191 */     return this.includeReadOnly;
/*     */   }
/*     */ 
/*     */   public SerializationDescriptor getDescriptor()
/*     */   {
/* 196 */     return this.descriptor;
/*     */   }
/*     */ 
/*     */   public void setDescriptor(SerializationDescriptor descriptor)
/*     */   {
/* 201 */     this.descriptor = descriptor;
/*     */   }
/*     */ 
/*     */   public Object instanceComplete(Object instance)
/*     */   {
/* 211 */     return instance;
/*     */   }
/*     */ 
/*     */   public Object getInstanceToSerialize(Object instance)
/*     */   {
/* 219 */     return instance;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 224 */     return null;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 229 */     if (this.defaultInstance != null) {
/* 230 */       return "[Proxy(inst=" + this.defaultInstance + ") proxyClass=" + getClass() + " descriptor=" + this.descriptor + "]";
/*     */     }
/* 232 */     return "[Proxy(proxyClass=" + getClass() + " descriptor=" + this.descriptor + "]";
/*     */   }
/*     */ 
/*     */   protected void setCloneFieldsFrom(AbstractProxy source)
/*     */   {
/* 237 */     setDescriptor(source.getDescriptor());
/* 238 */     setDefaultInstance(source.getDefaultInstance());
/* 239 */     this.context = source.context;
/* 240 */     this.includeReadOnly = source.includeReadOnly;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.AbstractProxy
 * JD-Core Version:    0.6.0
 */