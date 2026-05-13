/*     */ package org.apache.mina.transport.socket.nio;
/*     */ 
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.DatagramChannel;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.Selector;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.concurrent.Executor;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.polling.AbstractPollingConnectionlessIoAcceptor;
/*     */ import org.apache.mina.core.service.IoProcessor;
/*     */ import org.apache.mina.core.service.TransportMetadata;
/*     */ import org.apache.mina.transport.socket.DatagramAcceptor;
/*     */ import org.apache.mina.transport.socket.DatagramSessionConfig;
/*     */ import org.apache.mina.transport.socket.DefaultDatagramSessionConfig;
/*     */ 
/*     */ public final class NioDatagramAcceptor extends AbstractPollingConnectionlessIoAcceptor<NioSession, DatagramChannel>
/*     */   implements DatagramAcceptor
/*     */ {
/*     */   private volatile Selector selector;
/*     */ 
/*     */   public NioDatagramAcceptor()
/*     */   {
/*  57 */     super(new DefaultDatagramSessionConfig());
/*     */   }
/*     */ 
/*     */   public NioDatagramAcceptor(Executor executor)
/*     */   {
/*  64 */     super(new DefaultDatagramSessionConfig(), executor);
/*     */   }
/*     */ 
/*     */   protected void init() throws Exception
/*     */   {
/*  69 */     this.selector = Selector.open();
/*     */   }
/*     */ 
/*     */   protected void destroy() throws Exception
/*     */   {
/*  74 */     if (this.selector != null)
/*  75 */       this.selector.close();
/*     */   }
/*     */ 
/*     */   public TransportMetadata getTransportMetadata()
/*     */   {
/*  80 */     return NioDatagramSession.METADATA;
/*     */   }
/*     */ 
/*     */   public DatagramSessionConfig getSessionConfig()
/*     */   {
/*  85 */     return (DatagramSessionConfig)super.getSessionConfig();
/*     */   }
/*     */ 
/*     */   public InetSocketAddress getLocalAddress()
/*     */   {
/*  90 */     return (InetSocketAddress)super.getLocalAddress();
/*     */   }
/*     */ 
/*     */   public InetSocketAddress getDefaultLocalAddress()
/*     */   {
/*  95 */     return (InetSocketAddress)super.getDefaultLocalAddress();
/*     */   }
/*     */ 
/*     */   public void setDefaultLocalAddress(InetSocketAddress localAddress) {
/*  99 */     setDefaultLocalAddress(localAddress);
/*     */   }
/*     */ 
/*     */   protected DatagramChannel open(SocketAddress localAddress) throws Exception
/*     */   {
/* 104 */     DatagramChannel c = DatagramChannel.open();
/* 105 */     boolean success = false;
/*     */     try {
/* 107 */       new NioDatagramSessionConfig(c).setAll(getSessionConfig());
/* 108 */       c.configureBlocking(false);
/* 109 */       c.socket().bind(localAddress);
/* 110 */       c.register(this.selector, 1);
/* 111 */       success = true;
/*     */     } finally {
/* 113 */       if (!success) {
/* 114 */         close(c);
/*     */       }
/*     */     }
/*     */ 
/* 118 */     return c;
/*     */   }
/*     */ 
/*     */   protected boolean isReadable(DatagramChannel handle)
/*     */   {
/* 123 */     SelectionKey key = handle.keyFor(this.selector);
/*     */ 
/* 125 */     if ((key == null) || (!key.isValid())) {
/* 126 */       return false;
/*     */     }
/*     */ 
/* 129 */     return key.isReadable();
/*     */   }
/*     */ 
/*     */   protected boolean isWritable(DatagramChannel handle)
/*     */   {
/* 134 */     SelectionKey key = handle.keyFor(this.selector);
/*     */ 
/* 136 */     if ((key == null) || (!key.isValid())) {
/* 137 */       return false;
/*     */     }
/*     */ 
/* 140 */     return key.isWritable();
/*     */   }
/*     */ 
/*     */   protected SocketAddress localAddress(DatagramChannel handle)
/*     */     throws Exception
/*     */   {
/* 146 */     return handle.socket().getLocalSocketAddress();
/*     */   }
/*     */ 
/*     */   protected NioSession newSession(IoProcessor<NioSession> processor, DatagramChannel handle, SocketAddress remoteAddress)
/*     */   {
/* 153 */     SelectionKey key = handle.keyFor(this.selector);
/*     */ 
/* 155 */     if ((key == null) || (!key.isValid())) {
/* 156 */       return null;
/*     */     }
/*     */ 
/* 159 */     NioDatagramSession newSession = new NioDatagramSession(this, handle, processor, remoteAddress);
/*     */ 
/* 161 */     newSession.setSelectionKey(key);
/*     */ 
/* 163 */     return newSession;
/*     */   }
/*     */ 
/*     */   protected SocketAddress receive(DatagramChannel handle, IoBuffer buffer)
/*     */     throws Exception
/*     */   {
/* 169 */     return handle.receive(buffer.buf());
/*     */   }
/*     */ 
/*     */   protected int select() throws Exception
/*     */   {
/* 174 */     return this.selector.select();
/*     */   }
/*     */ 
/*     */   protected int select(int timeout) throws Exception
/*     */   {
/* 179 */     return this.selector.select(timeout);
/*     */   }
/*     */ 
/*     */   protected Iterator<DatagramChannel> selectedHandles()
/*     */   {
/* 184 */     return new DatagramChannelIterator(this.selector.selectedKeys(), null);
/*     */   }
/*     */ 
/*     */   protected int send(NioSession session, IoBuffer buffer, SocketAddress remoteAddress)
/*     */     throws Exception
/*     */   {
/* 190 */     return ((DatagramChannel)session.getChannel()).send(buffer.buf(), remoteAddress);
/*     */   }
/*     */ 
/*     */   protected void setInterestedInWrite(NioSession session, boolean interested)
/*     */     throws Exception
/*     */   {
/* 197 */     SelectionKey key = session.getSelectionKey();
/* 198 */     if (key == null) {
/* 199 */       return;
/*     */     }
/*     */ 
/* 202 */     if (interested)
/* 203 */       key.interestOps(key.interestOps() | 0x4);
/*     */     else
/* 205 */       key.interestOps(key.interestOps() & 0xFFFFFFFB);
/*     */   }
/*     */ 
/*     */   protected void close(DatagramChannel handle)
/*     */     throws Exception
/*     */   {
/* 211 */     SelectionKey key = handle.keyFor(this.selector);
/*     */ 
/* 213 */     if (key != null) {
/* 214 */       key.cancel();
/*     */     }
/*     */ 
/* 217 */     handle.disconnect();
/* 218 */     handle.close();
/*     */   }
/*     */ 
/*     */   protected void wakeup()
/*     */   {
/* 223 */     this.selector.wakeup();
/*     */   }
/*     */ 
/*     */   private static class DatagramChannelIterator implements Iterator<DatagramChannel> {
/*     */     private final Iterator<SelectionKey> i;
/*     */ 
/*     */     private DatagramChannelIterator(Collection<SelectionKey> keys) {
/* 231 */       this.i = keys.iterator();
/*     */     }
/*     */ 
/*     */     public boolean hasNext() {
/* 235 */       return this.i.hasNext();
/*     */     }
/*     */ 
/*     */     public DatagramChannel next() {
/* 239 */       return (DatagramChannel)((SelectionKey)this.i.next()).channel();
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 243 */       this.i.remove();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.transport.socket.nio.NioDatagramAcceptor
 * JD-Core Version:    0.6.0
 */