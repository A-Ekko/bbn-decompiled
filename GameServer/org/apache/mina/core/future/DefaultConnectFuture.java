/*     */ package org.apache.mina.core.future;
/*     */ 
/*     */ import org.apache.mina.core.RuntimeIoException;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ 
/*     */ public class DefaultConnectFuture extends DefaultIoFuture
/*     */   implements ConnectFuture
/*     */ {
/*  35 */   private static final Object CANCELED = new Object();
/*     */ 
/*     */   public static ConnectFuture newFailedFuture(Throwable exception)
/*     */   {
/*  41 */     DefaultConnectFuture failedFuture = new DefaultConnectFuture();
/*  42 */     failedFuture.setException(exception);
/*  43 */     return failedFuture;
/*     */   }
/*     */ 
/*     */   public DefaultConnectFuture()
/*     */   {
/*  50 */     super(null);
/*     */   }
/*     */ 
/*     */   public IoSession getSession()
/*     */   {
/*  55 */     Object v = getValue();
/*  56 */     if ((v instanceof RuntimeException))
/*  57 */       throw ((RuntimeException)v);
/*  58 */     if ((v instanceof Error))
/*  59 */       throw ((Error)v);
/*  60 */     if ((v instanceof Throwable)) {
/*  61 */       throw ((RuntimeIoException)new RuntimeIoException("Failed to get the session.").initCause((Throwable)v));
/*     */     }
/*  63 */     if ((v instanceof IoSession)) {
/*  64 */       return (IoSession)v;
/*     */     }
/*  66 */     return null;
/*     */   }
/*     */ 
/*     */   public Throwable getException()
/*     */   {
/*  71 */     Object v = getValue();
/*  72 */     if ((v instanceof Throwable)) {
/*  73 */       return (Throwable)v;
/*     */     }
/*  75 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isConnected()
/*     */   {
/*  80 */     return getValue() instanceof IoSession;
/*     */   }
/*     */ 
/*     */   public boolean isCanceled() {
/*  84 */     return getValue() == CANCELED;
/*     */   }
/*     */ 
/*     */   public void setSession(IoSession session) {
/*  88 */     if (session == null) {
/*  89 */       throw new NullPointerException("session");
/*     */     }
/*  91 */     setValue(session);
/*     */   }
/*     */ 
/*     */   public void setException(Throwable exception) {
/*  95 */     if (exception == null) {
/*  96 */       throw new NullPointerException("exception");
/*     */     }
/*  98 */     setValue(exception);
/*     */   }
/*     */ 
/*     */   public void cancel() {
/* 102 */     setValue(CANCELED);
/*     */   }
/*     */ 
/*     */   public ConnectFuture await() throws InterruptedException
/*     */   {
/* 107 */     return (ConnectFuture)super.await();
/*     */   }
/*     */ 
/*     */   public ConnectFuture awaitUninterruptibly()
/*     */   {
/* 112 */     return (ConnectFuture)super.awaitUninterruptibly();
/*     */   }
/*     */ 
/*     */   public ConnectFuture addListener(IoFutureListener<?> listener)
/*     */   {
/* 117 */     return (ConnectFuture)super.addListener(listener);
/*     */   }
/*     */ 
/*     */   public ConnectFuture removeListener(IoFutureListener<?> listener)
/*     */   {
/* 122 */     return (ConnectFuture)super.removeListener(listener);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.future.DefaultConnectFuture
 * JD-Core Version:    0.6.0
 */