/*     */ package org.apache.mina.core.write;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.mina.core.future.IoFutureListener;
/*     */ import org.apache.mina.core.future.WriteFuture;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ 
/*     */ public class DefaultWriteRequest
/*     */   implements WriteRequest
/*     */ {
/*  36 */   private static final WriteFuture UNUSED_FUTURE = new WriteFuture() {
/*     */     public boolean isWritten() {
/*  38 */       return false;
/*     */     }
/*     */ 
/*     */     public void setWritten() {
/*     */     }
/*     */ 
/*     */     public IoSession getSession() {
/*  45 */       return null;
/*     */     }
/*     */ 
/*     */     public void join() {
/*     */     }
/*     */ 
/*     */     public boolean join(long timeoutInMillis) {
/*  52 */       return true;
/*     */     }
/*     */ 
/*     */     public boolean isDone() {
/*  56 */       return true;
/*     */     }
/*     */ 
/*     */     public WriteFuture addListener(IoFutureListener<?> listener) {
/*  60 */       throw new IllegalStateException("You can't add a listener to a dummy future.");
/*     */     }
/*     */ 
/*     */     public WriteFuture removeListener(IoFutureListener<?> listener)
/*     */     {
/*  65 */       throw new IllegalStateException("You can't add a listener to a dummy future.");
/*     */     }
/*     */ 
/*     */     public WriteFuture await() throws InterruptedException
/*     */     {
/*  70 */       return this;
/*     */     }
/*     */ 
/*     */     public boolean await(long timeout, TimeUnit unit) throws InterruptedException
/*     */     {
/*  75 */       return true;
/*     */     }
/*     */ 
/*     */     public boolean await(long timeoutMillis) throws InterruptedException {
/*  79 */       return true;
/*     */     }
/*     */ 
/*     */     public WriteFuture awaitUninterruptibly() {
/*  83 */       return this;
/*     */     }
/*     */ 
/*     */     public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
/*  87 */       return true;
/*     */     }
/*     */ 
/*     */     public boolean awaitUninterruptibly(long timeoutMillis) {
/*  91 */       return true;
/*     */     }
/*     */ 
/*     */     public Throwable getException() {
/*  95 */       return null;
/*     */     }
/*     */ 
/*     */     public void setException(Throwable cause)
/*     */     {
/*     */     }
/*  36 */   };
/*     */   private final Object message;
/*     */   private final WriteFuture future;
/*     */   private final SocketAddress destination;
/*     */ 
/*     */   public DefaultWriteRequest(Object message)
/*     */   {
/* 112 */     this(message, null, null);
/*     */   }
/*     */ 
/*     */   public DefaultWriteRequest(Object message, WriteFuture future)
/*     */   {
/* 119 */     this(message, future, null);
/*     */   }
/*     */ 
/*     */   public DefaultWriteRequest(Object message, WriteFuture future, SocketAddress destination)
/*     */   {
/* 132 */     if (message == null) {
/* 133 */       throw new NullPointerException("message");
/*     */     }
/*     */ 
/* 136 */     if (future == null) {
/* 137 */       future = UNUSED_FUTURE;
/*     */     }
/*     */ 
/* 140 */     this.message = message;
/* 141 */     this.future = future;
/* 142 */     this.destination = destination;
/*     */   }
/*     */ 
/*     */   public WriteFuture getFuture() {
/* 146 */     return this.future;
/*     */   }
/*     */ 
/*     */   public Object getMessage() {
/* 150 */     return this.message;
/*     */   }
/*     */ 
/*     */   public WriteRequest getOriginalRequest() {
/* 154 */     return this;
/*     */   }
/*     */ 
/*     */   public SocketAddress getDestination() {
/* 158 */     return this.destination;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 163 */     if (getDestination() == null) {
/* 164 */       return this.message.toString();
/*     */     }
/* 166 */     return this.message.toString() + " => " + getDestination();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.write.DefaultWriteRequest
 * JD-Core Version:    0.6.0
 */