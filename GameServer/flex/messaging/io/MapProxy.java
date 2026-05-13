/*     */ package flex.messaging.io;
/*     */ 
/*     */ import flex.messaging.MessageException;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class MapProxy extends BeanProxy
/*     */ {
/*     */   static final long serialVersionUID = 7857999941099335210L;
/*     */   private static final int NULL_KEY_ERROR = 10026;
/*     */ 
/*     */   public MapProxy()
/*     */   {
/*     */   }
/*     */ 
/*     */   public MapProxy(Object defaultInstance)
/*     */   {
/*  50 */     super(defaultInstance);
/*     */   }
/*     */ 
/*     */   public List getPropertyNames(Object instance)
/*     */   {
/*  56 */     if (instance == null) {
/*  57 */       return null;
/*     */     }
/*  59 */     List propertyNames = null;
/*  60 */     List excludes = null;
/*     */ 
/*  62 */     if (this.descriptor != null)
/*     */     {
/*  64 */       excludes = this.descriptor.getExcludesForInstance(instance);
/*  65 */       if (excludes == null) {
/*  66 */         excludes = this.descriptor.getExcludes();
/*     */       }
/*     */     }
/*     */ 
/*  70 */     if ((instance instanceof Map))
/*     */     {
/*  72 */       Map map = (Map)instance;
/*     */ 
/*  74 */       if (map.size() > 0)
/*     */       {
/*  76 */         propertyNames = new ArrayList(map.size());
/*  77 */         SerializationContext context = getSerializationContext();
/*     */ 
/*  79 */         Iterator it = map.keySet().iterator();
/*  80 */         while (it.hasNext())
/*     */         {
/*  82 */           Object key = it.next();
/*  83 */           if (key != null)
/*     */           {
/*  85 */             if ((excludes != null) && (excludes.contains(key))) {
/*     */               continue;
/*     */             }
/*  88 */             propertyNames.add(key.toString());
/*     */           }
/*     */           else
/*     */           {
/*  93 */             if ((Log.isWarn()) && (context.logPropertyErrors))
/*     */             {
/*  95 */               Logger log = Log.getLogger("Endpoint.Type");
/*  96 */               log.warn("Cannot send a null Map key for type {0}.", new Object[] { map.getClass().getName() });
/*     */             }
/*     */ 
/* 100 */             if (!context.ignorePropertyErrors)
/*     */             {
/* 103 */               MessageException ex = new MessageException();
/* 104 */               ex.setMessage(10026, new Object[] { map.getClass().getName() });
/* 105 */               throw ex;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 113 */     List beanProperties = super.getPropertyNames(instance);
/* 114 */     if (beanProperties != null)
/*     */     {
/* 116 */       if (propertyNames == null)
/*     */       {
/* 118 */         propertyNames = beanProperties;
/*     */       }
/*     */       else
/*     */       {
/* 122 */         propertyNames.addAll(beanProperties);
/*     */       }
/*     */     }
/*     */ 
/* 126 */     return propertyNames;
/*     */   }
/*     */ 
/*     */   public Object getValue(Object instance, String propertyName)
/*     */   {
/* 131 */     if ((instance == null) || (propertyName == null)) {
/* 132 */       return null;
/*     */     }
/* 134 */     Object value = null;
/*     */ 
/* 137 */     BeanProxy.BeanProperty bp = getBeanProperty(instance, propertyName);
/* 138 */     if (bp != null)
/*     */     {
/* 140 */       value = super.getBeanValue(instance, bp);
/*     */     }
/*     */ 
/* 144 */     if ((value == null) && ((instance instanceof Map)))
/*     */     {
/* 146 */       Map map = (Map)instance;
/* 147 */       value = map.get(propertyName);
/*     */     }
/*     */ 
/* 150 */     return value;
/*     */   }
/*     */ 
/*     */   public void setValue(Object instance, String propertyName, Object value)
/*     */   {
/* 155 */     if ((instance == null) || (propertyName == null)) {
/* 156 */       return;
/*     */     }
/* 158 */     Map props = getBeanProperties(instance);
/* 159 */     if (props.containsKey(propertyName))
/*     */     {
/* 161 */       super.setValue(instance, propertyName, value);
/*     */     }
/* 163 */     else if ((instance instanceof Map))
/*     */     {
/* 165 */       ((Map)instance).put(propertyName, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 171 */     MapProxy proxy = new MapProxy();
/* 172 */     proxy.setCloneFieldsFrom(this);
/* 173 */     return proxy;
/*     */   }
/*     */ 
/*     */   protected boolean ignorePropertyErrors(SerializationContext context)
/*     */   {
/* 178 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean logPropertyErrors(SerializationContext context)
/*     */   {
/* 183 */     return false;
/*     */   }
/*     */ 
/*     */   protected String getClassName(Object instance)
/*     */   {
/* 188 */     if ((instance != null) && ((instance instanceof Map)) && (instance.getClass().getName().startsWith("java.util.")))
/*     */     {
/* 191 */       return null;
/*     */     }
/* 193 */     return super.getClassName(instance);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.MapProxy
 * JD-Core Version:    0.6.0
 */