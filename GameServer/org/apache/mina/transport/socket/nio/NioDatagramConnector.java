/*     */ package org.apache.mina.transport.socket.nio;
/*     */ 
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.DatagramChannel;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.apache.mina.core.polling.AbstractPollingIoConnector;
/*     */ import org.apache.mina.core.polling.AbstractPollingIoConnector.ConnectionRequest;
/*     */ import org.apache.mina.core.service.IoProcessor;
/*     */ import org.apache.mina.core.service.TransportMetadata;
/*     */ import org.apache.mina.core.session.IoSessionConfig;
/*     */ import org.apache.mina.transport.socket.DatagramConnector;
/*     */ import org.apache.mina.transport.socket.DatagramSessionConfig;
/*     */ import org.apache.mina.transport.socket.DefaultDatagramSessionConfig;
/*     */ 
/*     */ public final class NioDatagramConnector extends AbstractPollingIoConnector<NioSession, DatagramChannel>
/*     */   implements DatagramConnector
/*     */ {
/*     */   public NioDatagramConnector()
/*     */   {
/*  51 */     super(new DefaultDatagramSessionConfig(), NioProcessor.class);
/*     */   }
/*     */ 
/*     */   public NioDatagramConnector(int processorCount)
/*     */   {
/*  58 */     super(new DefaultDatagramSessionConfig(), NioProcessor.class, processorCount);
/*     */   }
/*     */ 
/*     */   public NioDatagramConnector(IoProcessor<NioSession> processor)
/*     */   {
/*  65 */     super(new DefaultDatagramSessionConfig(), processor);
/*     */   }
/*     */ 
/*     */   public NioDatagramConnector(Class<? extends IoProcessor<NioSession>> processorClass, int processorCount)
/*     */   {
/*  81 */     super(new DefaultDatagramSessionConfig(), processorClass, processorCount);
/*     */   }
/*     */ 
/*     */   public NioDatagramConnector(Class<? extends IoProcessor<NioSession>> processorClass)
/*     */   {
/*  97 */     super(new DefaultDatagramSessionConfig(), processorClass);
/*     */   }
/*     */ 
/*     */   public TransportMetadata getTransportMetadata() {
/* 101 */     return NioDatagramSession.METADATA;
/*     */   }
/*     */ 
/*     */   public DatagramSessionConfig getSessionConfig()
/*     */   {
/* 106 */     return (DatagramSessionConfig)super.getSessionConfig();
/*     */   }
/*     */ 
/*     */   public InetSocketAddress getDefaultRemoteAddress()
/*     */   {
/* 111 */     return (InetSocketAddress)super.getDefaultRemoteAddress();
/*     */   }
/*     */ 
/*     */   public void setDefaultRemoteAddress(InetSocketAddress defaultRemoteAddress) {
/* 115 */     super.setDefaultRemoteAddress(defaultRemoteAddress);
/*     */   }
/*     */ 
/*     */   protected void init()
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   protected DatagramChannel newHandle(SocketAddress localAddress) throws Exception
/*     */   {
/* 125 */     DatagramChannel ch = DatagramChannel.open();
/*     */     try
/*     */     {
/* 128 */       if (localAddress != null) {
/* 129 */         ch.socket().bind(localAddress);
/*     */       }
/*     */ 
/* 132 */       return ch;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 136 */       ch.close();
/* 137 */     }throw e;
/*     */   }
/*     */ 
/*     */   protected boolean connect(DatagramChannel handle, SocketAddress remoteAddress)
/*     */     throws Exception
/*     */   {
/* 144 */     handle.connect(remoteAddress);
/* 145 */     return true;
/*     */   }
/*     */ 
/*     */   protected NioSession newSession(IoProcessor<NioSession> processor, DatagramChannel handle)
/*     */   {
/* 151 */     NioSession session = new NioDatagramSession(this, handle, processor);
/* 152 */     session.getConfig().setAll(getSessionConfig());
/* 153 */     return session;
/*     */   }
/*     */ 
/*     */   protected void close(DatagramChannel handle) throws Exception
/*     */   {
/* 158 */     handle.disconnect();
/* 159 */     handle.close();
/*     */   }
/*     */ 
/*     */   protected Iterator<DatagramChannel> allHandles()
/*     */   {
/* 166 */     return Collections.EMPTY_LIST.iterator();
/*     */   }
/*     */ 
/*     */   protected AbstractPollingIoConnector<NioSession, DatagramChannel>.ConnectionRequest getConnectionRequest(DatagramChannel handle)
/*     */   {
/* 171 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   protected void destroy() throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   protected boolean finishConnect(DatagramChannel handle) throws Exception
/*     */   {
/* 180 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   protected void register(DatagramChannel handle, AbstractPollingIoConnector<NioSession, DatagramChannel>.ConnectionRequest request)
/*     */     throws Exception
/*     */   {
/* 186 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   protected int select(int timeout) throws Exception
/*     */   {
/* 191 */     return 0;
/*     */   }
/*     */ 
/*     */   protected Iterator<DatagramChannel> selectedHandles()
/*     */   {
/* 197 */     return Collections.EMPTY_LIST.iterator();
/*     */   }
/*     */ 
/*     */   protected void wakeup()
/*     */   {
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.transport.socket.nio.NioDatagramConnector
 * JD-Core Version:    0.6.0
 */