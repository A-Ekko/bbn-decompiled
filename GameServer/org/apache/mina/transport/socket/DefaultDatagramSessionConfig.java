/*     */ package org.apache.mina.transport.socket;
/*     */ 
/*     */ public class DefaultDatagramSessionConfig extends AbstractDatagramSessionConfig
/*     */ {
/*  31 */   private static boolean DEFAULT_BROADCAST = false;
/*  32 */   private static boolean DEFAULT_REUSE_ADDRESS = false;
/*  33 */   private static int DEFAULT_RECEIVE_BUFFER_SIZE = 1024;
/*  34 */   private static int DEFAULT_SEND_BUFFER_SIZE = 1024;
/*  35 */   private static int DEFAULT_TRAFFIC_CLASS = 0;
/*     */ 
/*  37 */   private boolean broadcast = DEFAULT_BROADCAST;
/*  38 */   private boolean reuseAddress = DEFAULT_REUSE_ADDRESS;
/*  39 */   private int receiveBufferSize = DEFAULT_RECEIVE_BUFFER_SIZE;
/*  40 */   private int sendBufferSize = DEFAULT_SEND_BUFFER_SIZE;
/*  41 */   private int trafficClass = DEFAULT_TRAFFIC_CLASS;
/*     */ 
/*     */   public boolean isBroadcast()
/*     */   {
/*  53 */     return this.broadcast;
/*     */   }
/*     */ 
/*     */   public void setBroadcast(boolean broadcast)
/*     */   {
/*  60 */     this.broadcast = broadcast;
/*     */   }
/*     */ 
/*     */   public boolean isReuseAddress()
/*     */   {
/*  67 */     return this.reuseAddress;
/*     */   }
/*     */ 
/*     */   public void setReuseAddress(boolean reuseAddress)
/*     */   {
/*  74 */     this.reuseAddress = reuseAddress;
/*     */   }
/*     */ 
/*     */   public int getReceiveBufferSize()
/*     */   {
/*  81 */     return this.receiveBufferSize;
/*     */   }
/*     */ 
/*     */   public void setReceiveBufferSize(int receiveBufferSize)
/*     */   {
/*  88 */     this.receiveBufferSize = receiveBufferSize;
/*     */   }
/*     */ 
/*     */   public int getSendBufferSize()
/*     */   {
/*  95 */     return this.sendBufferSize;
/*     */   }
/*     */ 
/*     */   public void setSendBufferSize(int sendBufferSize)
/*     */   {
/* 102 */     this.sendBufferSize = sendBufferSize;
/*     */   }
/*     */ 
/*     */   public int getTrafficClass()
/*     */   {
/* 109 */     return this.trafficClass;
/*     */   }
/*     */ 
/*     */   public void setTrafficClass(int trafficClass)
/*     */   {
/* 116 */     this.trafficClass = trafficClass;
/*     */   }
/*     */ 
/*     */   protected boolean isBroadcastChanged()
/*     */   {
/* 121 */     return this.broadcast != DEFAULT_BROADCAST;
/*     */   }
/*     */ 
/*     */   protected boolean isReceiveBufferSizeChanged()
/*     */   {
/* 126 */     return this.receiveBufferSize != DEFAULT_RECEIVE_BUFFER_SIZE;
/*     */   }
/*     */ 
/*     */   protected boolean isReuseAddressChanged()
/*     */   {
/* 131 */     return this.reuseAddress != DEFAULT_REUSE_ADDRESS;
/*     */   }
/*     */ 
/*     */   protected boolean isSendBufferSizeChanged()
/*     */   {
/* 136 */     return this.sendBufferSize != DEFAULT_SEND_BUFFER_SIZE;
/*     */   }
/*     */ 
/*     */   protected boolean isTrafficClassChanged()
/*     */   {
/* 141 */     return this.trafficClass != DEFAULT_TRAFFIC_CLASS;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.transport.socket.DefaultDatagramSessionConfig
 * JD-Core Version:    0.6.0
 */