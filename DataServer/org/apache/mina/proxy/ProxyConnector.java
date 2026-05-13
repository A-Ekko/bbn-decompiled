/*     */ package org.apache.mina.proxy;
/*     */ 
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.concurrent.Executor;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.file.FileRegion;
/*     */ import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
/*     */ import org.apache.mina.core.future.ConnectFuture;
/*     */ import org.apache.mina.core.future.DefaultConnectFuture;
/*     */ import org.apache.mina.core.future.IoFuture;
/*     */ import org.apache.mina.core.service.AbstractIoConnector;
/*     */ import org.apache.mina.core.service.DefaultTransportMetadata;
/*     */ import org.apache.mina.core.service.IoHandler;
/*     */ import org.apache.mina.core.service.TransportMetadata;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.session.IoSessionConfig;
/*     */ import org.apache.mina.core.session.IoSessionInitializer;
/*     */ import org.apache.mina.proxy.filter.ProxyFilter;
/*     */ import org.apache.mina.proxy.handlers.socks.SocksProxyRequest;
/*     */ import org.apache.mina.proxy.session.ProxyIoSession;
/*     */ import org.apache.mina.proxy.session.ProxyIoSessionInitializer;
/*     */ import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
/*     */ import org.apache.mina.transport.socket.SocketConnector;
/*     */ import org.apache.mina.transport.socket.SocketSessionConfig;
/*     */ 
/*     */ public class ProxyConnector extends AbstractIoConnector
/*     */ {
/*  63 */   static final TransportMetadata METADATA = new DefaultTransportMetadata("proxy", "proxyconnector", false, true, InetSocketAddress.class, SocketSessionConfig.class, new Class[] { IoBuffer.class, FileRegion.class });
/*     */ 
/*  70 */   private SocketConnector connector = null;
/*     */ 
/*  75 */   private final ProxyFilter proxyFilter = new ProxyFilter();
/*     */   private ProxyIoSession proxyIoSession;
/*     */   private DefaultConnectFuture future;
/*     */ 
/*     */   public ProxyConnector()
/*     */   {
/*  91 */     super(new DefaultSocketSessionConfig(), null);
/*     */   }
/*     */ 
/*     */   public ProxyConnector(SocketConnector connector)
/*     */   {
/* 100 */     this(connector, new DefaultSocketSessionConfig(), null);
/*     */   }
/*     */ 
/*     */   public ProxyConnector(SocketConnector connector, IoSessionConfig config, Executor executor)
/*     */   {
/* 108 */     super(config, executor);
/* 109 */     setConnector(connector);
/*     */   }
/*     */ 
/*     */   public IoSessionConfig getSessionConfig()
/*     */   {
/* 114 */     return this.connector.getSessionConfig();
/*     */   }
/*     */ 
/*     */   public ProxyIoSession getProxyIoSession() {
/* 118 */     return this.proxyIoSession;
/*     */   }
/*     */ 
/*     */   public void setProxyIoSession(ProxyIoSession proxyIoSession) {
/* 122 */     if (proxyIoSession == null) {
/* 123 */       throw new NullPointerException("proxySession cannot be null");
/*     */     }
/*     */ 
/* 126 */     if (proxyIoSession.getProxyAddress() == null) {
/* 127 */       throw new NullPointerException("proxySession.proxyAddress cannot be null");
/*     */     }
/*     */ 
/* 131 */     proxyIoSession.setConnector(this);
/* 132 */     setDefaultRemoteAddress(proxyIoSession.getProxyAddress());
/* 133 */     this.proxyIoSession = proxyIoSession;
/*     */   }
/*     */ 
/*     */   protected ConnectFuture connect0(SocketAddress remoteAddress, SocketAddress localAddress, IoSessionInitializer<? extends ConnectFuture> sessionInitializer)
/*     */   {
/* 149 */     if (!this.proxyIoSession.isReconnectionNeeded())
/*     */     {
/* 151 */       IoHandler handler = getHandler();
/* 152 */       if (!(handler instanceof AbstractProxyIoHandler)) {
/* 153 */         throw new IllegalArgumentException("IoHandler must be an instance of AbstractProxyIoHandler");
/*     */       }
/*     */ 
/* 157 */       this.connector.setHandler(handler);
/* 158 */       this.future = new DefaultConnectFuture();
/*     */     }
/*     */ 
/* 161 */     ConnectFuture conFuture = this.connector.connect(this.proxyIoSession.getProxyAddress(), new ProxyIoSessionInitializer(sessionInitializer, this.proxyIoSession));
/*     */ 
/* 165 */     if (((this.proxyIoSession.getRequest() instanceof SocksProxyRequest)) || (this.proxyIoSession.isReconnectionNeeded()))
/*     */     {
/* 167 */       return conFuture;
/*     */     }
/* 169 */     return this.future;
/*     */   }
/*     */ 
/*     */   public void cancelConnectFuture()
/*     */   {
/* 174 */     this.future.cancel();
/*     */   }
/*     */ 
/*     */   protected ConnectFuture fireConnected(IoSession session) {
/* 178 */     this.future.setSession(session);
/* 179 */     return this.future;
/*     */   }
/*     */ 
/*     */   public final SocketConnector getConnector()
/*     */   {
/* 187 */     return this.connector;
/*     */   }
/*     */ 
/*     */   public final void setConnector(SocketConnector newConnector)
/*     */   {
/* 195 */     if (newConnector == null) {
/* 196 */       throw new NullPointerException("connector cannot be null");
/*     */     }
/*     */ 
/* 199 */     SocketConnector oldConnector = this.connector;
/*     */ 
/* 202 */     if (oldConnector != null) {
/* 203 */       oldConnector.getFilterChain().remove(ProxyFilter.class.getName());
/*     */     }
/*     */ 
/* 206 */     this.connector = newConnector;
/*     */ 
/* 209 */     if (newConnector.getFilterChain().contains(ProxyFilter.class.getName())) {
/* 210 */       newConnector.getFilterChain().remove(ProxyFilter.class.getName());
/*     */     }
/*     */ 
/* 213 */     newConnector.getFilterChain().addFirst(ProxyFilter.class.getName(), this.proxyFilter);
/*     */   }
/*     */ 
/*     */   protected IoFuture dispose0()
/*     */     throws Exception
/*     */   {
/* 222 */     if (this.connector != null) {
/* 223 */       this.connector.dispose();
/*     */     }
/* 225 */     return null;
/*     */   }
/*     */ 
/*     */   public TransportMetadata getTransportMetadata()
/*     */   {
/* 232 */     return METADATA;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.ProxyConnector
 * JD-Core Version:    0.6.0
 */