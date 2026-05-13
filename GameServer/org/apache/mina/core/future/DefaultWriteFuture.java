/*     */ package org.apache.mina.core.future;
/*     */ 
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ 
/*     */ public class DefaultWriteFuture extends DefaultIoFuture
/*     */   implements WriteFuture
/*     */ {
/*     */   public static WriteFuture newWrittenFuture(IoSession session)
/*     */   {
/*  36 */     DefaultWriteFuture unwrittenFuture = new DefaultWriteFuture(session);
/*  37 */     unwrittenFuture.setWritten();
/*  38 */     return unwrittenFuture;
/*     */   }
/*     */ 
/*     */   public static WriteFuture newNotWrittenFuture(IoSession session, Throwable cause)
/*     */   {
/*  45 */     DefaultWriteFuture unwrittenFuture = new DefaultWriteFuture(session);
/*  46 */     unwrittenFuture.setException(cause);
/*  47 */     return unwrittenFuture;
/*     */   }
/*     */ 
/*     */   public DefaultWriteFuture(IoSession session)
/*     */   {
/*  54 */     super(session);
/*     */   }
/*     */ 
/*     */   public boolean isWritten()
/*     */   {
/*  61 */     if (isDone()) {
/*  62 */       Object v = getValue();
/*  63 */       if ((v instanceof Boolean)) {
/*  64 */         return ((Boolean)v).booleanValue();
/*     */       }
/*     */     }
/*  67 */     return false;
/*     */   }
/*     */ 
/*     */   public Throwable getException()
/*     */   {
/*  74 */     if (isDone()) {
/*  75 */       Object v = getValue();
/*  76 */       if ((v instanceof Throwable)) {
/*  77 */         return (Throwable)v;
/*     */       }
/*     */     }
/*  80 */     return null;
/*     */   }
/*     */ 
/*     */   public void setWritten()
/*     */   {
/*  87 */     setValue(Boolean.TRUE);
/*     */   }
/*     */ 
/*     */   public void setException(Throwable exception)
/*     */   {
/*  94 */     if (exception == null) {
/*  95 */       throw new NullPointerException("exception");
/*     */     }
/*     */ 
/*  98 */     setValue(exception);
/*     */   }
/*     */ 
/*     */   public WriteFuture await()
/*     */     throws InterruptedException
/*     */   {
/* 106 */     return (WriteFuture)super.await();
/*     */   }
/*     */ 
/*     */   public WriteFuture awaitUninterruptibly()
/*     */   {
/* 114 */     return (WriteFuture)super.awaitUninterruptibly();
/*     */   }
/*     */ 
/*     */   public WriteFuture addListener(IoFutureListener<?> listener)
/*     */   {
/* 122 */     return (WriteFuture)super.addListener(listener);
/*     */   }
/*     */ 
/*     */   public WriteFuture removeListener(IoFutureListener<?> listener)
/*     */   {
/* 130 */     return (WriteFuture)super.removeListener(listener);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.future.DefaultWriteFuture
 * JD-Core Version:    0.6.0
 */