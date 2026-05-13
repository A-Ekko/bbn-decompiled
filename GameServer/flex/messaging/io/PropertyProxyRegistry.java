/*     */ package flex.messaging.io;
/*     */ 
/*     */ import flex.messaging.LocalizedException;
/*     */ import flex.messaging.MessageException;
/*     */ import flex.messaging.io.amf.ASObject;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.Dictionary;
/*     */ import java.util.HashMap;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Map;
/*     */ import javax.sql.RowSet;
/*     */ 
/*     */ public class PropertyProxyRegistry
/*     */ {
/*  47 */   private Map classRegistry = new IdentityHashMap();
/*     */ 
/*  52 */   private static final PropertyProxyRegistry registry = new PropertyProxyRegistry();
/*  53 */   private static boolean preregistered = false;
/*     */ 
/*     */   public static PropertyProxyRegistry getRegistry()
/*     */   {
/*  72 */     if (!preregistered)
/*     */     {
/*  74 */       preRegister();
/*  75 */       preregistered = true;
/*     */     }
/*     */ 
/*  78 */     return registry;
/*     */   }
/*     */ 
/*     */   private static void preRegister()
/*     */   {
/*  87 */     ThrowableProxy proxy = new ThrowableProxy();
/*  88 */     registry.register(MessageException.class, proxy);
/*  89 */     registry.register(LocalizedException.class, proxy);
/*  90 */     registry.register(Throwable.class, proxy);
/*     */ 
/*  92 */     MapProxy mapProxy = new MapProxy();
/*  93 */     registry.register(ASObject.class, mapProxy);
/*  94 */     registry.register(HashMap.class, mapProxy);
/*  95 */     registry.register(AbstractMap.class, mapProxy);
/*  96 */     registry.register(Map.class, mapProxy);
/*     */   }
/*     */ 
/*     */   public static PropertyProxy getProxyAndRegister(Object instance)
/*     */   {
/* 112 */     if ((instance instanceof PropertyProxy)) {
/* 113 */       return (PropertyProxy)instance;
/*     */     }
/* 115 */     Class c = instance.getClass();
/* 116 */     PropertyProxy proxy = getRegistry().getProxyAndRegister(c);
/*     */ 
/* 118 */     if (proxy == null)
/*     */     {
/* 120 */       proxy = guessProxy(instance);
/* 121 */       getRegistry().register(c, proxy);
/*     */     }
/*     */ 
/* 124 */     return proxy;
/*     */   }
/*     */ 
/*     */   public static PropertyProxy getProxy(Object instance)
/*     */   {
/* 140 */     if ((instance instanceof PropertyProxy)) {
/* 141 */       return (PropertyProxy)instance;
/*     */     }
/* 143 */     Class c = instance.getClass();
/* 144 */     PropertyProxy proxy = getRegistry().getProxy(c);
/*     */ 
/* 146 */     if (proxy == null)
/*     */     {
/* 148 */       proxy = guessProxy(instance);
/*     */     }
/*     */ 
/* 151 */     proxy = (PropertyProxy)proxy.clone();
/* 152 */     proxy.setDefaultInstance(instance);
/* 153 */     return proxy;
/*     */   }
/*     */ 
/*     */   private static PropertyProxy guessProxy(Object instance)
/*     */   {
/*     */     PropertyProxy proxy;
/*     */     PropertyProxy proxy;
/* 164 */     if ((instance instanceof Map))
/*     */     {
/* 166 */       proxy = new MapProxy();
/*     */     }
/*     */     else
/*     */     {
/*     */       PropertyProxy proxy;
/* 168 */       if ((instance instanceof Throwable))
/*     */       {
/* 170 */         proxy = new ThrowableProxy();
/*     */       }
/*     */       else
/*     */       {
/*     */         PropertyProxy proxy;
/* 172 */         if (((instance instanceof PageableRowSet)) || ((instance instanceof RowSet)))
/*     */         {
/* 174 */           proxy = new PageableRowSetProxy();
/*     */         }
/*     */         else
/*     */         {
/*     */           PropertyProxy proxy;
/* 176 */           if ((instance instanceof Dictionary))
/*     */           {
/* 178 */             proxy = new DictionaryProxy();
/*     */           }
/*     */           else
/*     */           {
/* 182 */             proxy = new BeanProxy();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 185 */     return proxy;
/*     */   }
/*     */ 
/*     */   public PropertyProxy getProxy(Class c)
/*     */   {
/* 199 */     return getProxy(c, true, false);
/*     */   }
/*     */ 
/*     */   public PropertyProxy getProxyAndRegister(Class c)
/*     */   {
/* 213 */     return getProxy(c, true, true);
/*     */   }
/*     */ 
/*     */   public PropertyProxy getProxy(Class c, boolean searchHierarchy, boolean autoRegister)
/*     */   {
/* 232 */     if (c == null) {
/* 233 */       return null;
/*     */     }
/*     */ 
/* 236 */     if (c.isArray()) {
/* 237 */       c = c.getComponentType();
/*     */     }
/*     */ 
/* 240 */     PropertyProxy proxy = (PropertyProxy)this.classRegistry.get(c);
/*     */ 
/* 242 */     if ((proxy == null) && (searchHierarchy))
/*     */     {
/* 245 */       Class[] interfaces = c.getInterfaces();
/* 246 */       for (int i = 0; i < interfaces.length; i++)
/*     */       {
/* 248 */         Class interfaceClass = interfaces[i];
/* 249 */         proxy = (PropertyProxy)this.classRegistry.get(interfaceClass);
/* 250 */         if ((proxy != null) && (autoRegister))
/*     */         {
/* 252 */           register(c, proxy);
/* 253 */           break;
/*     */         }
/*     */ 
/* 258 */         proxy = getProxy(interfaceClass, searchHierarchy, autoRegister);
/* 259 */         if (proxy != null)
/*     */         {
/*     */           break;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 267 */     if ((proxy == null) && (searchHierarchy))
/*     */     {
/* 270 */       Class superclass = c.getSuperclass();
/* 271 */       if (superclass != null)
/*     */       {
/* 273 */         proxy = getProxy(superclass, searchHierarchy, autoRegister);
/* 274 */         if ((proxy != null) && (autoRegister))
/*     */         {
/* 276 */           register(c, proxy);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 281 */     return proxy;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 289 */     synchronized (this.classRegistry)
/*     */     {
/* 291 */       this.classRegistry.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void register(Class c, PropertyProxy proxy)
/*     */   {
/* 303 */     synchronized (this.classRegistry)
/*     */     {
/* 305 */       this.classRegistry.put(c, proxy);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unregister(Class c)
/*     */   {
/* 316 */     synchronized (this.classRegistry)
/*     */     {
/* 318 */       this.classRegistry.remove(c);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.PropertyProxyRegistry
 * JD-Core Version:    0.6.0
 */