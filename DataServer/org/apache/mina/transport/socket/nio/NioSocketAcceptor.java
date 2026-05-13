/*     */ package org.apache.mina.transport.socket.nio;
/*     */ 
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.concurrent.Executor;
/*     */ import org.apache.mina.core.polling.AbstractPollingIoAcceptor;
/*     */ import org.apache.mina.core.service.IoProcessor;
/*     */ import org.apache.mina.core.service.TransportMetadata;
/*     */ import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
/*     */ import org.apache.mina.transport.socket.SocketAcceptor;
/*     */ import org.apache.mina.transport.socket.SocketSessionConfig;
/*     */ 
/*     */ public final class NioSocketAcceptor extends AbstractPollingIoAcceptor<NioSession, ServerSocketChannel>
/*     */   implements SocketAcceptor
/*     */ {
/*  57 */   private int backlog = 50;
/*     */ 
/*  59 */   private boolean reuseAddress = false;
/*     */   private volatile Selector selector;
/*     */ 
/*     */   public NioSocketAcceptor()
/*     */   {
/*  67 */     super(new DefaultSocketSessionConfig(), NioProcessor.class);
/*  68 */     ((DefaultSocketSessionConfig)getSessionConfig()).init(this);
/*     */   }
/*     */ 
/*     */   public NioSocketAcceptor(int processorCount)
/*     */   {
/*  79 */     super(new DefaultSocketSessionConfig(), NioProcessor.class, processorCount);
/*  80 */     ((DefaultSocketSessionConfig)getSessionConfig()).init(this);
/*     */   }
/*     */ 
/*     */   public NioSocketAcceptor(IoProcessor<NioSession> processor)
/*     */   {
/*  90 */     super(new DefaultSocketSessionConfig(), processor);
/*  91 */     ((DefaultSocketSessionConfig)getSessionConfig()).init(this);
/*     */   }
/*     */ 
/*     */   public NioSocketAcceptor(Executor executor, IoProcessor<NioSession> processor)
/*     */   {
/* 102 */     super(new DefaultSocketSessionConfig(), executor, processor);
/* 103 */     ((DefaultSocketSessionConfig)getSessionConfig()).init(this);
/*     */   }
/*     */ 
/*     */   protected void init()
/*     */     throws Exception
/*     */   {
/* 111 */     this.selector = Selector.open();
/*     */   }
/*     */ 
/*     */   protected void destroy()
/*     */     throws Exception
/*     */   {
/* 119 */     if (this.selector != null)
/* 120 */       this.selector.close();
/*     */   }
/*     */ 
/*     */   public TransportMetadata getTransportMetadata()
/*     */   {
/* 128 */     return NioSocketSession.METADATA;
/*     */   }
/*     */ 
/*     */   public SocketSessionConfig getSessionConfig()
/*     */   {
/* 136 */     return (SocketSessionConfig)super.getSessionConfig();
/*     */   }
/*     */ 
/*     */   public InetSocketAddress getLocalAddress()
/*     */   {
/* 144 */     return (InetSocketAddress)super.getLocalAddress();
/*     */   }
/*     */ 
/*     */   public InetSocketAddress getDefaultLocalAddress()
/*     */   {
/* 152 */     return (InetSocketAddress)super.getDefaultLocalAddress();
/*     */   }
/*     */ 
/*     */   public void setDefaultLocalAddress(InetSocketAddress localAddress)
/*     */   {
/* 159 */     setDefaultLocalAddress(localAddress);
/*     */   }
/*     */ 
/*     */   public boolean isReuseAddress()
/*     */   {
/* 166 */     return this.reuseAddress;
/*     */   }
/*     */ 
/*     */   public void setReuseAddress(boolean reuseAddress)
/*     */   {
/* 173 */     synchronized (this.bindLock) {
/* 174 */       if (isActive()) {
/* 175 */         throw new IllegalStateException("reuseAddress can't be set while the acceptor is bound.");
/*     */       }
/*     */ 
/* 179 */       this.reuseAddress = reuseAddress;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getBacklog()
/*     */   {
/* 187 */     return this.backlog;
/*     */   }
/*     */ 
/*     */   public void setBacklog(int backlog)
/*     */   {
/* 194 */     synchronized (this.bindLock) {
/* 195 */       if (isActive()) {
/* 196 */         throw new IllegalStateException("backlog can't be set while the acceptor is bound.");
/*     */       }
/*     */ 
/* 200 */       this.backlog = backlog;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected NioSession accept(IoProcessor<NioSession> processor, ServerSocketChannel handle)
/*     */     throws Exception
/*     */   {
/* 211 */     SelectionKey key = handle.keyFor(this.selector);
/*     */ 
/* 213 */     if ((key == null) || (!key.isValid()) || (!key.isAcceptable())) {
/* 214 */       return null;
/*     */     }
/*     */ 
/* 218 */     SocketChannel ch = handle.accept();
/*     */ 
/* 220 */     if (ch == null) {
/* 221 */       return null;
/*     */     }
/*     */ 
/* 224 */     return new NioSocketSession(this, processor, ch);
/*     */   }
/*     */ 
/*     */   protected ServerSocketChannel open(SocketAddress localAddress)
/*     */     throws Exception
/*     */   {
/* 234 */     ServerSocketChannel channel = ServerSocketChannel.open();
/*     */ 
/* 236 */     boolean success = false;
/*     */     try
/*     */     {
/* 240 */       channel.configureBlocking(false);
/*     */ 
/* 243 */       ServerSocket socket = channel.socket();
/*     */ 
/* 246 */       socket.setReuseAddress(isReuseAddress());
/*     */ 
/* 249 */       socket.setReceiveBufferSize(getSessionConfig().getReceiveBufferSize());
/*     */ 
/* 252 */       socket.bind(localAddress, getBacklog());
/*     */ 
/* 255 */       channel.register(this.selector, 16);
/* 256 */       success = true;
/*     */     } finally {
/* 258 */       if (!success) {
/* 259 */         close(channel);
/*     */       }
/*     */     }
/* 262 */     return channel;
/*     */   }
/*     */ 
/*     */   protected SocketAddress localAddress(ServerSocketChannel handle)
/*     */     throws Exception
/*     */   {
/* 271 */     return handle.socket().getLocalSocketAddress();
/*     */   }
/*     */ 
/*     */   protected int select()
/*     */     throws Exception
/*     */   {
/* 289 */     return this.selector.select();
/*     */   }
/*     */ 
/*     */   protected Iterator<ServerSocketChannel> selectedHandles()
/*     */   {
/* 297 */     return new ServerSocketChannelIterator(this.selector.selectedKeys(), null);
/*     */   }
/*     */ 
/*     */   protected void close(ServerSocketChannel handle)
/*     */     throws Exception
/*     */   {
/* 305 */     SelectionKey key = handle.keyFor(this.selector);
/*     */ 
/* 307 */     if (key != null) {
/* 308 */       key.cancel();
/*     */     }
/*     */ 
/* 311 */     handle.close();
/*     */   }
/*     */ 
/*     */   protected void wakeup()
/*     */   {
/* 319 */     this.selector.wakeup();
/*     */   }
/*     */ 
/*     */   private static class ServerSocketChannelIterator
/*     */     implements Iterator<ServerSocketChannel>
/*     */   {
/*     */     private final Iterator<SelectionKey> iterator;
/*     */ 
/*     */     private ServerSocketChannelIterator(Collection<SelectionKey> selectedKeys)
/*     */     {
/* 337 */       this.iterator = selectedKeys.iterator();
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 346 */       return this.iterator.hasNext();
/*     */     }
/*     */ 
/*     */     public ServerSocketChannel next()
/*     */     {
/* 356 */       SelectionKey key = (SelectionKey)this.iterator.next();
/*     */ 
/* 358 */       if ((key.isValid()) && (key.isAcceptable())) {
/* 359 */         return (ServerSocketChannel)key.channel();
/*     */       }
/* 361 */       return null;
/*     */     }
/*     */ 
/*     */     public void remove()
/*     */     {
/* 369 */       this.iterator.remove();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.transport.socket.nio.NioSocketAcceptor
 * JD-Core Version:    0.6.0
 */