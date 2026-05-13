/*     */ package org.apache.mina.filter.reqres;
/*     */ 
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class Request
/*     */ {
/*     */   private final Object id;
/*     */   private final Object message;
/*     */   private final long timeoutMillis;
/*     */   private volatile Runnable timeoutTask;
/*     */   private volatile ScheduledFuture<?> timeoutFuture;
/*     */   private final BlockingQueue<Object> responses;
/*     */   private volatile boolean endOfResponses;
/*     */ 
/*     */   public Request(Object id, Object message, long timeoutMillis)
/*     */   {
/*  50 */     this(id, message, true, timeoutMillis);
/*     */   }
/*     */ 
/*     */   public Request(Object id, Object message, boolean useResponseQueue, long timeoutMillis)
/*     */   {
/*  55 */     this(id, message, useResponseQueue, timeoutMillis, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */   public Request(Object id, Object message, long timeout, TimeUnit unit)
/*     */   {
/*  60 */     this(id, message, true, timeout, unit);
/*     */   }
/*     */ 
/*     */   public Request(Object id, Object message, boolean useResponseQueue, long timeout, TimeUnit unit)
/*     */   {
/*  65 */     if (id == null) {
/*  66 */       throw new NullPointerException("id");
/*     */     }
/*  68 */     if (message == null) {
/*  69 */       throw new NullPointerException("message");
/*     */     }
/*  71 */     if (timeout < 0L) {
/*  72 */       throw new IllegalArgumentException("timeout: " + timeout + " (expected: 0+)");
/*     */     }
/*  74 */     if (timeout == 0L) {
/*  75 */       timeout = 9223372036854775807L;
/*     */     }
/*     */ 
/*  78 */     if (unit == null) {
/*  79 */       throw new NullPointerException("unit");
/*     */     }
/*     */ 
/*  82 */     this.id = id;
/*  83 */     this.message = message;
/*  84 */     this.responses = (useResponseQueue ? new LinkedBlockingQueue() : null);
/*  85 */     this.timeoutMillis = unit.toMillis(timeout);
/*     */   }
/*     */ 
/*     */   public Object getId() {
/*  89 */     return this.id;
/*     */   }
/*     */ 
/*     */   public Object getMessage() {
/*  93 */     return this.message;
/*     */   }
/*     */ 
/*     */   public long getTimeoutMillis() {
/*  97 */     return this.timeoutMillis;
/*     */   }
/*     */ 
/*     */   public boolean isUseResponseQueue() {
/* 101 */     return this.responses != null;
/*     */   }
/*     */ 
/*     */   public boolean hasResponse() {
/* 105 */     checkUseResponseQueue();
/* 106 */     return !this.responses.isEmpty();
/*     */   }
/*     */ 
/*     */   public Response awaitResponse() throws RequestTimeoutException, InterruptedException
/*     */   {
/* 111 */     checkUseResponseQueue();
/* 112 */     chechEndOfResponses();
/* 113 */     return convertToResponse(this.responses.take());
/*     */   }
/*     */ 
/*     */   public Response awaitResponse(long timeout, TimeUnit unit) throws RequestTimeoutException, InterruptedException
/*     */   {
/* 118 */     checkUseResponseQueue();
/* 119 */     chechEndOfResponses();
/* 120 */     return convertToResponse(this.responses.poll(timeout, unit));
/*     */   }
/*     */ 
/*     */   private Response convertToResponse(Object o) {
/* 124 */     if ((o instanceof Response)) {
/* 125 */       return (Response)o;
/*     */     }
/*     */ 
/* 128 */     if (o == null) {
/* 129 */       return null;
/*     */     }
/*     */ 
/* 132 */     throw ((RequestTimeoutException)o);
/*     */   }
/*     */ 
/*     */   public Response awaitResponseUninterruptibly() throws RequestTimeoutException
/*     */   {
/*     */     while (true)
/*     */       try {
/* 139 */         return awaitResponse();
/*     */       }
/*     */       catch (InterruptedException e) {
/*     */       }
/*     */   }
/*     */ 
/*     */   private void chechEndOfResponses() {
/* 146 */     if ((this.responses != null) && (this.endOfResponses) && (this.responses.isEmpty()))
/* 147 */       throw new NoSuchElementException("All responses has been retrieved already.");
/*     */   }
/*     */ 
/*     */   private void checkUseResponseQueue()
/*     */   {
/* 153 */     if (this.responses == null)
/* 154 */       throw new UnsupportedOperationException("Response queue is not available; useResponseQueue is false.");
/*     */   }
/*     */ 
/*     */   void signal(Response response)
/*     */   {
/* 160 */     signal0(response);
/* 161 */     if (response.getType() != ResponseType.PARTIAL)
/* 162 */       this.endOfResponses = true;
/*     */   }
/*     */ 
/*     */   void signal(RequestTimeoutException e)
/*     */   {
/* 167 */     signal0(e);
/* 168 */     this.endOfResponses = true;
/*     */   }
/*     */ 
/*     */   private void signal0(Object answer) {
/* 172 */     if (this.responses != null)
/* 173 */       this.responses.add(answer);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 179 */     return getId().hashCode();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 184 */     if (o == this) {
/* 185 */       return true;
/*     */     }
/*     */ 
/* 188 */     if (o == null) {
/* 189 */       return false;
/*     */     }
/*     */ 
/* 192 */     if (!(o instanceof Request)) {
/* 193 */       return false;
/*     */     }
/*     */ 
/* 196 */     Request that = (Request)o;
/* 197 */     return getId().equals(that.getId());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 202 */     String timeout = getTimeoutMillis() == 9223372036854775807L ? "max" : String.valueOf(getTimeoutMillis());
/*     */ 
/* 205 */     return "request: { id=" + getId() + ", timeout=" + timeout + ", message=" + getMessage() + " }";
/*     */   }
/*     */ 
/*     */   Runnable getTimeoutTask()
/*     */   {
/* 210 */     return this.timeoutTask;
/*     */   }
/*     */ 
/*     */   void setTimeoutTask(Runnable timeoutTask) {
/* 214 */     this.timeoutTask = timeoutTask;
/*     */   }
/*     */ 
/*     */   ScheduledFuture<?> getTimeoutFuture() {
/* 218 */     return this.timeoutFuture;
/*     */   }
/*     */ 
/*     */   void setTimeoutFuture(ScheduledFuture<?> timeoutFuture) {
/* 222 */     this.timeoutFuture = timeoutFuture;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.reqres.Request
 * JD-Core Version:    0.6.0
 */