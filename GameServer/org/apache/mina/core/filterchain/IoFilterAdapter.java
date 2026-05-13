/*     */ package org.apache.mina.core.filterchain;
/*     */ 
/*     */ import org.apache.mina.core.session.IdleStatus;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ 
/*     */ public class IoFilterAdapter
/*     */   implements IoFilter
/*     */ {
/*     */   public void init()
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   public void destroy()
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onPreAdd(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter)
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onPostAdd(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter)
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onPreRemove(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter)
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onPostRemove(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter)
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/*  80 */     nextFilter.sessionCreated(session);
/*     */   }
/*     */ 
/*     */   public void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/*  88 */     nextFilter.sessionOpened(session);
/*     */   }
/*     */ 
/*     */   public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/*  96 */     nextFilter.sessionClosed(session);
/*     */   }
/*     */ 
/*     */   public void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status)
/*     */     throws Exception
/*     */   {
/* 104 */     nextFilter.sessionIdle(session, status);
/*     */   }
/*     */ 
/*     */   public void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause)
/*     */     throws Exception
/*     */   {
/* 112 */     nextFilter.exceptionCaught(session, cause);
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message)
/*     */     throws Exception
/*     */   {
/* 120 */     nextFilter.messageReceived(session, message);
/*     */   }
/*     */ 
/*     */   public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */     throws Exception
/*     */   {
/* 128 */     nextFilter.messageSent(session, writeRequest);
/*     */   }
/*     */ 
/*     */   public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */     throws Exception
/*     */   {
/* 136 */     nextFilter.filterWrite(session, writeRequest);
/*     */   }
/*     */ 
/*     */   public void filterClose(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/* 144 */     nextFilter.filterClose(session);
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 148 */     return getClass().getSimpleName();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.filterchain.IoFilterAdapter
 * JD-Core Version:    0.6.0
 */