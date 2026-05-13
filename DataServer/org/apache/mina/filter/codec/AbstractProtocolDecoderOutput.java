/*    */ package org.apache.mina.filter.codec;
/*    */ 
/*    */ import java.util.Queue;
/*    */ import org.apache.mina.util.CircularQueue;
/*    */ 
/*    */ public abstract class AbstractProtocolDecoderOutput
/*    */   implements ProtocolDecoderOutput
/*    */ {
/* 33 */   private final Queue<Object> messageQueue = new CircularQueue();
/*    */ 
/*    */   public Queue<Object> getMessageQueue()
/*    */   {
/* 39 */     return this.messageQueue;
/*    */   }
/*    */ 
/*    */   public void write(Object message) {
/* 43 */     if (message == null) {
/* 44 */       throw new NullPointerException("message");
/*    */     }
/*    */ 
/* 47 */     this.messageQueue.add(message);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.AbstractProtocolDecoderOutput
 * JD-Core Version:    0.6.0
 */