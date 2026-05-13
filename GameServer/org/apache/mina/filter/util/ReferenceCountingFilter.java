/*     */ package org.apache.mina.filter.util;
/*     */ 
/*     */ import org.apache.mina.core.filterchain.IoFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilterAdapter;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.session.IdleStatus;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ 
/*     */ public class ReferenceCountingFilter extends IoFilterAdapter
/*     */ {
/*     */   private final IoFilter filter;
/*  40 */   private int count = 0;
/*     */ 
/*     */   public ReferenceCountingFilter(IoFilter filter) {
/*  43 */     this.filter = filter;
/*     */   }
/*     */ 
/*     */   public void init() throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   public void destroy() throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   public synchronized void onPreAdd(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter) throws Exception
/*     */   {
/*  56 */     if (0 == this.count) {
/*  57 */       this.filter.init();
/*     */ 
/*  59 */       this.count += 1;
/*     */     }
/*     */ 
/*  62 */     this.filter.onPreAdd(parent, name, nextFilter);
/*     */   }
/*     */ 
/*     */   public synchronized void onPostRemove(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter) throws Exception
/*     */   {
/*  67 */     this.filter.onPostRemove(parent, name, nextFilter);
/*     */ 
/*  69 */     this.count -= 1;
/*     */ 
/*  71 */     if (0 == this.count)
/*  72 */       this.filter.destroy();
/*     */   }
/*     */ 
/*     */   public void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause)
/*     */     throws Exception
/*     */   {
/*  78 */     this.filter.exceptionCaught(nextFilter, session, cause);
/*     */   }
/*     */ 
/*     */   public void filterClose(IoFilter.NextFilter nextFilter, IoSession session) throws Exception
/*     */   {
/*  83 */     this.filter.filterClose(nextFilter, session);
/*     */   }
/*     */ 
/*     */   public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception
/*     */   {
/*  88 */     this.filter.filterWrite(nextFilter, session, writeRequest);
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message) throws Exception
/*     */   {
/*  93 */     this.filter.messageReceived(nextFilter, session, message);
/*     */   }
/*     */ 
/*     */   public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception
/*     */   {
/*  98 */     this.filter.messageSent(nextFilter, session, writeRequest);
/*     */   }
/*     */ 
/*     */   public void onPostAdd(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter) throws Exception
/*     */   {
/* 103 */     this.filter.onPostAdd(parent, name, nextFilter);
/*     */   }
/*     */ 
/*     */   public void onPreRemove(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter) throws Exception
/*     */   {
/* 108 */     this.filter.onPreRemove(parent, name, nextFilter);
/*     */   }
/*     */ 
/*     */   public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception
/*     */   {
/* 113 */     this.filter.sessionClosed(nextFilter, session);
/*     */   }
/*     */ 
/*     */   public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session) throws Exception
/*     */   {
/* 118 */     this.filter.sessionCreated(nextFilter, session);
/*     */   }
/*     */ 
/*     */   public void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception
/*     */   {
/* 123 */     this.filter.sessionIdle(nextFilter, session, status);
/*     */   }
/*     */ 
/*     */   public void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session) throws Exception
/*     */   {
/* 128 */     this.filter.sessionOpened(nextFilter, session);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.util.ReferenceCountingFilter
 * JD-Core Version:    0.6.0
 */