/*     */ package org.apache.mina.transport.vmpipe;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Executor;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.filterchain.IoFilterChainBuilder;
/*     */ import org.apache.mina.core.future.CloseFuture;
/*     */ import org.apache.mina.core.future.ConnectFuture;
/*     */ import org.apache.mina.core.future.DefaultConnectFuture;
/*     */ import org.apache.mina.core.future.IoFuture;
/*     */ import org.apache.mina.core.future.IoFutureListener;
/*     */ import org.apache.mina.core.service.AbstractIoConnector;
/*     */ import org.apache.mina.core.service.IoServiceListenerSupport;
/*     */ import org.apache.mina.core.service.TransportMetadata;
/*     */ import org.apache.mina.core.session.IdleStatusChecker;
/*     */ import org.apache.mina.core.session.IdleStatusChecker.NotifyingTask;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.session.IoSessionInitializer;
/*     */ import org.apache.mina.util.ExceptionMonitor;
/*     */ 
/*     */ public final class VmPipeConnector extends AbstractIoConnector
/*     */ {
/*     */   private IdleStatusChecker idleChecker;
/* 151 */   private static final Set<VmPipeAddress> TAKEN_LOCAL_ADDRESSES = new HashSet();
/*     */ 
/* 153 */   private static int nextLocalPort = -1;
/*     */ 
/* 155 */   private static final IoFutureListener<IoFuture> LOCAL_ADDRESS_RECLAIMER = new LocalAddressReclaimer(null);
/*     */ 
/*     */   public VmPipeConnector()
/*     */   {
/*  56 */     this(null);
/*     */   }
/*     */ 
/*     */   public VmPipeConnector(Executor executor)
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
/*     */   protected ConnectFuture connect0(SocketAddress remoteAddress, SocketAddress localAddress, IoSessionInitializer<? extends ConnectFuture> sessionInitializer)
/*     */   {
/*  83 */     VmPipe entry = (VmPipe)VmPipeAcceptor.boundHandlers.get(remoteAddress);
/*  84 */     if (entry == null) {
/*  85 */       return DefaultConnectFuture.newFailedFuture(new IOException("Endpoint unavailable: " + remoteAddress));
/*     */     }
/*     */ 
/*  89 */     DefaultConnectFuture future = new DefaultConnectFuture();
/*     */     VmPipeAddress actualLocalAddress;
/*     */     try
/*     */     {
/*  94 */       actualLocalAddress = nextLocalAddress();
/*     */     } catch (IOException e) {
/*  96 */       return DefaultConnectFuture.newFailedFuture(e);
/*     */     }
/*     */ 
/*  99 */     VmPipeSession localSession = new VmPipeSession(this, getListeners(), actualLocalAddress, getHandler(), entry);
/*     */ 
/* 102 */     initSession(localSession, future, sessionInitializer);
/*     */ 
/* 105 */     localSession.getCloseFuture().addListener(LOCAL_ADDRESS_RECLAIMER);
/*     */     try
/*     */     {
/* 109 */       IoFilterChain filterChain = localSession.getFilterChain();
/* 110 */       getFilterChainBuilder().buildFilterChain(filterChain);
/*     */ 
/* 113 */       getListeners().fireSessionCreated(localSession);
/* 114 */       this.idleChecker.addSession(localSession);
/*     */     } catch (Throwable t) {
/* 116 */       future.setException(t);
/* 117 */       return future;
/*     */     }
/*     */ 
/* 121 */     VmPipeSession remoteSession = localSession.getRemoteSession();
/* 122 */     ((VmPipeAcceptor)remoteSession.getService()).doFinishSessionInitialization(remoteSession, null);
/*     */     try {
/* 124 */       IoFilterChain filterChain = remoteSession.getFilterChain();
/* 125 */       entry.getAcceptor().getFilterChainBuilder().buildFilterChain(filterChain);
/*     */ 
/* 129 */       entry.getListeners().fireSessionCreated(remoteSession);
/* 130 */       this.idleChecker.addSession(remoteSession);
/*     */     } catch (Throwable t) {
/* 132 */       ExceptionMonitor.getInstance().exceptionCaught(t);
/* 133 */       remoteSession.close(true);
/*     */     }
/*     */ 
/* 138 */     ((VmPipeFilterChain)localSession.getFilterChain()).start();
/* 139 */     ((VmPipeFilterChain)remoteSession.getFilterChain()).start();
/*     */ 
/* 141 */     return future;
/*     */   }
/*     */ 
/*     */   protected IoFuture dispose0()
/*     */     throws Exception
/*     */   {
/* 147 */     this.idleChecker.getNotifyingTask().cancel();
/* 148 */     return null;
/*     */   }
/*     */ 
/*     */   private static VmPipeAddress nextLocalAddress()
/*     */     throws IOException
/*     */   {
/* 158 */     synchronized (TAKEN_LOCAL_ADDRESSES) {
/* 159 */       if (nextLocalPort >= 0) {
/* 160 */         nextLocalPort = -1;
/*     */       }
/* 162 */       for (int i = 0; i < 2147483647; i++) {
/* 163 */         VmPipeAddress answer = new VmPipeAddress(nextLocalPort--);
/* 164 */         if (!TAKEN_LOCAL_ADDRESSES.contains(answer)) {
/* 165 */           TAKEN_LOCAL_ADDRESSES.add(answer);
/* 166 */           return answer;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 171 */     throw new IOException("Can't assign a local VM pipe port.");
/*     */   }
/*     */ 
/*     */   private static class LocalAddressReclaimer implements IoFutureListener<IoFuture> {
/*     */     public void operationComplete(IoFuture future) {
/* 176 */       synchronized (VmPipeConnector.TAKEN_LOCAL_ADDRESSES) {
/* 177 */         VmPipeConnector.TAKEN_LOCAL_ADDRESSES.remove(future.getSession().getLocalAddress());
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.transport.vmpipe.VmPipeConnector
 * JD-Core Version:    0.6.0
 */