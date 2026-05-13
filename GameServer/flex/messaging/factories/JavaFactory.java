/*     */ package flex.messaging.factories;
/*     */ 
/*     */ import flex.messaging.DestructibleFlexFactory;
/*     */ import flex.messaging.FactoryInstance;
/*     */ import flex.messaging.FlexContext;
/*     */ import flex.messaging.FlexFactory;
/*     */ import flex.messaging.FlexSession;
/*     */ import flex.messaging.MessageBroker;
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.config.ConfigurationException;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.services.ServiceException;
/*     */ import flex.messaging.util.ExceptionUtil;
/*     */ import flex.messaging.util.StringUtils;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ 
/*     */ public class JavaFactory
/*     */   implements FlexFactory, DestructibleFlexFactory
/*     */ {
/*     */   private static final String ATTRIBUTE_ID = "attribute-id";
/*     */   private static final int SINGLETON_ERROR = 10656;
/*     */   private static final int SESSION_NOT_FOUND = 10652;
/*     */   private static final int INVALID_CLASS_FOUND = 10654;
/*     */ 
/*     */   public void initialize(String id, ConfigMap configMap)
/*     */   {
/*     */   }
/*     */ 
/*     */   public FactoryInstance createFactoryInstance(String id, ConfigMap properties)
/*     */   {
/*  85 */     JavaFactoryInstance instance = new JavaFactoryInstance(this, id, properties);
/*     */ 
/*  87 */     if (properties == null)
/*     */     {
/*  90 */       instance.setSource(instance.getId());
/*  91 */       instance.setScope("request");
/*  92 */       instance.setAttributeId(id);
/*     */     }
/*     */     else
/*     */     {
/*  96 */       instance.setSource(properties.getPropertyAsString("source", instance.getId()));
/*  97 */       instance.setScope(properties.getPropertyAsString("scope", "request"));
/*     */ 
/*  99 */       instance.setAttributeId(properties.getPropertyAsString("attribute-id", id));
/*     */     }
/*     */ 
/* 102 */     if (instance.getScope().equalsIgnoreCase("application"))
/*     */     {
/*     */       try
/*     */       {
/* 106 */         ServletContext ctx = FlexContext.getServletConfig().getServletContext();
/*     */ 
/* 108 */         synchronized (ctx)
/*     */         {
/* 110 */           Object inst = ctx.getAttribute(instance.getAttributeId());
/* 111 */           if (inst == null)
/*     */           {
/* 113 */             inst = instance.createInstance();
/* 114 */             ctx.setAttribute(instance.getAttributeId(), inst);
/*     */           }
/*     */           else
/*     */           {
/* 118 */             Class configuredClass = instance.getInstanceClass();
/* 119 */             Class instClass = inst.getClass();
/* 120 */             if ((configuredClass != instClass) && (!configuredClass.isAssignableFrom(instClass)))
/*     */             {
/* 123 */               ServiceException e = new ServiceException();
/* 124 */               e.setMessage(10654, new Object[] { instance.getAttributeId(), "application", instance.getId(), instance.getInstanceClass(), inst.getClass() });
/*     */ 
/* 127 */               e.setCode("Server.Processing");
/* 128 */               throw e;
/*     */             }
/*     */           }
/* 131 */           instance.applicationInstance = inst;
/*     */ 
/* 134 */           MessageBroker mb = FlexContext.getMessageBroker();
/* 135 */           if (mb != null)
/*     */           {
/* 137 */             mb.incrementAttributeIdRefCount(instance.getAttributeId());
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 143 */         ConfigurationException ex = new ConfigurationException();
/* 144 */         ex.setMessage(10656, new Object[] { instance.getSource(), id });
/* 145 */         ex.setRootCause(t);
/*     */ 
/* 147 */         if (Log.isError()) {
/* 148 */           Log.getLogger("Configuration").error(ex.getMessage() + StringUtils.NEWLINE + ExceptionUtil.toString(t));
/*     */         }
/* 150 */         throw ex;
/*     */       }
/*     */     }
/* 153 */     else if (instance.getScope().equalsIgnoreCase("session"))
/*     */     {
/* 156 */       MessageBroker mb = FlexContext.getMessageBroker();
/* 157 */       if (mb != null)
/*     */       {
/* 159 */         mb.incrementAttributeIdRefCount(instance.getAttributeId());
/*     */       }
/*     */     }
/* 162 */     return instance;
/*     */   }
/*     */ 
/*     */   public Object lookup(FactoryInstance inst)
/*     */   {
/* 183 */     JavaFactoryInstance factoryInstance = (JavaFactoryInstance)inst;
/*     */     Object instance;
/*     */     Object instance;
/* 186 */     if (factoryInstance.getScope().equalsIgnoreCase("application"))
/*     */     {
/* 188 */       instance = factoryInstance.applicationInstance;
/*     */     }
/* 190 */     else if (factoryInstance.getScope().equalsIgnoreCase("session"))
/*     */     {
/* 193 */       FlexSession session = FlexContext.getFlexSession();
/*     */       Object instance;
/* 194 */       if (session != null)
/*     */       {
/* 196 */         Object instance = session.getAttribute(factoryInstance.getAttributeId());
/* 197 */         if (instance != null)
/*     */         {
/* 199 */           Class configuredClass = factoryInstance.getInstanceClass();
/* 200 */           Class instClass = instance.getClass();
/* 201 */           if ((configuredClass != instClass) && (!configuredClass.isAssignableFrom(instClass)))
/*     */           {
/* 204 */             ServiceException e = new ServiceException();
/* 205 */             e.setMessage(10654, new Object[] { factoryInstance.getAttributeId(), "session", factoryInstance.getId(), factoryInstance.getInstanceClass(), instance.getClass() });
/*     */ 
/* 210 */             e.setCode("Server.Processing");
/* 211 */             throw e;
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 217 */           instance = factoryInstance.createInstance();
/* 218 */           session.setAttribute(factoryInstance.getAttributeId(), instance);
/*     */         }
/*     */       }
/*     */       else {
/* 222 */         instance = null;
/*     */       }
/* 224 */       if (instance == null)
/*     */       {
/* 226 */         ServiceException e = new ServiceException();
/* 227 */         e.setMessage(10652, new Object[] { factoryInstance.getId() });
/* 228 */         e.setCode("Server.Processing");
/* 229 */         throw e;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 234 */       instance = factoryInstance.createInstance();
/*     */     }
/* 236 */     return instance;
/*     */   }
/*     */ 
/*     */   public void destroyFactoryInstance(FactoryInstance inst)
/*     */   {
/* 248 */     JavaFactoryInstance factoryInstance = (JavaFactoryInstance)inst;
/*     */ 
/* 252 */     if ((factoryInstance != null) && (("application".equals(factoryInstance.getScope())) || ("session".equals(factoryInstance.getScope()))))
/*     */     {
/* 255 */       MessageBroker mb = FlexContext.getMessageBroker();
/* 256 */       String attributeId = factoryInstance.getAttributeId();
/*     */ 
/* 258 */       if ("application".equals(factoryInstance.getScope()))
/*     */       {
/* 261 */         ServletContext ctx = FlexContext.getServletConfig().getServletContext();
/*     */ 
/* 264 */         if ((ctx == null) || (mb == null)) {
/* 265 */           return;
/*     */         }
/* 267 */         synchronized (ctx)
/*     */         {
/* 270 */           int refCount = mb.decrementAttributeIdRefCount(attributeId);
/* 271 */           if (refCount == 0)
/*     */           {
/* 274 */             ctx.removeAttribute(attributeId);
/*     */           }
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 280 */         FlexSession session = FlexContext.getFlexSession();
/*     */ 
/* 285 */         if (session == null) {
/* 286 */           return;
/*     */         }
/*     */ 
/* 289 */         int refCount = mb.decrementAttributeIdRefCount(attributeId);
/* 290 */         if (refCount == 0)
/*     */         {
/* 293 */           session.removeAttribute(attributeId);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.factories.JavaFactory
 * JD-Core Version:    0.6.0
 */