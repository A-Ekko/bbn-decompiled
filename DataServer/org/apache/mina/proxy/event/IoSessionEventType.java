/*    */ package org.apache.mina.proxy.event;
/*    */ 
/*    */ public enum IoSessionEventType
/*    */ {
/* 30 */   CREATED(1), OPENED(2), IDLE(3), CLOSED(4);
/*    */ 
/*    */   private final int id;
/*    */ 
/*    */   private IoSessionEventType(int id)
/*    */   {
/* 38 */     this.id = id;
/*    */   }
/*    */ 
/*    */   public int getId()
/*    */   {
/* 47 */     return this.id;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 55 */     switch (1.$SwitchMap$org$apache$mina$proxy$event$IoSessionEventType[ordinal()]) {
/*    */     case 1:
/* 57 */       return "- CREATED event -";
/*    */     case 2:
/* 59 */       return "- OPENED event -";
/*    */     case 3:
/* 61 */       return "- IDLE event -";
/*    */     case 4:
/* 63 */       return "- CLOSED event -";
/*    */     }
/* 65 */     return "- Event Id=" + this.id + " -";
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.event.IoSessionEventType
 * JD-Core Version:    0.6.0
 */