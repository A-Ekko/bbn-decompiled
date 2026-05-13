/*    */ package org.apache.mina.filter.keepalive;
/*    */ 
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public abstract interface KeepAliveRequestTimeoutHandler
/*    */ {
/* 37 */   public static final KeepAliveRequestTimeoutHandler NOOP = new KeepAliveRequestTimeoutHandler() {
/*    */     public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {  } } ;
/*    */ 
/* 47 */   public static final KeepAliveRequestTimeoutHandler LOG = new KeepAliveRequestTimeoutHandler() {
/* 48 */     private final Logger log = LoggerFactory.getLogger(KeepAliveFilter.class);
/*    */ 
/*    */     public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session)
/*    */       throws Exception
/*    */     {
/* 53 */       this.log.warn("A keep-alive response message was not received within {} second(s).", Integer.valueOf(filter.getRequestTimeout()));
/*    */     }
/* 47 */   };
/*    */ 
/* 61 */   public static final KeepAliveRequestTimeoutHandler EXCEPTION = new KeepAliveRequestTimeoutHandler()
/*    */   {
/*    */     public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
/* 64 */       throw new KeepAliveRequestTimeoutException("A keep-alive response message was not received within " + filter.getRequestTimeout() + " second(s).");
/*    */     }
/* 61 */   };
/*    */ 
/* 73 */   public static final KeepAliveRequestTimeoutHandler CLOSE = new KeepAliveRequestTimeoutHandler() {
/* 74 */     private final Logger log = LoggerFactory.getLogger(KeepAliveFilter.class);
/*    */ 
/*    */     public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session)
/*    */       throws Exception
/*    */     {
/* 79 */       this.log.warn("Closing the session because a keep-alive response message was not received within {} second(s).", Integer.valueOf(filter.getRequestTimeout()));
/*    */ 
/* 82 */       session.close(true);
/*    */     }
/* 73 */   };
/*    */ 
/* 89 */   public static final KeepAliveRequestTimeoutHandler DEAF_SPEAKER = new KeepAliveRequestTimeoutHandler()
/*    */   {
/*    */     public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
/* 92 */       throw new Error("Shouldn't be invoked.  Please file a bug report.");
/*    */     }
/* 89 */   };
/*    */ 
/*    */   public abstract void keepAliveRequestTimedOut(KeepAliveFilter paramKeepAliveFilter, IoSession paramIoSession)
/*    */     throws Exception;
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler
 * JD-Core Version:    0.6.0
 */