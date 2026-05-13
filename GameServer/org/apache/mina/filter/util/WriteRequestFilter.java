/*    */ package org.apache.mina.filter.util;
/*    */ 
/*    */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*    */ import org.apache.mina.core.filterchain.IoFilterAdapter;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ import org.apache.mina.core.write.WriteRequest;
/*    */ import org.apache.mina.core.write.WriteRequestWrapper;
/*    */ 
/*    */ public abstract class WriteRequestFilter extends IoFilterAdapter
/*    */ {
/*    */   public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*    */     throws Exception
/*    */   {
/* 41 */     Object filteredMessage = doFilterWrite(nextFilter, session, writeRequest);
/* 42 */     if ((filteredMessage != null) && (filteredMessage != writeRequest.getMessage())) {
/* 43 */       nextFilter.filterWrite(session, new FilteredWriteRequest(filteredMessage, writeRequest));
/*    */     }
/*    */     else
/*    */     {
/* 47 */       nextFilter.filterWrite(session, writeRequest);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*    */     throws Exception
/*    */   {
/* 54 */     if ((writeRequest instanceof FilteredWriteRequest)) {
/* 55 */       FilteredWriteRequest req = (FilteredWriteRequest)writeRequest;
/* 56 */       if (req.getParent() == this) {
/* 57 */         nextFilter.messageSent(session, req.getParentRequest());
/* 58 */         return;
/*    */       }
/*    */     }
/*    */ 
/* 62 */     nextFilter.messageSent(session, writeRequest);
/*    */   }
/*    */   protected abstract Object doFilterWrite(IoFilter.NextFilter paramNextFilter, IoSession paramIoSession, WriteRequest paramWriteRequest) throws Exception;
/*    */ 
/*    */   private class FilteredWriteRequest extends WriteRequestWrapper {
/*    */     private final Object filteredMessage;
/*    */ 
/*    */     public FilteredWriteRequest(Object filteredMessage, WriteRequest writeRequest) {
/* 72 */       super();
/*    */ 
/* 74 */       if (filteredMessage == null) {
/* 75 */         throw new NullPointerException("filteredMessage");
/*    */       }
/* 77 */       this.filteredMessage = filteredMessage;
/*    */     }
/*    */ 
/*    */     public WriteRequestFilter getParent() {
/* 81 */       return WriteRequestFilter.this;
/*    */     }
/*    */ 
/*    */     public Object getMessage()
/*    */     {
/* 86 */       return this.filteredMessage;
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.util.WriteRequestFilter
 * JD-Core Version:    0.6.0
 */