/*    */ package org.apache.mina.util.byteaccess;
/*    */ 
/*    */ abstract class AbstractByteArray
/*    */   implements ByteArray
/*    */ {
/*    */   public final int length()
/*    */   {
/* 39 */     return last() - first();
/*    */   }
/*    */ 
/*    */   public final boolean equals(Object other)
/*    */   {
/* 50 */     if (other == this)
/*    */     {
/* 52 */       return true;
/*    */     }
/*    */ 
/* 55 */     if (!(other instanceof ByteArray))
/*    */     {
/* 57 */       return false;
/*    */     }
/* 59 */     ByteArray otherByteArray = (ByteArray)other;
/*    */ 
/* 61 */     if ((first() != otherByteArray.first()) || (last() != otherByteArray.last()) || (!order().equals(otherByteArray.order())))
/*    */     {
/* 64 */       return false;
/*    */     }
/*    */ 
/* 67 */     ByteArray.Cursor cursor = cursor();
/* 68 */     ByteArray.Cursor otherCursor = otherByteArray.cursor();
/* 69 */     for (int remaining = cursor.getRemaining(); remaining > 0; )
/*    */     {
/* 72 */       if (remaining >= 4)
/*    */       {
/* 74 */         int i = cursor.getInt();
/* 75 */         int otherI = otherCursor.getInt();
/* 76 */         if (i != otherI)
/*    */         {
/* 78 */           return false;
/*    */         }
/* 80 */         continue;
/*    */       }
/*    */ 
/* 83 */       byte b = cursor.get();
/* 84 */       byte otherB = otherCursor.get();
/* 85 */       if (b != otherB)
/*    */       {
/* 87 */         return false;
/*    */       }
/*    */     }
/*    */ 
/* 91 */     return true;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.util.byteaccess.AbstractByteArray
 * JD-Core Version:    0.6.0
 */