/*     */ package org.apache.mina.transport.socket.nio;
/*     */ 
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.DatagramChannel;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.filterchain.DefaultIoFilterChain;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.service.DefaultTransportMetadata;
/*     */ import org.apache.mina.core.service.IoHandler;
/*     */ import org.apache.mina.core.service.IoProcessor;
/*     */ import org.apache.mina.core.service.IoService;
/*     */ import org.apache.mina.core.service.TransportMetadata;
/*     */ import org.apache.mina.transport.socket.DatagramSessionConfig;
/*     */ 
/*     */ class NioDatagramSession extends NioSession
/*     */ {
/*  46 */   static final TransportMetadata METADATA = new DefaultTransportMetadata("nio", "datagram", true, false, InetSocketAddress.class, DatagramSessionConfig.class, new Class[] { IoBuffer.class });
/*     */   private final IoService service;
/*     */   private final DatagramSessionConfig config;
/*  54 */   private final IoFilterChain filterChain = new DefaultIoFilterChain(this);
/*     */   private final DatagramChannel ch;
/*     */   private final IoHandler handler;
/*     */   private final InetSocketAddress localAddress;
/*     */   private final InetSocketAddress remoteAddress;
/*     */   private final IoProcessor<NioSession> processor;
/*     */   private SelectionKey key;
/*     */ 
/*     */   NioDatagramSession(IoService service, DatagramChannel ch, IoProcessor<NioSession> processor, SocketAddress remoteAddress)
/*     */   {
/*  69 */     this.service = service;
/*  70 */     this.ch = ch;
/*  71 */     this.config = new NioDatagramSessionConfig(ch);
/*  72 */     this.handler = service.getHandler();
/*  73 */     this.processor = processor;
/*  74 */     this.remoteAddress = ((InetSocketAddress)remoteAddress);
/*  75 */     this.localAddress = ((InetSocketAddress)ch.socket().getLocalSocketAddress());
/*     */   }
/*     */ 
/*     */   NioDatagramSession(IoService service, DatagramChannel ch, IoProcessor<NioSession> processor)
/*     */   {
/*  83 */     this(service, ch, processor, ch.socket().getRemoteSocketAddress());
/*     */   }
/*     */ 
/*     */   public IoService getService() {
/*  87 */     return this.service;
/*     */   }
/*     */ 
/*     */   public IoProcessor<NioSession> getProcessor()
/*     */   {
/*  92 */     return this.processor;
/*     */   }
/*     */ 
/*     */   public DatagramSessionConfig getConfig() {
/*  96 */     return this.config;
/*     */   }
/*     */ 
/*     */   public IoFilterChain getFilterChain() {
/* 100 */     return this.filterChain;
/*     */   }
/*     */ 
/*     */   DatagramChannel getChannel()
/*     */   {
/* 105 */     return this.ch;
/*     */   }
/*     */ 
/*     */   SelectionKey getSelectionKey()
/*     */   {
/* 110 */     return this.key;
/*     */   }
/*     */ 
/*     */   void setSelectionKey(SelectionKey key)
/*     */   {
/* 115 */     this.key = key;
/*     */   }
/*     */ 
/*     */   public IoHandler getHandler() {
/* 119 */     return this.handler;
/*     */   }
/*     */ 
/*     */   public TransportMetadata getTransportMetadata() {
/* 123 */     return METADATA;
/*     */   }
/*     */ 
/*     */   public InetSocketAddress getRemoteAddress() {
/* 127 */     return this.remoteAddress;
/*     */   }
/*     */ 
/*     */   public InetSocketAddress getLocalAddress() {
/* 131 */     return this.localAddress;
/*     */   }
/*     */ 
/*     */   public InetSocketAddress getServiceAddress()
/*     */   {
/* 136 */     return (InetSocketAddress)super.getServiceAddress();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.transport.socket.nio.NioDatagramSession
 * JD-Core Version:    0.6.0
 */