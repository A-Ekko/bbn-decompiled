/*     */ package org.apache.mina.transport.socket.nio;
/*     */ 
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.concurrent.Executor;
/*     */ import org.apache.mina.core.polling.AbstractPollingIoConnector;
/*     */ import org.apache.mina.core.polling.AbstractPollingIoConnector.ConnectionRequest;
/*     */ import org.apache.mina.core.service.IoProcessor;
/*     */ import org.apache.mina.core.service.TransportMetadata;
/*     */ import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
/*     */ import org.apache.mina.transport.socket.SocketConnector;
/*     */ import org.apache.mina.transport.socket.SocketSessionConfig;
/*     */ 
/*     */ public final class NioSocketConnector extends AbstractPollingIoConnector<NioSession, SocketChannel>
/*     */   implements SocketConnector
/*     */ {
/*     */   private volatile Selector selector;
/*     */ 
/*     */   public NioSocketConnector()
/*     */   {
/*  56 */     super(new DefaultSocketSessionConfig(), NioProcessor.class);
/*  57 */     ((DefaultSocketSessionConfig)getSessionConfig()).init(this);
/*     */   }
/*     */ 
/*     */   public NioSocketConnector(int processorCount)
/*     */   {
/*  67 */     super(new DefaultSocketSessionConfig(), NioProcessor.class, processorCount);
/*  68 */     ((DefaultSocketSessionConfig)getSessionConfig()).init(this);
/*     */   }
/*     */ 
/*     */   public NioSocketConnector(IoProcessor<NioSession> processor)
/*     */   {
/*  78 */     super(new DefaultSocketSessionConfig(), processor);
/*  79 */     ((DefaultSocketSessionConfig)getSessionConfig()).init(this);
/*     */   }
/*     */ 
/*     */   public NioSocketConnector(Executor executor, IoProcessor<NioSession> processor)
/*     */   {
/*  90 */     super(new DefaultSocketSessionConfig(), executor, processor);
/*  91 */     ((DefaultSocketSessionConfig)getSessionConfig()).init(this);
/*     */   }
/*     */ 
/*     */   public NioSocketConnector(Class<? extends IoProcessor<NioSession>> processorClass, int processorCount)
/*     */   {
/* 107 */     super(new DefaultSocketSessionConfig(), processorClass, processorCount);
/*     */   }
/*     */ 
/*     */   public NioSocketConnector(Class<? extends IoProcessor<NioSession>> processorClass)
/*     */   {
/* 123 */     super(new DefaultSocketSessionConfig(), processorClass);
/*     */   }
/*     */ 
/*     */   protected void init()
/*     */     throws Exception
/*     */   {
/* 131 */     this.selector = Selector.open();
/*     */   }
/*     */ 
/*     */   protected void destroy()
/*     */     throws Exception
/*     */   {
/* 139 */     if (this.selector != null)
/* 140 */       this.selector.close();
/*     */   }
/*     */ 
/*     */   public TransportMetadata getTransportMetadata()
/*     */   {
/* 148 */     return NioSocketSession.METADATA;
/*     */   }
/*     */ 
/*     */   public SocketSessionConfig getSessionConfig()
/*     */   {
/* 156 */     return (SocketSessionConfig)super.getSessionConfig();
/*     */   }
/*     */ 
/*     */   public InetSocketAddress getDefaultRemoteAddress()
/*     */   {
/* 164 */     return (InetSocketAddress)super.getDefaultRemoteAddress();
/*     */   }
/*     */ 
/*     */   public void setDefaultRemoteAddress(InetSocketAddress defaultRemoteAddress)
/*     */   {
/* 171 */     super.setDefaultRemoteAddress(defaultRemoteAddress);
/*     */   }
/*     */ 
/*     */   protected Iterator<SocketChannel> allHandles()
/*     */   {
/* 179 */     return new SocketChannelIterator(this.selector.keys(), null);
/*     */   }
/*     */ 
/*     */   protected boolean connect(SocketChannel handle, SocketAddress remoteAddress)
/*     */     throws Exception
/*     */   {
/* 188 */     return handle.connect(remoteAddress);
/*     */   }
/*     */ 
/*     */   protected AbstractPollingIoConnector<NioSession, SocketChannel>.ConnectionRequest getConnectionRequest(SocketChannel handle)
/*     */   {
/* 196 */     SelectionKey key = handle.keyFor(this.selector);
/*     */ 
/* 198 */     if ((key == null) || (!key.isValid())) {
/* 199 */       return null;
/*     */     }
/*     */ 
/* 202 */     return (AbstractPollingIoConnector.ConnectionRequest)key.attachment();
/*     */   }
/*     */ 
/*     */   protected void close(SocketChannel handle)
/*     */     throws Exception
/*     */   {
/* 210 */     SelectionKey key = handle.keyFor(this.selector);
/*     */ 
/* 212 */     if (key != null) {
/* 213 */       key.cancel();
/*     */     }
/*     */ 
/* 216 */     handle.close();
/*     */   }
/*     */ 
/*     */   protected boolean finishConnect(SocketChannel handle)
/*     */     throws Exception
/*     */   {
/* 224 */     if (handle.finishConnect()) {
/* 225 */       SelectionKey key = handle.keyFor(this.selector);
/*     */ 
/* 227 */       if (key != null) {
/* 228 */         key.cancel();
/*     */       }
/*     */ 
/* 231 */       return true;
/*     */     }
/*     */ 
/* 234 */     return false;
/*     */   }
/*     */ 
/*     */   protected SocketChannel newHandle(SocketAddress localAddress)
/*     */     throws Exception
/*     */   {
/* 243 */     SocketChannel ch = SocketChannel.open();
/*     */ 
/* 245 */     int receiveBufferSize = getSessionConfig().getReceiveBufferSize();
/*     */ 
/* 247 */     if (receiveBufferSize > 65535) {
/* 248 */       ch.socket().setReceiveBufferSize(receiveBufferSize);
/*     */     }
/*     */ 
/* 251 */     if (localAddress != null) {
/* 252 */       ch.socket().bind(localAddress);
/*     */     }
/* 254 */     ch.configureBlocking(false);
/* 255 */     return ch;
/*     */   }
/*     */ 
/*     */   protected NioSession newSession(IoProcessor<NioSession> processor, SocketChannel handle)
/*     */   {
/* 263 */     return new NioSocketSession(this, processor, handle);
/*     */   }
/*     */ 
/*     */   protected void register(SocketChannel handle, AbstractPollingIoConnector<NioSession, SocketChannel>.ConnectionRequest request)
/*     */     throws Exception
/*     */   {
/* 272 */     handle.register(this.selector, 8, request);
/*     */   }
/*     */ 
/*     */   protected int select(int timeout)
/*     */     throws Exception
/*     */   {
/* 280 */     return this.selector.select(timeout);
/*     */   }
/*     */ 
/*     */   protected Iterator<SocketChannel> selectedHandles()
/*     */   {
/* 288 */     return new SocketChannelIterator(this.selector.selectedKeys(), null);
/*     */   }
/*     */ 
/*     */   protected void wakeup()
/*     */   {
/* 296 */     this.selector.wakeup();
/*     */   }
/*     */ 
/*     */   private static class SocketChannelIterator implements Iterator<SocketChannel> {
/*     */     private final Iterator<SelectionKey> i;
/*     */ 
/*     */     private SocketChannelIterator(Collection<SelectionKey> selectedKeys) {
/* 304 */       this.i = selectedKeys.iterator();
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 311 */       return this.i.hasNext();
/*     */     }
/*     */ 
/*     */     public SocketChannel next()
/*     */     {
/* 318 */       SelectionKey key = (SelectionKey)this.i.next();
/* 319 */       return (SocketChannel)key.channel();
/*     */     }
/*     */ 
/*     */     public void remove()
/*     */     {
/* 326 */       this.i.remove();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.transport.socket.nio.NioSocketConnector
 * JD-Core Version:    0.6.0
 */