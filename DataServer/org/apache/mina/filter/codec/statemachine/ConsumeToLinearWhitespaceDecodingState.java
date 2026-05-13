/*    */ package org.apache.mina.filter.codec.statemachine;
/*    */ 
/*    */ public abstract class ConsumeToLinearWhitespaceDecodingState extends ConsumeToDynamicTerminatorDecodingState
/*    */ {
/*    */   protected boolean isTerminator(byte b)
/*    */   {
/* 37 */     return (b == 32) || (b == 9);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.statemachine.ConsumeToLinearWhitespaceDecodingState
 * JD-Core Version:    0.6.0
 */