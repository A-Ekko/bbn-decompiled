/*    */ package org.apache.mina.filter.codec.statemachine;
/*    */ 
/*    */ public abstract class LinearWhitespaceSkippingState extends SkippingState
/*    */ {
/*    */   protected boolean canSkip(byte b)
/*    */   {
/* 35 */     return (b == 32) || (b == 9);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.statemachine.LinearWhitespaceSkippingState
 * JD-Core Version:    0.6.0
 */