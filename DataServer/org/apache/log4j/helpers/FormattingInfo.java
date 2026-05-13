/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ public class FormattingInfo
/*    */ {
/* 22 */   int min = -1;
/* 23 */   int max = 2147483647;
/* 24 */   boolean leftAlign = false;
/*    */ 
/*    */   void reset() {
/* 27 */     this.min = -1;
/* 28 */     this.max = 2147483647;
/* 29 */     this.leftAlign = false;
/*    */   }
/*    */ 
/*    */   void dump() {
/* 33 */     LogLog.debug("min=" + this.min + ", max=" + this.max + ", leftAlign=" + this.leftAlign);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.FormattingInfo
 * JD-Core Version:    0.6.0
 */