/*     */ package org.apache.mina.transport.vmpipe;
/*     */ 
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.service.DefaultTransportMetadata;
/*     */ import org.apache.mina.core.service.IoHandler;
/*     */ import org.apache.mina.core.service.IoProcessor;
/*     */ import org.apache.mina.core.service.IoService;
/*     */ import org.apache.mina.core.service.IoServiceListenerSupport;
/*     */ import org.apache.mina.core.service.TransportMetadata;
/*     */ import org.apache.mina.core.session.AbstractIoSession;
/*     */ import org.apache.mina.core.write.WriteRequestQueue;
/*     */ 
/*     */ class VmPipeSession extends AbstractIoSession
/*     */ {
/*  46 */   static final TransportMetadata METADATA = new DefaultTransportMetadata("mina", "vmpipe", false, false, VmPipeAddress.class, VmPipeSessionConfig.class, new Class[] { Object.class });
/*     */ 
/*  53 */   private static final VmPipeSessionConfig CONFIG = new DefaultVmPipeSessionConfig();
/*     */   private final IoService service;
/*     */   private final IoServiceListenerSupport serviceListeners;
/*     */   private final VmPipeAddress localAddress;
/*     */   private final VmPipeAddress remoteAddress;
/*     */   private final VmPipeAddress serviceAddress;
/*     */   private final IoHandler handler;
/*     */   private final VmPipeFilterChain filterChain;
/*     */   private final VmPipeSession remoteSession;
/*     */   private final Lock lock;
/*     */   final BlockingQueue<Object> receivedMessageQueue;
/*     */ 
/*     */   VmPipeSession(IoService service, IoServiceListenerSupport serviceListeners, VmPipeAddress localAddress, IoHandler handler, VmPipe remoteEntry)
/*     */   {
/*  81 */     this.service = service;
/*  82 */     this.serviceListeners = serviceListeners;
/*  83 */     this.lock = new ReentrantLock();
/*  84 */     this.localAddress = localAddress;
/*  85 */     this.remoteAddress = (this.serviceAddress = remoteEntry.getAddress());
/*  86 */     this.handler = handler;
/*  87 */     this.filterChain = new VmPipeFilterChain(this);
/*  88 */     this.receivedMessageQueue = new LinkedBlockingQueue();
/*     */ 
/*  90 */     this.remoteSession = new VmPipeSession(this, remoteEntry);
/*     */   }
/*     */ 
/*     */   private VmPipeSession(VmPipeSession remoteSession, VmPipe entry)
/*     */   {
/*  97 */     this.service = entry.getAcceptor();
/*  98 */     this.serviceListeners = entry.getListeners();
/*  99 */     this.lock = remoteSession.lock;
/* 100 */     this.localAddress = (this.serviceAddress = remoteSession.remoteAddress);
/* 101 */     this.remoteAddress = remoteSession.localAddress;
/* 102 */     this.handler = entry.getHandler();
/* 103 */     this.filterChain = new VmPipeFilterChain(this);
/* 104 */     this.remoteSession = remoteSession;
/* 105 */     this.receivedMessageQueue = new LinkedBlockingQueue();
/*     */   }
/*     */ 
/*     */   public IoService getService() {
/* 109 */     return this.service;
/*     */   }
/*     */ 
/*     */   public IoProcessor<VmPipeSession> getProcessor()
/*     */   {
/* 114 */     return this.filterChain.getProcessor();
/*     */   }
/*     */ 
/*     */   IoServiceListenerSupport getServiceListeners() {
/* 118 */     return this.serviceListeners;
/*     */   }
/*     */ 
/*     */   public VmPipeSessionConfig getConfig() {
/* 122 */     return CONFIG;
/*     */   }
/*     */ 
/*     */   public IoFilterChain getFilterChain() {
/* 126 */     return this.filterChain;
/*     */   }
/*     */ 
/*     */   public VmPipeSession getRemoteSession() {
/* 130 */     return this.remoteSession;
/*     */   }
/*     */ 
/*     */   public IoHandler getHandler() {
/* 134 */     return this.handler;
/*     */   }
/*     */ 
/*     */   public TransportMetadata getTransportMetadata() {
/* 138 */     return METADATA;
/*     */   }
/*     */ 
/*     */   public VmPipeAddress getRemoteAddress() {
/* 142 */     return this.remoteAddress;
/*     */   }
/*     */ 
/*     */   public VmPipeAddress getLocalAddress() {
/* 146 */     return this.localAddress;
/*     */   }
/*     */ 
/*     */   public VmPipeAddress getServiceAddress()
/*     */   {
/* 151 */     return this.serviceAddress;
/*     */   }
/*     */ 
/*     */   void increaseWrittenBytes0(int increment, long currentTime) {
/* 155 */     super.increaseWrittenBytes(increment, currentTime);
/*     */   }
/*     */ 
/*     */   WriteRequestQueue getWriteRequestQueue0() {
/* 159 */     return super.getWriteRequestQueue();
/*     */   }
/*     */ 
/*     */   Lock getLock() {
/* 163 */     return this.lock;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.transport.vmpipe.VmPipeSession
 * JD-Core Version:    0.6.0
 */