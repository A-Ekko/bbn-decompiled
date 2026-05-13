/*     */ package org.apache.mina.core.future;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.mina.core.RuntimeIoException;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ 
/*     */ public class DefaultReadFuture extends DefaultIoFuture
/*     */   implements ReadFuture
/*     */ {
/*  36 */   private static final Object CLOSED = new Object();
/*     */ 
/*     */   public DefaultReadFuture(IoSession session)
/*     */   {
/*  42 */     super(session);
/*     */   }
/*     */ 
/*     */   public Object getMessage() {
/*  46 */     if (isDone()) {
/*  47 */       Object v = getValue();
/*  48 */       if (v == CLOSED) {
/*  49 */         return null;
/*     */       }
/*     */ 
/*  52 */       if ((v instanceof ExceptionHolder)) {
/*  53 */         v = ((ExceptionHolder)v).exception;
/*  54 */         if ((v instanceof RuntimeException)) {
/*  55 */           throw ((RuntimeException)v);
/*     */         }
/*  57 */         if ((v instanceof Error)) {
/*  58 */           throw ((Error)v);
/*     */         }
/*  60 */         if (((v instanceof IOException)) || ((v instanceof Exception))) {
/*  61 */           throw new RuntimeIoException((Exception)v);
/*     */         }
/*     */       }
/*     */ 
/*  65 */       return v;
/*     */     }
/*     */ 
/*  68 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isRead()
/*     */   {
/*  73 */     if (isDone()) {
/*  74 */       Object v = getValue();
/*  75 */       return (v != CLOSED) && (!(v instanceof ExceptionHolder));
/*     */     }
/*  77 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isClosed() {
/*  81 */     if (isDone()) {
/*  82 */       return getValue() == CLOSED;
/*     */     }
/*  84 */     return false;
/*     */   }
/*     */ 
/*     */   public Throwable getException() {
/*  88 */     if (isDone()) {
/*  89 */       Object v = getValue();
/*  90 */       if ((v instanceof ExceptionHolder)) {
/*  91 */         return ((ExceptionHolder)v).exception;
/*     */       }
/*     */     }
/*  94 */     return null;
/*     */   }
/*     */ 
/*     */   public void setClosed() {
/*  98 */     setValue(CLOSED);
/*     */   }
/*     */ 
/*     */   public void setRead(Object message) {
/* 102 */     if (message == null) {
/* 103 */       throw new NullPointerException("message");
/*     */     }
/* 105 */     setValue(message);
/*     */   }
/*     */ 
/*     */   public void setException(Throwable exception) {
/* 109 */     if (exception == null) {
/* 110 */       throw new NullPointerException("exception");
/*     */     }
/*     */ 
/* 113 */     setValue(new ExceptionHolder(exception, null));
/*     */   }
/*     */ 
/*     */   public ReadFuture await() throws InterruptedException
/*     */   {
/* 118 */     return (ReadFuture)super.await();
/*     */   }
/*     */ 
/*     */   public ReadFuture awaitUninterruptibly()
/*     */   {
/* 123 */     return (ReadFuture)super.awaitUninterruptibly();
/*     */   }
/*     */ 
/*     */   public ReadFuture addListener(IoFutureListener<?> listener)
/*     */   {
/* 128 */     return (ReadFuture)super.addListener(listener);
/*     */   }
/*     */ 
/*     */   public ReadFuture removeListener(IoFutureListener<?> listener)
/*     */   {
/* 133 */     return (ReadFuture)super.removeListener(listener);
/*     */   }
/*     */   private static class ExceptionHolder {
/*     */     private final Throwable exception;
/*     */ 
/*     */     private ExceptionHolder(Throwable exception) {
/* 140 */       this.exception = exception;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.future.DefaultReadFuture
 * JD-Core Version:    0.6.0
 */