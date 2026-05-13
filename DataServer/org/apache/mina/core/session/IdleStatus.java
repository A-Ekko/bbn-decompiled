/*    */ package org.apache.mina.core.session;
/*    */ 
/*    */ public class IdleStatus
/*    */ {
/* 43 */   public static final IdleStatus READER_IDLE = new IdleStatus("reader idle");
/*    */ 
/* 48 */   public static final IdleStatus WRITER_IDLE = new IdleStatus("writer idle");
/*    */ 
/* 53 */   public static final IdleStatus BOTH_IDLE = new IdleStatus("both idle");
/*    */   private final String strValue;
/*    */ 
/*    */   private IdleStatus(String strValue)
/*    */   {
/* 61 */     this.strValue = strValue;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 74 */     return this.strValue;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.session.IdleStatus
 * JD-Core Version:    0.6.0
 */