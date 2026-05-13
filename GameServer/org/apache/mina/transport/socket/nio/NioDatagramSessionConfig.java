/*     */ package org.apache.mina.transport.socket.nio;
/*     */ 
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.SocketException;
/*     */ import java.nio.channels.DatagramChannel;
/*     */ import org.apache.mina.core.RuntimeIoException;
/*     */ import org.apache.mina.transport.socket.AbstractDatagramSessionConfig;
/*     */ 
/*     */ class NioDatagramSessionConfig extends AbstractDatagramSessionConfig
/*     */ {
/*     */   private final DatagramChannel channel;
/*     */ 
/*     */   NioDatagramSessionConfig(DatagramChannel channel)
/*     */   {
/*  46 */     this.channel = channel;
/*     */   }
/*     */ 
/*     */   public int getReceiveBufferSize()
/*     */   {
/*     */     try
/*     */     {
/*  60 */       return this.channel.socket().getReceiveBufferSize(); } catch (SocketException e) {
/*     */     }
/*  62 */     throw new RuntimeIoException(e);
/*     */   }
/*     */ 
/*     */   public void setReceiveBufferSize(int receiveBufferSize)
/*     */   {
/*     */     try
/*     */     {
/*  80 */       this.channel.socket().setReceiveBufferSize(receiveBufferSize);
/*     */     } catch (SocketException e) {
/*  82 */       throw new RuntimeIoException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isBroadcast()
/*     */   {
/*     */     try
/*     */     {
/*  95 */       return this.channel.socket().getBroadcast(); } catch (SocketException e) {
/*     */     }
/*  97 */     throw new RuntimeIoException(e);
/*     */   }
/*     */ 
/*     */   public void setBroadcast(boolean broadcast)
/*     */   {
/*     */     try {
/* 103 */       this.channel.socket().setBroadcast(broadcast);
/*     */     } catch (SocketException e) {
/* 105 */       throw new RuntimeIoException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getSendBufferSize()
/*     */   {
/*     */     try
/*     */     {
/* 116 */       return this.channel.socket().getSendBufferSize(); } catch (SocketException e) {
/*     */     }
/* 118 */     throw new RuntimeIoException(e);
/*     */   }
/*     */ 
/*     */   public void setSendBufferSize(int sendBufferSize)
/*     */   {
/*     */     try
/*     */     {
/* 129 */       this.channel.socket().setSendBufferSize(sendBufferSize);
/*     */     } catch (SocketException e) {
/* 131 */       throw new RuntimeIoException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isReuseAddress()
/*     */   {
/*     */     try
/*     */     {
/* 144 */       return this.channel.socket().getReuseAddress(); } catch (SocketException e) {
/*     */     }
/* 146 */     throw new RuntimeIoException(e);
/*     */   }
/*     */ 
/*     */   public void setReuseAddress(boolean reuseAddress)
/*     */   {
/*     */     try
/*     */     {
/* 157 */       this.channel.socket().setReuseAddress(reuseAddress);
/*     */     } catch (SocketException e) {
/* 159 */       throw new RuntimeIoException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getTrafficClass()
/*     */   {
/*     */     try
/*     */     {
/* 174 */       return this.channel.socket().getTrafficClass(); } catch (SocketException e) {
/*     */     }
/* 176 */     throw new RuntimeIoException(e);
/*     */   }
/*     */ 
/*     */   public void setTrafficClass(int trafficClass)
/*     */   {
/*     */     try
/*     */     {
/* 187 */       this.channel.socket().setTrafficClass(trafficClass);
/*     */     } catch (SocketException e) {
/* 189 */       throw new RuntimeIoException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.transport.socket.nio.NioDatagramSessionConfig
 * JD-Core Version:    0.6.0
 */