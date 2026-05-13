/*    */ package org.apache.mina.filter.buffer;
/*    */ 
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ import org.apache.mina.util.LazyInitializer;
/*    */ 
/*    */ public class IoBufferLazyInitializer extends LazyInitializer<IoBuffer>
/*    */ {
/*    */   private int bufferSize;
/*    */ 
/*    */   public IoBufferLazyInitializer(int bufferSize)
/*    */   {
/* 46 */     this.bufferSize = bufferSize;
/*    */   }
/*    */ 
/*    */   public IoBuffer init()
/*    */   {
/* 53 */     return IoBuffer.allocate(this.bufferSize);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.buffer.IoBufferLazyInitializer
 * JD-Core Version:    0.6.0
 */