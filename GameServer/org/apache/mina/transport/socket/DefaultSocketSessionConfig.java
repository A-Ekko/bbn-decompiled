/*     */ package org.apache.mina.transport.socket;
/*     */ 
/*     */ import org.apache.mina.core.service.IoService;
/*     */ 
/*     */ public class DefaultSocketSessionConfig extends AbstractSocketSessionConfig
/*     */ {
/*  31 */   private static boolean DEFAULT_REUSE_ADDRESS = false;
/*  32 */   private static int DEFAULT_RECEIVE_BUFFER_SIZE = 1024;
/*  33 */   private static int DEFAULT_SEND_BUFFER_SIZE = 1024;
/*  34 */   private static int DEFAULT_TRAFFIC_CLASS = 0;
/*  35 */   private static boolean DEFAULT_KEEP_ALIVE = false;
/*  36 */   private static boolean DEFAULT_OOB_INLINE = false;
/*  37 */   private static int DEFAULT_SO_LINGER = -1;
/*  38 */   private static boolean DEFAULT_TCP_NO_DELAY = false;
/*     */   private IoService parent;
/*     */   private boolean defaultReuseAddress;
/*  42 */   private int defaultReceiveBufferSize = DEFAULT_RECEIVE_BUFFER_SIZE;
/*     */   private boolean reuseAddress;
/*  45 */   private int receiveBufferSize = this.defaultReceiveBufferSize;
/*  46 */   private int sendBufferSize = DEFAULT_SEND_BUFFER_SIZE;
/*  47 */   private int trafficClass = DEFAULT_TRAFFIC_CLASS;
/*  48 */   private boolean keepAlive = DEFAULT_KEEP_ALIVE;
/*  49 */   private boolean oobInline = DEFAULT_OOB_INLINE;
/*  50 */   private int soLinger = DEFAULT_SO_LINGER;
/*  51 */   private boolean tcpNoDelay = DEFAULT_TCP_NO_DELAY;
/*     */ 
/*     */   public void init(IoService parent)
/*     */   {
/*  60 */     this.parent = parent;
/*  61 */     if ((parent instanceof SocketAcceptor))
/*  62 */       this.defaultReuseAddress = true;
/*     */     else {
/*  64 */       this.defaultReuseAddress = DEFAULT_REUSE_ADDRESS;
/*     */     }
/*  66 */     this.reuseAddress = this.defaultReuseAddress;
/*     */   }
/*     */ 
/*     */   public boolean isReuseAddress() {
/*  70 */     return this.reuseAddress;
/*     */   }
/*     */ 
/*     */   public void setReuseAddress(boolean reuseAddress) {
/*  74 */     this.reuseAddress = reuseAddress;
/*     */   }
/*     */ 
/*     */   public int getReceiveBufferSize() {
/*  78 */     return this.receiveBufferSize;
/*     */   }
/*     */ 
/*     */   public void setReceiveBufferSize(int receiveBufferSize) {
/*  82 */     this.receiveBufferSize = receiveBufferSize;
/*     */ 
/*  92 */     if ((!this.parent.isActive()) && ((this.parent instanceof SocketAcceptor)))
/*  93 */       this.defaultReceiveBufferSize = receiveBufferSize;
/*     */   }
/*     */ 
/*     */   public int getSendBufferSize()
/*     */   {
/*  98 */     return this.sendBufferSize;
/*     */   }
/*     */ 
/*     */   public void setSendBufferSize(int sendBufferSize) {
/* 102 */     this.sendBufferSize = sendBufferSize;
/*     */   }
/*     */ 
/*     */   public int getTrafficClass() {
/* 106 */     return this.trafficClass;
/*     */   }
/*     */ 
/*     */   public void setTrafficClass(int trafficClass) {
/* 110 */     this.trafficClass = trafficClass;
/*     */   }
/*     */ 
/*     */   public boolean isKeepAlive() {
/* 114 */     return this.keepAlive;
/*     */   }
/*     */ 
/*     */   public void setKeepAlive(boolean keepAlive) {
/* 118 */     this.keepAlive = keepAlive;
/*     */   }
/*     */ 
/*     */   public boolean isOobInline() {
/* 122 */     return this.oobInline;
/*     */   }
/*     */ 
/*     */   public void setOobInline(boolean oobInline) {
/* 126 */     this.oobInline = oobInline;
/*     */   }
/*     */ 
/*     */   public int getSoLinger() {
/* 130 */     return this.soLinger;
/*     */   }
/*     */ 
/*     */   public void setSoLinger(int soLinger) {
/* 134 */     this.soLinger = soLinger;
/*     */   }
/*     */ 
/*     */   public boolean isTcpNoDelay() {
/* 138 */     return this.tcpNoDelay;
/*     */   }
/*     */ 
/*     */   public void setTcpNoDelay(boolean tcpNoDelay) {
/* 142 */     this.tcpNoDelay = tcpNoDelay;
/*     */   }
/*     */ 
/*     */   protected boolean isKeepAliveChanged()
/*     */   {
/* 147 */     return this.keepAlive != DEFAULT_KEEP_ALIVE;
/*     */   }
/*     */ 
/*     */   protected boolean isOobInlineChanged()
/*     */   {
/* 152 */     return this.oobInline != DEFAULT_OOB_INLINE;
/*     */   }
/*     */ 
/*     */   protected boolean isReceiveBufferSizeChanged()
/*     */   {
/* 157 */     return this.receiveBufferSize != this.defaultReceiveBufferSize;
/*     */   }
/*     */ 
/*     */   protected boolean isReuseAddressChanged()
/*     */   {
/* 162 */     return this.reuseAddress != this.defaultReuseAddress;
/*     */   }
/*     */ 
/*     */   protected boolean isSendBufferSizeChanged()
/*     */   {
/* 167 */     return this.sendBufferSize != DEFAULT_SEND_BUFFER_SIZE;
/*     */   }
/*     */ 
/*     */   protected boolean isSoLingerChanged()
/*     */   {
/* 172 */     return this.soLinger != DEFAULT_SO_LINGER;
/*     */   }
/*     */ 
/*     */   protected boolean isTcpNoDelayChanged()
/*     */   {
/* 177 */     return this.tcpNoDelay != DEFAULT_TCP_NO_DELAY;
/*     */   }
/*     */ 
/*     */   protected boolean isTrafficClassChanged()
/*     */   {
/* 182 */     return this.trafficClass != DEFAULT_TRAFFIC_CLASS;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.transport.socket.DefaultSocketSessionConfig
 * JD-Core Version:    0.6.0
 */