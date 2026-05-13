/*     */ package org.apache.mina.transport.socket.nio;
/*     */ 
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import org.apache.mina.core.RuntimeIoException;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.file.FileRegion;
/*     */ import org.apache.mina.core.filterchain.DefaultIoFilterChain;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.service.DefaultTransportMetadata;
/*     */ import org.apache.mina.core.service.IoHandler;
/*     */ import org.apache.mina.core.service.IoProcessor;
/*     */ import org.apache.mina.core.service.IoService;
/*     */ import org.apache.mina.core.service.TransportMetadata;
/*     */ import org.apache.mina.transport.socket.AbstractSocketSessionConfig;
/*     */ import org.apache.mina.transport.socket.SocketSessionConfig;
/*     */ 
/*     */ class NioSocketSession extends NioSession
/*     */ {
/*  50 */   static final TransportMetadata METADATA = new DefaultTransportMetadata("nio", "socket", false, true, InetSocketAddress.class, SocketSessionConfig.class, new Class[] { IoBuffer.class, FileRegion.class });
/*     */   private final IoService service;
/*  59 */   private final SocketSessionConfig config = new SessionConfigImpl(null);
/*     */   private final IoProcessor<NioSession> processor;
/*  63 */   private final IoFilterChain filterChain = new DefaultIoFilterChain(this);
/*     */   private final SocketChannel ch;
/*     */   private final IoHandler handler;
/*     */   private SelectionKey key;
/*     */ 
/*     */   public NioSocketSession(IoService service, IoProcessor<NioSession> processor, SocketChannel ch)
/*     */   {
/*  81 */     this.service = service;
/*  82 */     this.processor = processor;
/*  83 */     this.ch = ch;
/*  84 */     this.handler = service.getHandler();
/*  85 */     this.config.setAll(service.getSessionConfig());
/*     */   }
/*     */ 
/*     */   public IoService getService() {
/*  89 */     return this.service;
/*     */   }
/*     */ 
/*     */   public SocketSessionConfig getConfig() {
/*  93 */     return this.config;
/*     */   }
/*     */ 
/*     */   public IoProcessor<NioSession> getProcessor()
/*     */   {
/*  98 */     return this.processor;
/*     */   }
/*     */ 
/*     */   public IoFilterChain getFilterChain() {
/* 102 */     return this.filterChain;
/*     */   }
/*     */ 
/*     */   public TransportMetadata getTransportMetadata() {
/* 106 */     return METADATA;
/*     */   }
/*     */ 
/*     */   SocketChannel getChannel()
/*     */   {
/* 111 */     return this.ch;
/*     */   }
/*     */ 
/*     */   SelectionKey getSelectionKey()
/*     */   {
/* 116 */     return this.key;
/*     */   }
/*     */ 
/*     */   void setSelectionKey(SelectionKey key)
/*     */   {
/* 121 */     this.key = key;
/*     */   }
/*     */ 
/*     */   public IoHandler getHandler() {
/* 125 */     return this.handler;
/*     */   }
/*     */ 
/*     */   public InetSocketAddress getRemoteAddress()
/*     */   {
/* 132 */     if (this.ch == null) {
/* 133 */       return null;
/*     */     }
/*     */ 
/* 136 */     Socket socket = this.ch.socket();
/*     */ 
/* 138 */     if (socket == null) {
/* 139 */       return null;
/*     */     }
/*     */ 
/* 142 */     return (InetSocketAddress)socket.getRemoteSocketAddress();
/*     */   }
/*     */ 
/*     */   public InetSocketAddress getLocalAddress()
/*     */   {
/* 149 */     if (this.ch == null) {
/* 150 */       return null;
/*     */     }
/*     */ 
/* 153 */     Socket socket = this.ch.socket();
/*     */ 
/* 155 */     if (socket == null) {
/* 156 */       return null;
/*     */     }
/*     */ 
/* 159 */     return (InetSocketAddress)socket.getLocalSocketAddress();
/*     */   }
/*     */ 
/*     */   public InetSocketAddress getServiceAddress()
/*     */   {
/* 164 */     return (InetSocketAddress)super.getServiceAddress();
/*     */   }
/*     */   private class SessionConfigImpl extends AbstractSocketSessionConfig {
/*     */     private SessionConfigImpl() {
/*     */     }
/*     */     public boolean isKeepAlive() {
/*     */       try { return NioSocketSession.this.ch.socket().getKeepAlive(); } catch (SocketException e) {
/*     */       }
/* 172 */       throw new RuntimeIoException(e);
/*     */     }
/*     */ 
/*     */     public void setKeepAlive(boolean on)
/*     */     {
/*     */       try {
/* 178 */         NioSocketSession.this.ch.socket().setKeepAlive(on);
/*     */       } catch (SocketException e) {
/* 180 */         throw new RuntimeIoException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean isOobInline() {
/*     */       try {
/* 186 */         return NioSocketSession.this.ch.socket().getOOBInline(); } catch (SocketException e) {
/*     */       }
/* 188 */       throw new RuntimeIoException(e);
/*     */     }
/*     */ 
/*     */     public void setOobInline(boolean on)
/*     */     {
/*     */       try {
/* 194 */         NioSocketSession.this.ch.socket().setOOBInline(on);
/*     */       } catch (SocketException e) {
/* 196 */         throw new RuntimeIoException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean isReuseAddress() {
/*     */       try {
/* 202 */         return NioSocketSession.this.ch.socket().getReuseAddress(); } catch (SocketException e) {
/*     */       }
/* 204 */       throw new RuntimeIoException(e);
/*     */     }
/*     */ 
/*     */     public void setReuseAddress(boolean on)
/*     */     {
/*     */       try {
/* 210 */         NioSocketSession.this.ch.socket().setReuseAddress(on);
/*     */       } catch (SocketException e) {
/* 212 */         throw new RuntimeIoException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public int getSoLinger() {
/*     */       try {
/* 218 */         return NioSocketSession.this.ch.socket().getSoLinger(); } catch (SocketException e) {
/*     */       }
/* 220 */       throw new RuntimeIoException(e);
/*     */     }
/*     */ 
/*     */     public void setSoLinger(int linger)
/*     */     {
/*     */       try {
/* 226 */         if (linger < 0)
/* 227 */           NioSocketSession.this.ch.socket().setSoLinger(false, 0);
/*     */         else
/* 229 */           NioSocketSession.this.ch.socket().setSoLinger(true, linger);
/*     */       }
/*     */       catch (SocketException e) {
/* 232 */         throw new RuntimeIoException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean isTcpNoDelay() {
/* 237 */       if (!NioSocketSession.this.isConnected()) {
/* 238 */         return false;
/*     */       }
/*     */       try
/*     */       {
/* 242 */         return NioSocketSession.this.ch.socket().getTcpNoDelay(); } catch (SocketException e) {
/*     */       }
/* 244 */       throw new RuntimeIoException(e);
/*     */     }
/*     */ 
/*     */     public void setTcpNoDelay(boolean on)
/*     */     {
/*     */       try {
/* 250 */         NioSocketSession.this.ch.socket().setTcpNoDelay(on);
/*     */       } catch (SocketException e) {
/* 252 */         throw new RuntimeIoException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public int getTrafficClass()
/*     */     {
/*     */       try
/*     */       {
/* 261 */         return NioSocketSession.this.ch.socket().getTrafficClass(); } catch (SocketException e) {
/*     */       }
/* 263 */       throw new RuntimeIoException(e);
/*     */     }
/*     */ 
/*     */     public void setTrafficClass(int tc)
/*     */     {
/*     */       try
/*     */       {
/* 272 */         NioSocketSession.this.ch.socket().setTrafficClass(tc);
/*     */       } catch (SocketException e) {
/* 274 */         throw new RuntimeIoException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public int getSendBufferSize() {
/*     */       try {
/* 280 */         return NioSocketSession.this.ch.socket().getSendBufferSize(); } catch (SocketException e) {
/*     */       }
/* 282 */       throw new RuntimeIoException(e);
/*     */     }
/*     */ 
/*     */     public void setSendBufferSize(int size)
/*     */     {
/*     */       try {
/* 288 */         NioSocketSession.this.ch.socket().setSendBufferSize(size);
/*     */       } catch (SocketException e) {
/* 290 */         throw new RuntimeIoException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public int getReceiveBufferSize() {
/*     */       try {
/* 296 */         return NioSocketSession.this.ch.socket().getReceiveBufferSize(); } catch (SocketException e) {
/*     */       }
/* 298 */       throw new RuntimeIoException(e);
/*     */     }
/*     */ 
/*     */     public void setReceiveBufferSize(int size)
/*     */     {
/*     */       try {
/* 304 */         NioSocketSession.this.ch.socket().setReceiveBufferSize(size);
/*     */       } catch (SocketException e) {
/* 306 */         throw new RuntimeIoException(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.transport.socket.nio.NioSocketSession
 * JD-Core Version:    0.6.0
 */