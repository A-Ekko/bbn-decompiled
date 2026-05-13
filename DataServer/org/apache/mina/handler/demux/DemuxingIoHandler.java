/*     */ package org.apache.mina.handler.demux;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.mina.core.service.IoHandlerAdapter;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.session.UnknownMessageTypeException;
/*     */ import org.apache.mina.util.IdentityHashSet;
/*     */ 
/*     */ public class DemuxingIoHandler extends IoHandlerAdapter
/*     */ {
/*  82 */   private final Map<Class<?>, MessageHandler<?>> receivedMessageHandlerCache = new ConcurrentHashMap();
/*     */ 
/*  85 */   private final Map<Class<?>, MessageHandler<?>> receivedMessageHandlers = new ConcurrentHashMap();
/*     */ 
/*  88 */   private final Map<Class<?>, MessageHandler<?>> sentMessageHandlerCache = new ConcurrentHashMap();
/*     */ 
/*  91 */   private final Map<Class<?>, MessageHandler<?>> sentMessageHandlers = new ConcurrentHashMap();
/*     */ 
/*  94 */   private final Map<Class<?>, ExceptionHandler<?>> exceptionHandlerCache = new ConcurrentHashMap();
/*     */ 
/*  97 */   private final Map<Class<?>, ExceptionHandler<?>> exceptionHandlers = new ConcurrentHashMap();
/*     */ 
/*     */   public <E> MessageHandler<? super E> addReceivedMessageHandler(Class<E> type, MessageHandler<? super E> handler)
/*     */   {
/* 116 */     this.receivedMessageHandlerCache.clear();
/* 117 */     return (MessageHandler)this.receivedMessageHandlers.put(type, handler);
/*     */   }
/*     */ 
/*     */   public <E> MessageHandler<? super E> removeReceivedMessageHandler(Class<E> type)
/*     */   {
/* 128 */     this.receivedMessageHandlerCache.clear();
/* 129 */     return (MessageHandler)this.receivedMessageHandlers.remove(type);
/*     */   }
/*     */ 
/*     */   public <E> MessageHandler<? super E> addSentMessageHandler(Class<E> type, MessageHandler<? super E> handler)
/*     */   {
/* 142 */     this.sentMessageHandlerCache.clear();
/* 143 */     return (MessageHandler)this.sentMessageHandlers.put(type, handler);
/*     */   }
/*     */ 
/*     */   public <E> MessageHandler<? super E> removeSentMessageHandler(Class<E> type)
/*     */   {
/* 154 */     this.sentMessageHandlerCache.clear();
/* 155 */     return (MessageHandler)this.sentMessageHandlers.remove(type);
/*     */   }
/*     */ 
/*     */   public <E extends Throwable> ExceptionHandler<? super E> addExceptionHandler(Class<E> type, ExceptionHandler<? super E> handler)
/*     */   {
/* 169 */     this.exceptionHandlerCache.clear();
/* 170 */     return (ExceptionHandler)this.exceptionHandlers.put(type, handler);
/*     */   }
/*     */ 
/*     */   public <E extends Throwable> ExceptionHandler<? super E> removeExceptionHandler(Class<E> type)
/*     */   {
/* 182 */     this.exceptionHandlerCache.clear();
/* 183 */     return (ExceptionHandler)this.exceptionHandlers.remove(type);
/*     */   }
/*     */ 
/*     */   public <E> MessageHandler<? super E> getMessageHandler(Class<E> type)
/*     */   {
/* 192 */     return (MessageHandler)this.receivedMessageHandlers.get(type);
/*     */   }
/*     */ 
/*     */   public Map<Class<?>, MessageHandler<?>> getReceivedMessageHandlerMap()
/*     */   {
/* 200 */     return Collections.unmodifiableMap(this.receivedMessageHandlers);
/*     */   }
/*     */ 
/*     */   public Map<Class<?>, MessageHandler<?>> getSentMessageHandlerMap()
/*     */   {
/* 208 */     return Collections.unmodifiableMap(this.sentMessageHandlers);
/*     */   }
/*     */ 
/*     */   public Map<Class<?>, ExceptionHandler<?>> getExceptionHandlerMap()
/*     */   {
/* 216 */     return Collections.unmodifiableMap(this.exceptionHandlers);
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoSession session, Object message)
/*     */     throws Exception
/*     */   {
/* 230 */     MessageHandler handler = findReceivedMessageHandler(message.getClass());
/* 231 */     if (handler != null)
/* 232 */       handler.handleMessage(session, message);
/*     */     else
/* 234 */       throw new UnknownMessageTypeException("No message handler found for message type: " + message.getClass().getSimpleName());
/*     */   }
/*     */ 
/*     */   public void messageSent(IoSession session, Object message)
/*     */     throws Exception
/*     */   {
/* 249 */     MessageHandler handler = findSentMessageHandler(message.getClass());
/* 250 */     if (handler != null)
/* 251 */       handler.handleMessage(session, message);
/*     */     else
/* 253 */       throw new UnknownMessageTypeException("No handler found for message type: " + message.getClass().getSimpleName());
/*     */   }
/*     */ 
/*     */   public void exceptionCaught(IoSession session, Throwable cause)
/*     */     throws Exception
/*     */   {
/* 270 */     ExceptionHandler handler = findExceptionHandler(cause.getClass());
/* 271 */     if (handler != null)
/* 272 */       handler.exceptionCaught(session, cause);
/*     */     else
/* 274 */       throw new UnknownMessageTypeException("No handler found for exception type: " + cause.getClass().getSimpleName());
/*     */   }
/*     */ 
/*     */   protected MessageHandler<Object> findReceivedMessageHandler(Class<?> type)
/*     */   {
/* 281 */     return findReceivedMessageHandler(type, null);
/*     */   }
/*     */ 
/*     */   protected MessageHandler<Object> findSentMessageHandler(Class<?> type) {
/* 285 */     return findSentMessageHandler(type, null);
/*     */   }
/*     */ 
/*     */   protected ExceptionHandler<Throwable> findExceptionHandler(Class<? extends Throwable> type) {
/* 289 */     return findExceptionHandler(type, null);
/*     */   }
/*     */ 
/*     */   private MessageHandler<Object> findReceivedMessageHandler(Class type, Set<Class> triedClasses)
/*     */   {
/* 296 */     return (MessageHandler)findHandler(this.receivedMessageHandlers, this.receivedMessageHandlerCache, type, triedClasses);
/*     */   }
/*     */ 
/*     */   private MessageHandler<Object> findSentMessageHandler(Class type, Set<Class> triedClasses)
/*     */   {
/* 304 */     return (MessageHandler)findHandler(this.sentMessageHandlers, this.sentMessageHandlerCache, type, triedClasses);
/*     */   }
/*     */ 
/*     */   private ExceptionHandler<Throwable> findExceptionHandler(Class type, Set<Class> triedClasses)
/*     */   {
/* 312 */     return (ExceptionHandler)findHandler(this.exceptionHandlers, this.exceptionHandlerCache, type, triedClasses);
/*     */   }
/*     */ 
/*     */   private Object findHandler(Map handlers, Map handlerCache, Class type, Set<Class> triedClasses)
/*     */   {
/* 321 */     Object handler = null;
/*     */ 
/* 323 */     if ((triedClasses != null) && (triedClasses.contains(type))) {
/* 324 */       return null;
/*     */     }
/*     */ 
/* 330 */     handler = handlerCache.get(type);
/* 331 */     if (handler != null) {
/* 332 */       return handler;
/*     */     }
/*     */ 
/* 338 */     handler = handlers.get(type);
/*     */ 
/* 340 */     if (handler == null)
/*     */     {
/* 345 */       if (triedClasses == null) {
/* 346 */         triedClasses = new IdentityHashSet();
/*     */       }
/* 348 */       triedClasses.add(type);
/*     */ 
/* 350 */       Class[] interfaces = type.getInterfaces();
/* 351 */       for (Class element : interfaces) {
/* 352 */         handler = findHandler(handlers, handlerCache, element, triedClasses);
/* 353 */         if (handler != null)
/*     */         {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/* 359 */     if (handler == null)
/*     */     {
/* 364 */       Class superclass = type.getSuperclass();
/* 365 */       if (superclass != null) {
/* 366 */         handler = findHandler(handlers, handlerCache, superclass, null);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 375 */     if (handler != null) {
/* 376 */       handlerCache.put(type, handler);
/*     */     }
/*     */ 
/* 379 */     return handler;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.handler.demux.DemuxingIoHandler
 * JD-Core Version:    0.6.0
 */