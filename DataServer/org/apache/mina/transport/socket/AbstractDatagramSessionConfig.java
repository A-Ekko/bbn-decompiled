/*     */ package org.apache.mina.transport.socket;
/*     */ 
/*     */ import org.apache.mina.core.session.AbstractIoSessionConfig;
/*     */ import org.apache.mina.core.session.IoSessionConfig;
/*     */ 
/*     */ public abstract class AbstractDatagramSessionConfig extends AbstractIoSessionConfig
/*     */   implements DatagramSessionConfig
/*     */ {
/*     */   protected void doSetAll(IoSessionConfig config)
/*     */   {
/*  39 */     if (!(config instanceof DatagramSessionConfig)) {
/*  40 */       return;
/*     */     }
/*     */ 
/*  43 */     if ((config instanceof AbstractDatagramSessionConfig))
/*     */     {
/*  45 */       AbstractDatagramSessionConfig cfg = (AbstractDatagramSessionConfig)config;
/*  46 */       if (cfg.isBroadcastChanged()) {
/*  47 */         setBroadcast(cfg.isBroadcast());
/*     */       }
/*  49 */       if (cfg.isReceiveBufferSizeChanged()) {
/*  50 */         setReceiveBufferSize(cfg.getReceiveBufferSize());
/*     */       }
/*  52 */       if (cfg.isReuseAddressChanged()) {
/*  53 */         setReuseAddress(cfg.isReuseAddress());
/*     */       }
/*  55 */       if (cfg.isSendBufferSizeChanged()) {
/*  56 */         setSendBufferSize(cfg.getSendBufferSize());
/*     */       }
/*  58 */       if ((cfg.isTrafficClassChanged()) && (getTrafficClass() != cfg.getTrafficClass()))
/*  59 */         setTrafficClass(cfg.getTrafficClass());
/*     */     }
/*     */     else {
/*  62 */       DatagramSessionConfig cfg = (DatagramSessionConfig)config;
/*  63 */       setBroadcast(cfg.isBroadcast());
/*  64 */       setReceiveBufferSize(cfg.getReceiveBufferSize());
/*  65 */       setReuseAddress(cfg.isReuseAddress());
/*  66 */       setSendBufferSize(cfg.getSendBufferSize());
/*  67 */       if (getTrafficClass() != cfg.getTrafficClass())
/*  68 */         setTrafficClass(cfg.getTrafficClass());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean isBroadcastChanged()
/*     */   {
/*  81 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean isReceiveBufferSizeChanged()
/*     */   {
/*  92 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean isReuseAddressChanged()
/*     */   {
/* 103 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean isSendBufferSizeChanged()
/*     */   {
/* 114 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean isTrafficClassChanged()
/*     */   {
/* 125 */     return true;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.transport.socket.AbstractDatagramSessionConfig
 * JD-Core Version:    0.6.0
 */