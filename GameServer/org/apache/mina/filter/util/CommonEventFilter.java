/*    */ package org.apache.mina.filter.util;
/*    */ 
/*    */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*    */ import org.apache.mina.core.filterchain.IoFilterAdapter;
/*    */ import org.apache.mina.core.filterchain.IoFilterEvent;
/*    */ import org.apache.mina.core.session.IdleStatus;
/*    */ import org.apache.mina.core.session.IoEventType;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ import org.apache.mina.core.write.WriteRequest;
/*    */ 
/*    */ public abstract class CommonEventFilter extends IoFilterAdapter
/*    */ {
/*    */   protected abstract void filter(IoFilterEvent paramIoFilterEvent)
/*    */     throws Exception;
/*    */ 
/*    */   public final void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session)
/*    */     throws Exception
/*    */   {
/* 45 */     filter(new IoFilterEvent(nextFilter, IoEventType.SESSION_CREATED, session, null));
/*    */   }
/*    */ 
/*    */   public final void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session) throws Exception
/*    */   {
/* 50 */     filter(new IoFilterEvent(nextFilter, IoEventType.SESSION_OPENED, session, null));
/*    */   }
/*    */ 
/*    */   public final void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception
/*    */   {
/* 55 */     filter(new IoFilterEvent(nextFilter, IoEventType.SESSION_CLOSED, session, null));
/*    */   }
/*    */ 
/*    */   public final void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception
/*    */   {
/* 60 */     filter(new IoFilterEvent(nextFilter, IoEventType.SESSION_IDLE, session, status));
/*    */   }
/*    */ 
/*    */   public final void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause) throws Exception
/*    */   {
/* 65 */     filter(new IoFilterEvent(nextFilter, IoEventType.EXCEPTION_CAUGHT, session, cause));
/*    */   }
/*    */ 
/*    */   public final void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message) throws Exception
/*    */   {
/* 70 */     filter(new IoFilterEvent(nextFilter, IoEventType.MESSAGE_RECEIVED, session, message));
/*    */   }
/*    */ 
/*    */   public final void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception
/*    */   {
/* 75 */     filter(new IoFilterEvent(nextFilter, IoEventType.MESSAGE_SENT, session, writeRequest));
/*    */   }
/*    */ 
/*    */   public final void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception
/*    */   {
/* 80 */     filter(new IoFilterEvent(nextFilter, IoEventType.WRITE, session, writeRequest));
/*    */   }
/*    */ 
/*    */   public final void filterClose(IoFilter.NextFilter nextFilter, IoSession session) throws Exception
/*    */   {
/* 85 */     filter(new IoFilterEvent(nextFilter, IoEventType.CLOSE, session, null));
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.util.CommonEventFilter
 * JD-Core Version:    0.6.0
 */