/*     */ package org.apache.mina.proxy.event;
/*     */ 
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.session.IdleStatus;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class IoSessionEvent
/*     */ {
/*  36 */   private static final Logger logger = LoggerFactory.getLogger(IoSessionEvent.class);
/*     */   private final IoFilter.NextFilter nextFilter;
/*     */   private final IoSession session;
/*     */   private final IoSessionEventType type;
/*     */   private IdleStatus status;
/*     */ 
/*     */   public IoSessionEvent(IoFilter.NextFilter nextFilter, IoSession session, IoSessionEventType type)
/*     */   {
/*  70 */     this.nextFilter = nextFilter;
/*  71 */     this.session = session;
/*  72 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public IoSessionEvent(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status)
/*     */   {
/*  85 */     this(nextFilter, session, IoSessionEventType.IDLE);
/*  86 */     this.status = status;
/*     */   }
/*     */ 
/*     */   public void deliverEvent()
/*     */   {
/*  93 */     logger.debug("Delivering event {}", this);
/*  94 */     deliverEvent(this.nextFilter, this.session, this.type, this.status);
/*     */   }
/*     */ 
/*     */   private static void deliverEvent(IoFilter.NextFilter nextFilter, IoSession session, IoSessionEventType type, IdleStatus status)
/*     */   {
/* 110 */     switch (1.$SwitchMap$org$apache$mina$proxy$event$IoSessionEventType[type.ordinal()]) {
/*     */     case 1:
/* 112 */       nextFilter.sessionCreated(session);
/* 113 */       break;
/*     */     case 2:
/* 115 */       nextFilter.sessionOpened(session);
/* 116 */       break;
/*     */     case 3:
/* 118 */       nextFilter.sessionIdle(session, status);
/* 119 */       break;
/*     */     case 4:
/* 121 */       nextFilter.sessionClosed(session);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 131 */     StringBuilder sb = new StringBuilder(IoSessionEvent.class.getSimpleName());
/*     */ 
/* 133 */     sb.append('@');
/* 134 */     sb.append(Integer.toHexString(hashCode()));
/* 135 */     sb.append(" - [ ").append(this.session);
/* 136 */     sb.append(", ").append(this.type);
/* 137 */     sb.append(']');
/* 138 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public IdleStatus getStatus()
/*     */   {
/* 147 */     return this.status;
/*     */   }
/*     */ 
/*     */   public IoFilter.NextFilter getNextFilter()
/*     */   {
/* 156 */     return this.nextFilter;
/*     */   }
/*     */ 
/*     */   public IoSession getSession()
/*     */   {
/* 165 */     return this.session;
/*     */   }
/*     */ 
/*     */   public IoSessionEventType getType()
/*     */   {
/* 174 */     return this.type;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.event.IoSessionEvent
 * JD-Core Version:    0.6.0
 */