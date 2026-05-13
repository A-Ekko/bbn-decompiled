/*     */ package org.apache.mina.transport.vmpipe;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Executor;
/*     */ import org.apache.mina.core.future.IoFuture;
/*     */ import org.apache.mina.core.service.AbstractIoAcceptor;
/*     */ import org.apache.mina.core.service.TransportMetadata;
/*     */ import org.apache.mina.core.session.IdleStatusChecker;
/*     */ import org.apache.mina.core.session.IdleStatusChecker.NotifyingTask;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ 
/*     */ public final class VmPipeAcceptor extends AbstractIoAcceptor
/*     */ {
/*     */   private IdleStatusChecker idleChecker;
/*  50 */   static final Map<VmPipeAddress, VmPipe> boundHandlers = new HashMap();
/*     */ 
/*     */   public VmPipeAcceptor()
/*     */   {
/*  56 */     this(null);
/*     */   }
/*     */ 
/*     */   public VmPipeAcceptor(Executor executor)
/*     */   {
/*  63 */     super(new DefaultVmPipeSessionConfig(), executor);
/*  64 */     this.idleChecker = new IdleStatusChecker();
/*     */ 
/*  67 */     executeWorker(this.idleChecker.getNotifyingTask(), "idleStatusChecker");
/*     */   }
/*     */ 
/*     */   public TransportMetadata getTransportMetadata() {
/*  71 */     return VmPipeSession.METADATA;
/*     */   }
/*     */ 
/*     */   public VmPipeSessionConfig getSessionConfig()
/*     */   {
/*  76 */     return (VmPipeSessionConfig)super.getSessionConfig();
/*     */   }
/*     */ 
/*     */   public VmPipeAddress getLocalAddress()
/*     */   {
/*  81 */     return (VmPipeAddress)super.getLocalAddress();
/*     */   }
/*     */ 
/*     */   public VmPipeAddress getDefaultLocalAddress()
/*     */   {
/*  86 */     return (VmPipeAddress)super.getDefaultLocalAddress();
/*     */   }
/*     */ 
/*     */   public void setDefaultLocalAddress(VmPipeAddress localAddress)
/*     */   {
/*  93 */     super.setDefaultLocalAddress(localAddress);
/*     */   }
/*     */ 
/*     */   protected IoFuture dispose0()
/*     */     throws Exception
/*     */   {
/*  99 */     this.idleChecker.getNotifyingTask().cancel();
/* 100 */     unbind();
/* 101 */     return null;
/*     */   }
/*     */ 
/*     */   protected Set<SocketAddress> bindInternal(List<? extends SocketAddress> localAddresses) throws IOException
/*     */   {
/* 106 */     Set newLocalAddresses = new HashSet();
/*     */ 
/* 108 */     synchronized (boundHandlers) {
/* 109 */       for (SocketAddress a : localAddresses) {
/* 110 */         VmPipeAddress localAddress = (VmPipeAddress)a;
/* 111 */         if ((localAddress == null) || (localAddress.getPort() == 0)) {
/* 112 */           localAddress = null;
/* 113 */           for (int i = 10000; i < 2147483647; i++) {
/* 114 */             VmPipeAddress newLocalAddress = new VmPipeAddress(i);
/* 115 */             if ((boundHandlers.containsKey(newLocalAddress)) || (newLocalAddresses.contains(newLocalAddress)))
/*     */               continue;
/* 117 */             localAddress = newLocalAddress;
/* 118 */             break;
/*     */           }
/*     */ 
/* 122 */           if (localAddress == null)
/* 123 */             throw new IOException("No port available.");
/*     */         } else {
/* 125 */           if (localAddress.getPort() < 0)
/* 126 */             throw new IOException("Bind port number must be 0 or above.");
/* 127 */           if (boundHandlers.containsKey(localAddress)) {
/* 128 */             throw new IOException("Address already bound: " + localAddress);
/*     */           }
/*     */         }
/* 131 */         newLocalAddresses.add(localAddress);
/*     */       }
/*     */ 
/* 134 */       for (SocketAddress a : newLocalAddresses) {
/* 135 */         VmPipeAddress localAddress = (VmPipeAddress)a;
/* 136 */         if (!boundHandlers.containsKey(localAddress)) {
/* 137 */           boundHandlers.put(localAddress, new VmPipe(this, localAddress, getHandler(), getListeners()));
/*     */         }
/*     */         else {
/* 140 */           for (SocketAddress a2 : newLocalAddresses) {
/* 141 */             boundHandlers.remove(a2);
/*     */           }
/* 143 */           throw new IOException("Duplicate local address: " + a);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 148 */     return newLocalAddresses;
/*     */   }
/*     */ 
/*     */   protected void unbind0(List<? extends SocketAddress> localAddresses)
/*     */   {
/* 153 */     synchronized (boundHandlers) {
/* 154 */       for (SocketAddress a : localAddresses)
/* 155 */         boundHandlers.remove(a);
/*     */     }
/*     */   }
/*     */ 
/*     */   public IoSession newSession(SocketAddress remoteAddress, SocketAddress localAddress)
/*     */   {
/* 161 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   void doFinishSessionInitialization(IoSession session, IoFuture future) {
/* 165 */     initSession(session, future, null);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.transport.vmpipe.VmPipeAcceptor
 * JD-Core Version:    0.6.0
 */