/*     */ package org.apache.mina.core.service;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import java.util.concurrent.Executor;
/*     */ import org.apache.mina.core.future.ConnectFuture;
/*     */ import org.apache.mina.core.future.IoFuture;
/*     */ import org.apache.mina.core.future.IoFutureListener;
/*     */ import org.apache.mina.core.session.IdleStatus;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.session.IoSessionConfig;
/*     */ import org.apache.mina.core.session.IoSessionInitializer;
/*     */ 
/*     */ public abstract class AbstractIoConnector extends AbstractIoService
/*     */   implements IoConnector
/*     */ {
/*  45 */   private long connectTimeoutCheckInterval = 50L;
/*  46 */   private long connectTimeoutInMillis = 60000L;
/*     */   private SocketAddress defaultRemoteAddress;
/*     */ 
/*     */   protected AbstractIoConnector(IoSessionConfig sessionConfig, Executor executor)
/*     */   {
/*  64 */     super(sessionConfig, executor);
/*     */   }
/*     */ 
/*     */   public long getConnectTimeoutCheckInterval()
/*     */   {
/*  75 */     return this.connectTimeoutCheckInterval;
/*     */   }
/*     */ 
/*     */   public void setConnectTimeoutCheckInterval(long minimumConnectTimeout) {
/*  79 */     if (getConnectTimeoutMillis() < minimumConnectTimeout) {
/*  80 */       this.connectTimeoutInMillis = minimumConnectTimeout;
/*     */     }
/*     */ 
/*  83 */     this.connectTimeoutCheckInterval = minimumConnectTimeout;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public final int getConnectTimeout()
/*     */   {
/*  91 */     return (int)this.connectTimeoutInMillis / 1000;
/*     */   }
/*     */ 
/*     */   public final long getConnectTimeoutMillis()
/*     */   {
/*  98 */     return this.connectTimeoutInMillis;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public final void setConnectTimeout(int connectTimeout)
/*     */   {
/* 107 */     setConnectTimeoutMillis(connectTimeout * 1000L);
/*     */   }
/*     */ 
/*     */   public final void setConnectTimeoutMillis(long connectTimeoutInMillis)
/*     */   {
/* 115 */     if (connectTimeoutInMillis <= this.connectTimeoutCheckInterval) {
/* 116 */       this.connectTimeoutCheckInterval = connectTimeoutInMillis;
/*     */     }
/* 118 */     this.connectTimeoutInMillis = connectTimeoutInMillis;
/*     */   }
/*     */ 
/*     */   public SocketAddress getDefaultRemoteAddress()
/*     */   {
/* 125 */     return this.defaultRemoteAddress;
/*     */   }
/*     */ 
/*     */   public final void setDefaultRemoteAddress(SocketAddress defaultRemoteAddress)
/*     */   {
/* 132 */     if (defaultRemoteAddress == null) {
/* 133 */       throw new NullPointerException("defaultRemoteAddress");
/*     */     }
/*     */ 
/* 136 */     if (!getTransportMetadata().getAddressType().isAssignableFrom(defaultRemoteAddress.getClass()))
/*     */     {
/* 138 */       throw new IllegalArgumentException("defaultRemoteAddress type: " + defaultRemoteAddress.getClass() + " (expected: " + getTransportMetadata().getAddressType() + ")");
/*     */     }
/*     */ 
/* 142 */     this.defaultRemoteAddress = defaultRemoteAddress;
/*     */   }
/*     */ 
/*     */   public final ConnectFuture connect()
/*     */   {
/* 149 */     SocketAddress defaultRemoteAddress = getDefaultRemoteAddress();
/* 150 */     if (defaultRemoteAddress == null) {
/* 151 */       throw new IllegalStateException("defaultRemoteAddress is not set.");
/*     */     }
/*     */ 
/* 154 */     return connect(defaultRemoteAddress, null, null);
/*     */   }
/*     */ 
/*     */   public ConnectFuture connect(IoSessionInitializer<? extends ConnectFuture> sessionInitializer)
/*     */   {
/* 161 */     SocketAddress defaultRemoteAddress = getDefaultRemoteAddress();
/* 162 */     if (defaultRemoteAddress == null) {
/* 163 */       throw new IllegalStateException("defaultRemoteAddress is not set.");
/*     */     }
/*     */ 
/* 166 */     return connect(defaultRemoteAddress, null, sessionInitializer);
/*     */   }
/*     */ 
/*     */   public final ConnectFuture connect(SocketAddress remoteAddress)
/*     */   {
/* 173 */     return connect(remoteAddress, null, null);
/*     */   }
/*     */ 
/*     */   public ConnectFuture connect(SocketAddress remoteAddress, IoSessionInitializer<? extends ConnectFuture> sessionInitializer)
/*     */   {
/* 181 */     return connect(remoteAddress, null, sessionInitializer);
/*     */   }
/*     */ 
/*     */   public ConnectFuture connect(SocketAddress remoteAddress, SocketAddress localAddress)
/*     */   {
/* 189 */     return connect(remoteAddress, localAddress, null);
/*     */   }
/*     */ 
/*     */   public final ConnectFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, IoSessionInitializer<? extends ConnectFuture> sessionInitializer)
/*     */   {
/* 197 */     if (isDisposing()) {
/* 198 */       throw new IllegalStateException("Already disposed.");
/*     */     }
/*     */ 
/* 201 */     if (remoteAddress == null) {
/* 202 */       throw new NullPointerException("remoteAddress");
/*     */     }
/*     */ 
/* 205 */     if (!getTransportMetadata().getAddressType().isAssignableFrom(remoteAddress.getClass()))
/*     */     {
/* 207 */       throw new IllegalArgumentException("remoteAddress type: " + remoteAddress.getClass() + " (expected: " + getTransportMetadata().getAddressType() + ")");
/*     */     }
/*     */ 
/* 212 */     if ((localAddress != null) && (!getTransportMetadata().getAddressType().isAssignableFrom(localAddress.getClass())))
/*     */     {
/* 215 */       throw new IllegalArgumentException("localAddress type: " + localAddress.getClass() + " (expected: " + getTransportMetadata().getAddressType() + ")");
/*     */     }
/*     */ 
/* 220 */     if (getHandler() == null) {
/* 221 */       if (getSessionConfig().isUseReadOperation())
/* 222 */         setHandler(new IoHandler()
/*     */         {
/*     */           public void exceptionCaught(IoSession session, Throwable cause) throws Exception
/*     */           {
/*     */           }
/*     */ 
/*     */           public void messageReceived(IoSession session, Object message) throws Exception
/*     */           {
/*     */           }
/*     */ 
/*     */           public void messageSent(IoSession session, Object message) throws Exception
/*     */           {
/*     */           }
/*     */ 
/*     */           public void sessionClosed(IoSession session) throws Exception
/*     */           {
/*     */           }
/*     */ 
/*     */           public void sessionCreated(IoSession session) throws Exception
/*     */           {
/*     */           }
/*     */ 
/*     */           public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
/*     */           }
/*     */ 
/*     */           public void sessionOpened(IoSession session) throws Exception {
/*     */           }
/*     */         });
/*     */       else {
/* 252 */         throw new IllegalStateException("handler is not set.");
/*     */       }
/*     */     }
/*     */ 
/* 256 */     return connect0(remoteAddress, localAddress, sessionInitializer);
/*     */   }
/*     */ 
/*     */   protected abstract ConnectFuture connect0(SocketAddress paramSocketAddress1, SocketAddress paramSocketAddress2, IoSessionInitializer<? extends ConnectFuture> paramIoSessionInitializer);
/*     */ 
/*     */   protected final void finishSessionInitialization0(IoSession session, IoFuture future)
/*     */   {
/* 280 */     future.addListener(new IoFutureListener(session) {
/*     */       public void operationComplete(ConnectFuture future) {
/* 282 */         if (future.isCanceled())
/* 283 */           this.val$session.close(true);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 294 */     TransportMetadata m = getTransportMetadata();
/* 295 */     return '(' + m.getProviderName() + ' ' + m.getName() + " connector: " + "managedSessionCount: " + getManagedSessionCount() + ')';
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.service.AbstractIoConnector
 * JD-Core Version:    0.6.0
 */