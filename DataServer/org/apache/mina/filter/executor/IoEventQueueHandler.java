/*    */ package org.apache.mina.filter.executor;
/*    */ 
/*    */ import java.util.EventListener;
/*    */ import org.apache.mina.core.session.IoEvent;
/*    */ 
/*    */ public abstract interface IoEventQueueHandler extends EventListener
/*    */ {
/* 38 */   public static final IoEventQueueHandler NOOP = new IoEventQueueHandler() {
/*    */     public boolean accept(Object source, IoEvent event) {
/* 40 */       return true;
/*    */     }
/*    */ 
/*    */     public void offered(Object source, IoEvent event)
/*    */     {
/*    */     }
/*    */ 
/*    */     public void polled(Object source, IoEvent event)
/*    */     {
/*    */     }
/* 38 */   };
/*    */ 
/*    */   public abstract boolean accept(Object paramObject, IoEvent paramIoEvent);
/*    */ 
/*    */   public abstract void offered(Object paramObject, IoEvent paramIoEvent);
/*    */ 
/*    */   public abstract void polled(Object paramObject, IoEvent paramIoEvent);
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.executor.IoEventQueueHandler
 * JD-Core Version:    0.6.0
 */