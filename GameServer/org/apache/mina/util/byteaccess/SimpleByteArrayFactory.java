/*    */ package org.apache.mina.util.byteaccess;
/*    */ 
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ 
/*    */ public class SimpleByteArrayFactory
/*    */   implements ByteArrayFactory
/*    */ {
/*    */   public ByteArray create(int size)
/*    */   {
/* 52 */     if (size < 0)
/*    */     {
/* 54 */       throw new IllegalArgumentException("Buffer size must not be negative:" + size);
/*    */     }
/* 56 */     IoBuffer bb = IoBuffer.allocate(size);
/* 57 */     ByteArray ba = new BufferByteArray(bb)
/*    */     {
/*    */       public void free()
/*    */       {
/*    */       }
/*    */     };
/* 67 */     return ba;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.util.byteaccess.SimpleByteArrayFactory
 * JD-Core Version:    0.6.0
 */