/*    */ package org.apache.mina.filter.codec.demux;
/*    */ 
/*    */ public class MessageDecoderResult
/*    */ {
/* 40 */   public static MessageDecoderResult OK = new MessageDecoderResult("OK");
/*    */ 
/* 47 */   public static MessageDecoderResult NEED_DATA = new MessageDecoderResult("NEED_DATA");
/*    */ 
/* 55 */   public static MessageDecoderResult NOT_OK = new MessageDecoderResult("NOT_OK");
/*    */   private final String name;
/*    */ 
/*    */   private MessageDecoderResult(String name)
/*    */   {
/* 61 */     this.name = name;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 66 */     return this.name;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.demux.MessageDecoderResult
 * JD-Core Version:    0.6.0
 */