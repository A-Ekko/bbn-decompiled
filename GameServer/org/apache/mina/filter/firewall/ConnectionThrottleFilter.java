/*     */ package org.apache.mina.filter.firewall;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilterAdapter;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ConnectionThrottleFilter extends IoFilterAdapter
/*     */ {
/*     */   private static final long DEFAULT_TIME = 1000L;
/*     */   private long allowedInterval;
/*     */   private final Map<String, Long> clients;
/*  48 */   private final Logger logger = LoggerFactory.getLogger(getClass());
/*     */ 
/*     */   public ConnectionThrottleFilter()
/*     */   {
/*  53 */     this(1000L);
/*     */   }
/*     */ 
/*     */   public ConnectionThrottleFilter(long allowedInterval)
/*     */   {
/*  65 */     this.allowedInterval = allowedInterval;
/*  66 */     this.clients = Collections.synchronizedMap(new HashMap());
/*     */   }
/*     */ 
/*     */   public void setAllowedInterval(long allowedInterval)
/*     */   {
/*  78 */     this.allowedInterval = allowedInterval;
/*     */   }
/*     */ 
/*     */   protected boolean isConnectionOk(IoSession session)
/*     */   {
/*  91 */     SocketAddress remoteAddress = session.getRemoteAddress();
/*  92 */     if ((remoteAddress instanceof InetSocketAddress)) {
/*  93 */       InetSocketAddress addr = (InetSocketAddress)remoteAddress;
/*  94 */       long now = System.currentTimeMillis();
/*     */ 
/*  96 */       if (this.clients.containsKey(addr.getAddress().getHostAddress()))
/*     */       {
/*  98 */         this.logger.debug("This is not a new client");
/*  99 */         Long lastConnTime = (Long)this.clients.get(addr.getAddress().getHostAddress());
/*     */ 
/* 102 */         this.clients.put(addr.getAddress().getHostAddress(), Long.valueOf(now));
/*     */ 
/* 106 */         if (now - lastConnTime.longValue() < this.allowedInterval) {
/* 107 */           this.logger.warn("Session connection interval too short");
/* 108 */           return false;
/*     */         }
/* 110 */         return true;
/*     */       }
/*     */ 
/* 113 */       this.clients.put(addr.getAddress().getHostAddress(), Long.valueOf(now));
/* 114 */       return true;
/*     */     }
/*     */ 
/* 118 */     return false;
/*     */   }
/*     */ 
/*     */   public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/* 124 */     if (!isConnectionOk(session)) {
/* 125 */       this.logger.warn("Connections coming in too fast; closing.");
/* 126 */       session.close(true);
/*     */     }
/* 128 */     nextFilter.sessionCreated(session);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.firewall.ConnectionThrottleFilter
 * JD-Core Version:    0.6.0
 */