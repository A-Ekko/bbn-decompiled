/*     */ package org.apache.mina.core.session;
/*     */ 
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ 
/*     */ public class IoEvent
/*     */   implements Runnable
/*     */ {
/*     */   private final IoEventType type;
/*     */   private final IoSession session;
/*     */   private final Object parameter;
/*     */ 
/*     */   public IoEvent(IoEventType type, IoSession session, Object parameter)
/*     */   {
/*  40 */     if (type == null) {
/*  41 */       throw new NullPointerException("type");
/*     */     }
/*  43 */     if (session == null) {
/*  44 */       throw new NullPointerException("session");
/*     */     }
/*  46 */     this.type = type;
/*  47 */     this.session = session;
/*  48 */     this.parameter = parameter;
/*     */   }
/*     */ 
/*     */   public IoEventType getType() {
/*  52 */     return this.type;
/*     */   }
/*     */ 
/*     */   public IoSession getSession() {
/*  56 */     return this.session;
/*     */   }
/*     */ 
/*     */   public Object getParameter() {
/*  60 */     return this.parameter;
/*     */   }
/*     */ 
/*     */   public void run() {
/*  64 */     fire();
/*     */   }
/*     */ 
/*     */   public void fire() {
/*  68 */     switch (1.$SwitchMap$org$apache$mina$core$session$IoEventType[getType().ordinal()]) {
/*     */     case 1:
/*  70 */       getSession().getFilterChain().fireMessageReceived(getParameter());
/*  71 */       break;
/*     */     case 2:
/*  73 */       getSession().getFilterChain().fireMessageSent((WriteRequest)getParameter());
/*  74 */       break;
/*     */     case 3:
/*  76 */       getSession().getFilterChain().fireFilterWrite((WriteRequest)getParameter());
/*  77 */       break;
/*     */     case 4:
/*  79 */       getSession().getFilterChain().fireFilterClose();
/*  80 */       break;
/*     */     case 5:
/*  82 */       getSession().getFilterChain().fireExceptionCaught((Throwable)getParameter());
/*  83 */       break;
/*     */     case 6:
/*  85 */       getSession().getFilterChain().fireSessionIdle((IdleStatus)getParameter());
/*  86 */       break;
/*     */     case 7:
/*  88 */       getSession().getFilterChain().fireSessionOpened();
/*  89 */       break;
/*     */     case 8:
/*  91 */       getSession().getFilterChain().fireSessionCreated();
/*  92 */       break;
/*     */     case 9:
/*  94 */       getSession().getFilterChain().fireSessionClosed();
/*  95 */       break;
/*     */     default:
/*  97 */       throw new IllegalArgumentException("Unknown event type: " + getType());
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 103 */     if (getParameter() == null) {
/* 104 */       return "[" + getSession() + "] " + getType().name();
/*     */     }
/* 106 */     return "[" + getSession() + "] " + getType().name() + ": " + getParameter();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.session.IoEvent
 * JD-Core Version:    0.6.0
 */