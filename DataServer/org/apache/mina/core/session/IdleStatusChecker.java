/*     */ package org.apache.mina.core.session;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import org.apache.mina.core.future.CloseFuture;
/*     */ import org.apache.mina.core.future.IoFuture;
/*     */ import org.apache.mina.core.future.IoFutureListener;
/*     */ import org.apache.mina.util.ConcurrentHashSet;
/*     */ 
/*     */ public class IdleStatusChecker
/*     */ {
/*  43 */   private final Set<AbstractIoSession> sessions = new ConcurrentHashSet();
/*     */ 
/*  51 */   private final NotifyingTask notifyingTask = new NotifyingTask(null);
/*     */ 
/*  53 */   private final IoFutureListener<IoFuture> sessionCloseListener = new SessionCloseListener(null);
/*     */ 
/*     */   public void addSession(AbstractIoSession session)
/*     */   {
/*  63 */     this.sessions.add(session);
/*  64 */     CloseFuture closeFuture = session.getCloseFuture();
/*     */ 
/*  67 */     closeFuture.addListener(this.sessionCloseListener);
/*     */   }
/*     */ 
/*     */   private void removeSession(AbstractIoSession session)
/*     */   {
/*  75 */     this.sessions.remove(session);
/*     */   }
/*     */ 
/*     */   public NotifyingTask getNotifyingTask()
/*     */   {
/*  83 */     return this.notifyingTask;
/*     */   }
/*     */ 
/*     */   private class SessionCloseListener
/*     */     implements IoFutureListener<IoFuture>
/*     */   {
/*     */     private SessionCloseListener()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void operationComplete(IoFuture future)
/*     */     {
/* 142 */       IdleStatusChecker.this.removeSession((AbstractIoSession)future.getSession());
/*     */     }
/*     */   }
/*     */ 
/*     */   public class NotifyingTask
/*     */     implements Runnable
/*     */   {
/*     */     private volatile boolean cancelled;
/*     */     private volatile Thread thread;
/*     */ 
/*     */     private NotifyingTask()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*  99 */       this.thread = Thread.currentThread();
/*     */       try {
/* 101 */         while (!this.cancelled)
/*     */         {
/* 103 */           long currentTime = System.currentTimeMillis();
/*     */ 
/* 105 */           notifySessions(currentTime);
/*     */           try
/*     */           {
/* 108 */             Thread.sleep(1000L);
/*     */           } catch (InterruptedException e) {
/*     */           }
/*     */         }
/*     */       }
/*     */       finally {
/* 114 */         this.thread = null;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void cancel()
/*     */     {
/* 122 */       this.cancelled = true;
/* 123 */       Thread thread = this.thread;
/* 124 */       if (thread != null)
/* 125 */         thread.interrupt();
/*     */     }
/*     */ 
/*     */     private void notifySessions(long currentTime)
/*     */     {
/* 130 */       Iterator it = IdleStatusChecker.this.sessions.iterator();
/* 131 */       while (it.hasNext()) {
/* 132 */         AbstractIoSession session = (AbstractIoSession)it.next();
/* 133 */         if (session.isConnected())
/* 134 */           AbstractIoSession.notifyIdleSession(session, currentTime);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.session.IdleStatusChecker
 * JD-Core Version:    0.6.0
 */