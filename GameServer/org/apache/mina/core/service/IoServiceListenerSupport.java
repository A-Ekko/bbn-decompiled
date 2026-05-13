/*     */ package org.apache.mina.core.service;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.future.CloseFuture;
/*     */ import org.apache.mina.core.future.IoFuture;
/*     */ import org.apache.mina.core.future.IoFutureListener;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.util.ExceptionMonitor;
/*     */ 
/*     */ public class IoServiceListenerSupport
/*     */ {
/*     */   private final IoService service;
/*  52 */   private final List<IoServiceListener> listeners = new CopyOnWriteArrayList();
/*     */ 
/*  57 */   private final ConcurrentMap<Long, IoSession> managedSessions = new ConcurrentHashMap();
/*     */ 
/*  62 */   private final Map<Long, IoSession> readOnlyManagedSessions = Collections.unmodifiableMap(this.managedSessions);
/*     */ 
/*  64 */   private final AtomicBoolean activated = new AtomicBoolean();
/*     */   private volatile long activationTime;
/*     */   private volatile int largestManagedSessionCount;
/*     */   private volatile long cumulativeManagedSessionCount;
/*     */ 
/*     */   public IoServiceListenerSupport(IoService service)
/*     */   {
/*  73 */     if (service == null) {
/*  74 */       throw new NullPointerException("service");
/*     */     }
/*  76 */     this.service = service;
/*     */   }
/*     */ 
/*     */   public void add(IoServiceListener listener)
/*     */   {
/*  83 */     this.listeners.add(listener);
/*     */   }
/*     */ 
/*     */   public void remove(IoServiceListener listener)
/*     */   {
/*  90 */     this.listeners.remove(listener);
/*     */   }
/*     */ 
/*     */   public long getActivationTime() {
/*  94 */     return this.activationTime;
/*     */   }
/*     */ 
/*     */   public Map<Long, IoSession> getManagedSessions() {
/*  98 */     return this.readOnlyManagedSessions;
/*     */   }
/*     */ 
/*     */   public int getManagedSessionCount() {
/* 102 */     return this.managedSessions.size();
/*     */   }
/*     */ 
/*     */   public int getLargestManagedSessionCount() {
/* 106 */     return this.largestManagedSessionCount;
/*     */   }
/*     */ 
/*     */   public long getCumulativeManagedSessionCount() {
/* 110 */     return this.cumulativeManagedSessionCount;
/*     */   }
/*     */ 
/*     */   public boolean isActive() {
/* 114 */     return this.activated.get();
/*     */   }
/*     */ 
/*     */   public void fireServiceActivated()
/*     */   {
/* 122 */     if (!this.activated.compareAndSet(false, true)) {
/* 123 */       return;
/*     */     }
/*     */ 
/* 126 */     this.activationTime = System.currentTimeMillis();
/*     */ 
/* 128 */     for (IoServiceListener l : this.listeners)
/*     */       try {
/* 130 */         l.serviceActivated(this.service);
/*     */       } catch (Throwable e) {
/* 132 */         ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void fireServiceDeactivated()
/*     */   {
/* 142 */     if (!this.activated.compareAndSet(true, false)) {
/* 143 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 147 */       for (IoServiceListener l : this.listeners)
/*     */         try {
/* 149 */           l.serviceDeactivated(this.service);
/*     */         } catch (Throwable e) {
/* 151 */           ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */         }
/*     */     }
/*     */     finally {
/* 155 */       disconnectSessions();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void fireSessionCreated(IoSession session)
/*     */   {
/* 163 */     boolean firstSession = false;
/* 164 */     if ((session.getService() instanceof IoConnector)) {
/* 165 */       synchronized (this.managedSessions) {
/* 166 */         firstSession = this.managedSessions.isEmpty();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 171 */     if (this.managedSessions.putIfAbsent(Long.valueOf(session.getId()), session) != null) {
/* 172 */       return;
/*     */     }
/*     */ 
/* 176 */     if (firstSession) {
/* 177 */       fireServiceActivated();
/*     */     }
/*     */ 
/* 181 */     IoFilterChain filterChain = session.getFilterChain();
/* 182 */     filterChain.fireSessionCreated();
/* 183 */     filterChain.fireSessionOpened();
/*     */ 
/* 185 */     int managedSessionCount = this.managedSessions.size();
/* 186 */     if (managedSessionCount > this.largestManagedSessionCount) {
/* 187 */       this.largestManagedSessionCount = managedSessionCount;
/*     */     }
/* 189 */     this.cumulativeManagedSessionCount += 1L;
/*     */ 
/* 192 */     for (IoServiceListener l : this.listeners)
/*     */       try {
/* 194 */         l.sessionCreated(session);
/*     */       } catch (Throwable e) {
/* 196 */         ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void fireSessionDestroyed(IoSession session)
/*     */   {
/* 206 */     if (this.managedSessions.remove(Long.valueOf(session.getId())) == null) {
/* 207 */       return;
/*     */     }
/*     */ 
/* 211 */     session.getFilterChain().fireSessionClosed();
/*     */     try
/*     */     {
/* 215 */       for (IoServiceListener l : this.listeners)
/*     */         try {
/* 217 */           l.sessionDestroyed(session);
/*     */         } catch (Throwable e) {
/* 219 */           ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */         }
/*     */     }
/*     */     finally
/*     */     {
/*     */       boolean lastSession;
/* 224 */       if ((session.getService() instanceof IoConnector)) {
/* 225 */         boolean lastSession = false;
/* 226 */         synchronized (this.managedSessions) {
/* 227 */           lastSession = this.managedSessions.isEmpty();
/*     */         }
/* 229 */         if (lastSession)
/* 230 */           fireServiceDeactivated();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void disconnectSessions()
/*     */   {
/* 237 */     if (!(this.service instanceof IoAcceptor)) {
/* 238 */       return;
/*     */     }
/*     */ 
/* 241 */     if (!((IoAcceptor)this.service).isCloseOnDeactivation()) {
/* 242 */       return;
/*     */     }
/*     */ 
/* 245 */     Object lock = new Object();
/* 246 */     IoFutureListener listener = new LockNotifyingListener(lock);
/*     */ 
/* 248 */     for (IoSession s : this.managedSessions.values()) {
/* 249 */       s.close(true).addListener(listener);
/*     */     }
/*     */     try
/*     */     {
/* 253 */       synchronized (lock) {
/* 254 */         while (!this.managedSessions.isEmpty())
/* 255 */           lock.wait(500L);
/*     */       }
/*     */     }
/*     */     catch (InterruptedException ie) {
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class LockNotifyingListener implements IoFutureListener<IoFuture> {
/*     */     private final Object lock;
/*     */ 
/*     */     public LockNotifyingListener(Object lock) {
/* 267 */       this.lock = lock;
/*     */     }
/*     */ 
/*     */     public void operationComplete(IoFuture future) {
/* 271 */       synchronized (this.lock) {
/* 272 */         this.lock.notifyAll();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.service.IoServiceListenerSupport
 * JD-Core Version:    0.6.0
 */