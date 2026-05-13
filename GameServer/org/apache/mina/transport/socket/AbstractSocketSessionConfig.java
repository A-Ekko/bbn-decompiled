/*     */ package org.apache.mina.transport.socket;
/*     */ 
/*     */ import org.apache.mina.core.session.AbstractIoSessionConfig;
/*     */ import org.apache.mina.core.session.IoSessionConfig;
/*     */ 
/*     */ public abstract class AbstractSocketSessionConfig extends AbstractIoSessionConfig
/*     */   implements SocketSessionConfig
/*     */ {
/*     */   protected final void doSetAll(IoSessionConfig config)
/*     */   {
/*  39 */     if (!(config instanceof SocketSessionConfig)) {
/*  40 */       return;
/*     */     }
/*     */ 
/*  43 */     if ((config instanceof AbstractSocketSessionConfig))
/*     */     {
/*  45 */       AbstractSocketSessionConfig cfg = (AbstractSocketSessionConfig)config;
/*  46 */       if (cfg.isKeepAliveChanged()) {
/*  47 */         setKeepAlive(cfg.isKeepAlive());
/*     */       }
/*  49 */       if (cfg.isOobInlineChanged()) {
/*  50 */         setOobInline(cfg.isOobInline());
/*     */       }
/*  52 */       if (cfg.isReceiveBufferSizeChanged()) {
/*  53 */         setReceiveBufferSize(cfg.getReceiveBufferSize());
/*     */       }
/*  55 */       if (cfg.isReuseAddressChanged()) {
/*  56 */         setReuseAddress(cfg.isReuseAddress());
/*     */       }
/*  58 */       if (cfg.isSendBufferSizeChanged()) {
/*  59 */         setSendBufferSize(cfg.getSendBufferSize());
/*     */       }
/*  61 */       if (cfg.isSoLingerChanged()) {
/*  62 */         setSoLinger(cfg.getSoLinger());
/*     */       }
/*  64 */       if (cfg.isTcpNoDelayChanged()) {
/*  65 */         setTcpNoDelay(cfg.isTcpNoDelay());
/*     */       }
/*  67 */       if ((cfg.isTrafficClassChanged()) && (getTrafficClass() != cfg.getTrafficClass()))
/*  68 */         setTrafficClass(cfg.getTrafficClass());
/*     */     }
/*     */     else {
/*  71 */       SocketSessionConfig cfg = (SocketSessionConfig)config;
/*  72 */       setKeepAlive(cfg.isKeepAlive());
/*  73 */       setOobInline(cfg.isOobInline());
/*  74 */       setReceiveBufferSize(cfg.getReceiveBufferSize());
/*  75 */       setReuseAddress(cfg.isReuseAddress());
/*  76 */       setSendBufferSize(cfg.getSendBufferSize());
/*  77 */       setSoLinger(cfg.getSoLinger());
/*  78 */       setTcpNoDelay(cfg.isTcpNoDelay());
/*  79 */       if (getTrafficClass() != cfg.getTrafficClass())
/*  80 */         setTrafficClass(cfg.getTrafficClass());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean isKeepAliveChanged()
/*     */   {
/*  93 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean isOobInlineChanged()
/*     */   {
/* 104 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean isReceiveBufferSizeChanged()
/*     */   {
/* 115 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean isReuseAddressChanged()
/*     */   {
/* 126 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean isSendBufferSizeChanged()
/*     */   {
/* 137 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean isSoLingerChanged()
/*     */   {
/* 148 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean isTcpNoDelayChanged()
/*     */   {
/* 159 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean isTrafficClassChanged()
/*     */   {
/* 170 */     return true;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.transport.socket.AbstractSocketSessionConfig
 * JD-Core Version:    0.6.0
 */