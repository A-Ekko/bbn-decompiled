/*    */ package org.apache.mina.core.filterchain;
/*    */ 
/*    */ import org.apache.mina.core.session.IdleStatus;
/*    */ import org.apache.mina.core.session.IoEvent;
/*    */ import org.apache.mina.core.session.IoEventType;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ import org.apache.mina.core.write.WriteRequest;
/*    */ 
/*    */ public class IoFilterEvent extends IoEvent
/*    */ {
/*    */   private final IoFilter.NextFilter nextFilter;
/*    */ 
/*    */   public IoFilterEvent(IoFilter.NextFilter nextFilter, IoEventType type, IoSession session, Object parameter)
/*    */   {
/* 43 */     super(type, session, parameter);
/*    */ 
/* 45 */     if (nextFilter == null) {
/* 46 */       throw new NullPointerException("nextFilter");
/*    */     }
/* 48 */     this.nextFilter = nextFilter;
/*    */   }
/*    */ 
/*    */   public IoFilter.NextFilter getNextFilter() {
/* 52 */     return this.nextFilter;
/*    */   }
/*    */ 
/*    */   public void fire()
/*    */   {
/* 57 */     switch (1.$SwitchMap$org$apache$mina$core$session$IoEventType[getType().ordinal()]) {
/*    */     case 1:
/* 59 */       getNextFilter().messageReceived(getSession(), getParameter());
/* 60 */       break;
/*    */     case 2:
/* 62 */       getNextFilter().messageSent(getSession(), (WriteRequest)getParameter());
/* 63 */       break;
/*    */     case 3:
/* 65 */       getNextFilter().filterWrite(getSession(), (WriteRequest)getParameter());
/* 66 */       break;
/*    */     case 4:
/* 68 */       getNextFilter().filterClose(getSession());
/* 69 */       break;
/*    */     case 5:
/* 71 */       getNextFilter().exceptionCaught(getSession(), (Throwable)getParameter());
/* 72 */       break;
/*    */     case 6:
/* 74 */       getNextFilter().sessionIdle(getSession(), (IdleStatus)getParameter());
/* 75 */       break;
/*    */     case 7:
/* 77 */       getNextFilter().sessionOpened(getSession());
/* 78 */       break;
/*    */     case 8:
/* 80 */       getNextFilter().sessionCreated(getSession());
/* 81 */       break;
/*    */     case 9:
/* 83 */       getNextFilter().sessionClosed(getSession());
/* 84 */       break;
/*    */     default:
/* 86 */       throw new IllegalArgumentException("Unknown event type: " + getType());
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.filterchain.IoFilterEvent
 * JD-Core Version:    0.6.0
 */