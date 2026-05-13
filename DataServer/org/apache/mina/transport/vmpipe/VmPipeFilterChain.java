/*     */ package org.apache.mina.transport.vmpipe;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.filterchain.DefaultIoFilterChain;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.future.CloseFuture;
/*     */ import org.apache.mina.core.future.WriteFuture;
/*     */ import org.apache.mina.core.service.IoProcessor;
/*     */ import org.apache.mina.core.service.IoServiceListenerSupport;
/*     */ import org.apache.mina.core.session.AbstractIoSession;
/*     */ import org.apache.mina.core.session.IdleStatus;
/*     */ import org.apache.mina.core.session.IoEvent;
/*     */ import org.apache.mina.core.session.IoEventType;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ import org.apache.mina.core.write.WriteRequestQueue;
/*     */ import org.apache.mina.core.write.WriteToClosedSessionException;
/*     */ 
/*     */ class VmPipeFilterChain extends DefaultIoFilterChain
/*     */ {
/*  46 */   private final Queue<IoEvent> eventQueue = new ConcurrentLinkedQueue();
/*  47 */   private final IoProcessor<VmPipeSession> processor = new VmPipeIoProcessor(null);
/*     */   private volatile boolean flushEnabled;
/*     */   private volatile boolean sessionOpened;
/*     */ 
/*     */   VmPipeFilterChain(AbstractIoSession session)
/*     */   {
/*  53 */     super(session);
/*     */   }
/*     */ 
/*     */   IoProcessor<VmPipeSession> getProcessor() {
/*  57 */     return this.processor;
/*     */   }
/*     */ 
/*     */   public void start() {
/*  61 */     this.flushEnabled = true;
/*  62 */     flushEvents();
/*  63 */     flushPendingDataQueues((VmPipeSession)getSession());
/*     */   }
/*     */ 
/*     */   private void pushEvent(IoEvent e) {
/*  67 */     pushEvent(e, this.flushEnabled);
/*     */   }
/*     */ 
/*     */   private void pushEvent(IoEvent e, boolean flushNow) {
/*  71 */     this.eventQueue.add(e);
/*  72 */     if (flushNow)
/*  73 */       flushEvents();
/*     */   }
/*     */ 
/*     */   private void flushEvents()
/*     */   {
/*     */     IoEvent e;
/*  79 */     while ((e = (IoEvent)this.eventQueue.poll()) != null)
/*  80 */       fireEvent(e);
/*     */   }
/*     */ 
/*     */   private void fireEvent(IoEvent e)
/*     */   {
/*  85 */     VmPipeSession session = (VmPipeSession)getSession();
/*  86 */     IoEventType type = e.getType();
/*  87 */     Object data = e.getParameter();
/*     */ 
/*  89 */     if (type == IoEventType.MESSAGE_RECEIVED) {
/*  90 */       if ((this.sessionOpened) && (!session.isReadSuspended()) && (session.getLock().tryLock()))
/*     */         try {
/*  92 */           if (session.isReadSuspended())
/*  93 */             session.receivedMessageQueue.add(data);
/*     */           else
/*  95 */             super.fireMessageReceived(data);
/*     */         }
/*     */         finally {
/*  98 */           session.getLock().unlock();
/*     */         }
/*     */       else
/* 101 */         session.receivedMessageQueue.add(data);
/*     */     }
/* 103 */     else if (type == IoEventType.WRITE) {
/* 104 */       super.fireFilterWrite((WriteRequest)data);
/* 105 */     } else if (type == IoEventType.MESSAGE_SENT) {
/* 106 */       super.fireMessageSent((WriteRequest)data);
/* 107 */     } else if (type == IoEventType.EXCEPTION_CAUGHT) {
/* 108 */       super.fireExceptionCaught((Throwable)data);
/* 109 */     } else if (type == IoEventType.SESSION_IDLE) {
/* 110 */       super.fireSessionIdle((IdleStatus)data);
/* 111 */     } else if (type == IoEventType.SESSION_OPENED) {
/* 112 */       super.fireSessionOpened();
/* 113 */       this.sessionOpened = true;
/* 114 */     } else if (type == IoEventType.SESSION_CREATED) {
/* 115 */       session.getLock().lock();
/*     */       try {
/* 117 */         super.fireSessionCreated();
/*     */       } finally {
/* 119 */         session.getLock().unlock();
/*     */       }
/* 121 */     } else if (type == IoEventType.SESSION_CLOSED) {
/* 122 */       flushPendingDataQueues(session);
/* 123 */       super.fireSessionClosed();
/* 124 */     } else if (type == IoEventType.CLOSE) {
/* 125 */       super.fireFilterClose();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void flushPendingDataQueues(VmPipeSession s) {
/* 130 */     s.getProcessor().updateTrafficControl(s);
/* 131 */     s.getRemoteSession().getProcessor().updateTrafficControl(s);
/*     */   }
/*     */ 
/*     */   public void fireFilterClose()
/*     */   {
/* 136 */     pushEvent(new IoEvent(IoEventType.CLOSE, getSession(), null));
/*     */   }
/*     */ 
/*     */   public void fireFilterWrite(WriteRequest writeRequest)
/*     */   {
/* 141 */     pushEvent(new IoEvent(IoEventType.WRITE, getSession(), writeRequest));
/*     */   }
/*     */ 
/*     */   public void fireExceptionCaught(Throwable cause)
/*     */   {
/* 146 */     pushEvent(new IoEvent(IoEventType.EXCEPTION_CAUGHT, getSession(), cause));
/*     */   }
/*     */ 
/*     */   public void fireMessageSent(WriteRequest request)
/*     */   {
/* 151 */     pushEvent(new IoEvent(IoEventType.MESSAGE_SENT, getSession(), request));
/*     */   }
/*     */ 
/*     */   public void fireSessionClosed()
/*     */   {
/* 156 */     pushEvent(new IoEvent(IoEventType.SESSION_CLOSED, getSession(), null));
/*     */   }
/*     */ 
/*     */   public void fireSessionCreated()
/*     */   {
/* 161 */     pushEvent(new IoEvent(IoEventType.SESSION_CREATED, getSession(), null));
/*     */   }
/*     */ 
/*     */   public void fireSessionIdle(IdleStatus status)
/*     */   {
/* 166 */     pushEvent(new IoEvent(IoEventType.SESSION_IDLE, getSession(), status));
/*     */   }
/*     */ 
/*     */   public void fireSessionOpened()
/*     */   {
/* 171 */     pushEvent(new IoEvent(IoEventType.SESSION_OPENED, getSession(), null));
/*     */   }
/*     */ 
/*     */   public void fireMessageReceived(Object message)
/*     */   {
/* 176 */     pushEvent(new IoEvent(IoEventType.MESSAGE_RECEIVED, getSession(), message));
/*     */   }
/*     */   private class VmPipeIoProcessor implements IoProcessor<VmPipeSession> {
/*     */     private VmPipeIoProcessor() {
/*     */     }
/* 181 */     public void flush(VmPipeSession session) { WriteRequestQueue queue = session.getWriteRequestQueue0();
/* 182 */       if (!session.isClosing()) {
/* 183 */         session.getLock().lock();
/*     */         try {
/* 185 */           if (queue.isEmpty(session)) {
/*     */             return;
/*     */           }
/* 189 */           long currentTime = System.currentTimeMillis();
/*     */           WriteRequest req;
/* 190 */           while ((req = queue.poll(session)) != null) {
/* 191 */             Object m = req.getMessage();
/* 192 */             VmPipeFilterChain.this.pushEvent(new IoEvent(IoEventType.MESSAGE_SENT, session, req), false);
/* 193 */             session.getRemoteSession().getFilterChain().fireMessageReceived(getMessageCopy(m));
/*     */ 
/* 195 */             if ((m instanceof IoBuffer))
/* 196 */               session.increaseWrittenBytes0(((IoBuffer)m).remaining(), currentTime);
/*     */           }
/*     */         }
/*     */         finally
/*     */         {
/* 201 */           if (VmPipeFilterChain.this.flushEnabled) {
/* 202 */             VmPipeFilterChain.this.flushEvents();
/*     */           }
/* 204 */           session.getLock().unlock();
/*     */         }
/*     */ 
/* 207 */         VmPipeFilterChain.access$400(session);
/*     */       } else {
/* 209 */         List failedRequests = new ArrayList();
/*     */         WriteRequest req;
/* 211 */         while ((req = queue.poll(session)) != null) {
/* 212 */           failedRequests.add(req);
/*     */         }
/*     */ 
/* 215 */         if (!failedRequests.isEmpty()) {
/* 216 */           WriteToClosedSessionException cause = new WriteToClosedSessionException(failedRequests);
/* 217 */           for (WriteRequest r : failedRequests) {
/* 218 */             r.getFuture().setException(cause);
/*     */           }
/* 220 */           session.getFilterChain().fireExceptionCaught(cause);
/*     */         }
/*     */       } }
/*     */ 
/*     */     private Object getMessageCopy(Object message)
/*     */     {
/* 226 */       Object messageCopy = message;
/* 227 */       if ((message instanceof IoBuffer)) {
/* 228 */         IoBuffer rb = (IoBuffer)message;
/* 229 */         rb.mark();
/* 230 */         IoBuffer wb = IoBuffer.allocate(rb.remaining());
/* 231 */         wb.put(rb);
/* 232 */         wb.flip();
/* 233 */         rb.reset();
/* 234 */         messageCopy = wb;
/*     */       }
/* 236 */       return messageCopy;
/*     */     }
/*     */ 
/*     */     public void remove(VmPipeSession session) {
/*     */       try {
/* 241 */         session.getLock().lock();
/* 242 */         if (!session.getCloseFuture().isClosed()) {
/* 243 */           session.getServiceListeners().fireSessionDestroyed(session);
/* 244 */           session.getRemoteSession().close(true);
/*     */         }
/*     */       } finally {
/* 247 */         session.getLock().unlock();
/*     */       }
/*     */     }
/*     */ 
/*     */     public void add(VmPipeSession session)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void updateTrafficControl(VmPipeSession session)
/*     */     {
/*     */       Iterator i$;
/* 256 */       if (!session.isReadSuspended()) {
/* 257 */         List data = new ArrayList();
/* 258 */         session.receivedMessageQueue.drainTo(data);
/* 259 */         for (i$ = data.iterator(); i$.hasNext(); ) { Object aData = i$.next();
/* 260 */           VmPipeFilterChain.this.fireMessageReceived(aData);
/*     */         }
/*     */       }
/*     */ 
/* 264 */       if (!session.isWriteSuspended())
/* 265 */         flush(session);
/*     */     }
/*     */ 
/*     */     public void dispose()
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean isDisposed()
/*     */     {
/* 274 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean isDisposing() {
/* 278 */       return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.transport.vmpipe.VmPipeFilterChain
 * JD-Core Version:    0.6.0
 */