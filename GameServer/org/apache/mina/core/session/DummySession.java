/*     */ package org.apache.mina.core.session;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Executor;
/*     */ import org.apache.mina.core.file.FileRegion;
/*     */ import org.apache.mina.core.filterchain.DefaultIoFilterChain;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.future.CloseFuture;
/*     */ import org.apache.mina.core.future.IoFuture;
/*     */ import org.apache.mina.core.service.AbstractIoAcceptor;
/*     */ import org.apache.mina.core.service.DefaultTransportMetadata;
/*     */ import org.apache.mina.core.service.IoAcceptor;
/*     */ import org.apache.mina.core.service.IoHandler;
/*     */ import org.apache.mina.core.service.IoHandlerAdapter;
/*     */ import org.apache.mina.core.service.IoProcessor;
/*     */ import org.apache.mina.core.service.IoService;
/*     */ import org.apache.mina.core.service.TransportMetadata;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ import org.apache.mina.core.write.WriteRequestQueue;
/*     */ 
/*     */ public class DummySession extends AbstractIoSession
/*     */ {
/*  58 */   private static final TransportMetadata TRANSPORT_METADATA = new DefaultTransportMetadata("mina", "dummy", false, false, SocketAddress.class, IoSessionConfig.class, new Class[] { Object.class });
/*     */ 
/*  63 */   private static final SocketAddress ANONYMOUS_ADDRESS = new SocketAddress() {
/*     */     private static final long serialVersionUID = -496112902353454179L;
/*     */ 
/*     */     public String toString() {
/*  68 */       return "?";
/*     */     }
/*  63 */   };
/*     */   private volatile IoService service;
/*  74 */   private volatile IoSessionConfig config = new AbstractIoSessionConfig() {
/*     */     protected void doSetAll(IoSessionConfig config) {  } } ;
/*     */ 
/*  80 */   private final IoFilterChain filterChain = new DefaultIoFilterChain(this);
/*     */   private final IoProcessor<AbstractIoSession> processor;
/*  83 */   private volatile IoHandler handler = new IoHandlerAdapter();
/*  84 */   private volatile SocketAddress localAddress = ANONYMOUS_ADDRESS;
/*  85 */   private volatile SocketAddress remoteAddress = ANONYMOUS_ADDRESS;
/*  86 */   private volatile TransportMetadata transportMetadata = TRANSPORT_METADATA;
/*     */ 
/*     */   public DummySession()
/*     */   {
/*  93 */     IoAcceptor acceptor = new AbstractIoAcceptor(new AbstractIoSessionConfig()
/*     */     {
/*     */       protected void doSetAll(IoSessionConfig config)
/*     */       {
/*     */       }
/*     */     }
/*     */     , new Executor()
/*     */     {
/*     */       public void execute(Runnable command)
/*     */       {
/*     */       }
/*     */ 
/*     */     })
/*     */     {
/*     */       protected Set<SocketAddress> bindInternal(List<? extends SocketAddress> localAddresses)
/*     */         throws Exception
/*     */       {
/* 104 */         throw new UnsupportedOperationException();
/*     */       }
/*     */ 
/*     */       protected void unbind0(List<? extends SocketAddress> localAddresses) throws Exception
/*     */       {
/* 109 */         throw new UnsupportedOperationException();
/*     */       }
/*     */ 
/*     */       public IoSession newSession(SocketAddress remoteAddress, SocketAddress localAddress) {
/* 113 */         throw new UnsupportedOperationException();
/*     */       }
/*     */ 
/*     */       public TransportMetadata getTransportMetadata() {
/* 117 */         return DummySession.TRANSPORT_METADATA;
/*     */       }
/*     */ 
/*     */       protected IoFuture dispose0() throws Exception
/*     */       {
/* 122 */         return null;
/*     */       }
/*     */     };
/* 127 */     acceptor.setHandler(new IoHandlerAdapter());
/*     */ 
/* 129 */     this.service = acceptor;
/*     */ 
/* 131 */     this.processor = new IoProcessor() {
/*     */       public void add(AbstractIoSession session) {
/*     */       }
/*     */ 
/*     */       public void flush(AbstractIoSession session) {
/* 136 */         DummySession s = (DummySession)session;
/* 137 */         WriteRequest req = s.getWriteRequestQueue().poll(session);
/*     */ 
/* 141 */         if (req != null) {
/* 142 */           Object m = req.getMessage();
/* 143 */           if ((m instanceof FileRegion)) {
/* 144 */             FileRegion file = (FileRegion)m;
/*     */             try {
/* 146 */               file.getFileChannel().position(file.getPosition() + file.getRemainingBytes());
/* 147 */               file.update(file.getRemainingBytes());
/*     */             } catch (IOException e) {
/* 149 */               s.getFilterChain().fireExceptionCaught(e);
/*     */             }
/*     */           }
/* 152 */           DummySession.this.getFilterChain().fireMessageSent(req);
/*     */         }
/*     */       }
/*     */ 
/*     */       public void remove(AbstractIoSession session) {
/* 157 */         if (!session.getCloseFuture().isClosed())
/* 158 */           session.getFilterChain().fireSessionClosed();
/*     */       }
/*     */ 
/*     */       public void updateTrafficControl(AbstractIoSession session)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void dispose() {
/*     */       }
/*     */ 
/*     */       public boolean isDisposed() {
/* 169 */         return false;
/*     */       }
/*     */ 
/*     */       public boolean isDisposing() {
/* 173 */         return false;
/*     */       }
/*     */     };
/*     */     try
/*     */     {
/* 179 */       IoSessionDataStructureFactory factory = new DefaultIoSessionDataStructureFactory();
/* 180 */       setAttributeMap(factory.getAttributeMap(this));
/* 181 */       setWriteRequestQueue(factory.getWriteRequestQueue(this));
/*     */     } catch (Exception e) {
/* 183 */       throw new InternalError();
/*     */     }
/*     */   }
/*     */ 
/*     */   public IoSessionConfig getConfig() {
/* 188 */     return this.config;
/*     */   }
/*     */ 
/*     */   public void setConfig(IoSessionConfig config)
/*     */   {
/* 195 */     if (config == null) {
/* 196 */       throw new NullPointerException("config");
/*     */     }
/*     */ 
/* 199 */     this.config = config;
/*     */   }
/*     */ 
/*     */   public IoFilterChain getFilterChain() {
/* 203 */     return this.filterChain;
/*     */   }
/*     */ 
/*     */   public IoHandler getHandler() {
/* 207 */     return this.handler;
/*     */   }
/*     */ 
/*     */   public void setHandler(IoHandler handler)
/*     */   {
/* 214 */     if (handler == null) {
/* 215 */       throw new NullPointerException("handler");
/*     */     }
/*     */ 
/* 218 */     this.handler = handler;
/*     */   }
/*     */ 
/*     */   public SocketAddress getLocalAddress() {
/* 222 */     return this.localAddress;
/*     */   }
/*     */ 
/*     */   public SocketAddress getRemoteAddress() {
/* 226 */     return this.remoteAddress;
/*     */   }
/*     */ 
/*     */   public void setLocalAddress(SocketAddress localAddress)
/*     */   {
/* 234 */     if (localAddress == null) {
/* 235 */       throw new NullPointerException("localAddress");
/*     */     }
/*     */ 
/* 238 */     this.localAddress = localAddress;
/*     */   }
/*     */ 
/*     */   public void setRemoteAddress(SocketAddress remoteAddress)
/*     */   {
/* 245 */     if (remoteAddress == null) {
/* 246 */       throw new NullPointerException("remoteAddress");
/*     */     }
/*     */ 
/* 249 */     this.remoteAddress = remoteAddress;
/*     */   }
/*     */ 
/*     */   public IoService getService() {
/* 253 */     return this.service;
/*     */   }
/*     */ 
/*     */   public void setService(IoService service)
/*     */   {
/* 260 */     if (service == null) {
/* 261 */       throw new NullPointerException("service");
/*     */     }
/*     */ 
/* 264 */     this.service = service;
/*     */   }
/*     */ 
/*     */   public final IoProcessor<AbstractIoSession> getProcessor()
/*     */   {
/* 269 */     return this.processor;
/*     */   }
/*     */ 
/*     */   public TransportMetadata getTransportMetadata() {
/* 273 */     return this.transportMetadata;
/*     */   }
/*     */ 
/*     */   public void setTransportMetadata(TransportMetadata transportMetadata)
/*     */   {
/* 280 */     if (transportMetadata == null) {
/* 281 */       throw new NullPointerException("transportMetadata");
/*     */     }
/*     */ 
/* 284 */     this.transportMetadata = transportMetadata;
/*     */   }
/*     */ 
/*     */   public void setScheduledWriteBytes(int byteCount)
/*     */   {
/* 289 */     super.setScheduledWriteBytes(byteCount);
/*     */   }
/*     */ 
/*     */   public void setScheduledWriteMessages(int messages)
/*     */   {
/* 294 */     super.setScheduledWriteMessages(messages);
/*     */   }
/*     */ 
/*     */   public void updateThroughput(boolean force)
/*     */   {
/* 306 */     super.updateThroughput(System.currentTimeMillis(), force);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.session.DummySession
 * JD-Core Version:    0.6.0
 */