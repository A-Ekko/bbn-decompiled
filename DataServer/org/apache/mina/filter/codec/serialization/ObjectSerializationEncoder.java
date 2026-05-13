/*    */ package org.apache.mina.filter.codec.serialization;
/*    */ 
/*    */ import java.io.NotSerializableException;
/*    */ import java.io.Serializable;
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
/*    */ import org.apache.mina.filter.codec.ProtocolEncoderOutput;
/*    */ 
/*    */ public class ObjectSerializationEncoder extends ProtocolEncoderAdapter
/*    */ {
/* 39 */   private int maxObjectSize = 2147483647;
/*    */ 
/*    */   public int getMaxObjectSize()
/*    */   {
/* 54 */     return this.maxObjectSize;
/*    */   }
/*    */ 
/*    */   public void setMaxObjectSize(int maxObjectSize)
/*    */   {
/* 64 */     if (maxObjectSize <= 0) {
/* 65 */       throw new IllegalArgumentException("maxObjectSize: " + maxObjectSize);
/*    */     }
/*    */ 
/* 69 */     this.maxObjectSize = maxObjectSize;
/*    */   }
/*    */ 
/*    */   public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception
/*    */   {
/* 74 */     if (!(message instanceof Serializable)) {
/* 75 */       throw new NotSerializableException();
/*    */     }
/*    */ 
/* 78 */     IoBuffer buf = IoBuffer.allocate(64);
/* 79 */     buf.setAutoExpand(true);
/* 80 */     buf.putObject(message);
/*    */ 
/* 82 */     int objectSize = buf.position() - 4;
/* 83 */     if (objectSize > this.maxObjectSize) {
/* 84 */       throw new IllegalArgumentException("The encoded object is too big: " + objectSize + " (> " + this.maxObjectSize + ')');
/*    */     }
/*    */ 
/* 89 */     buf.flip();
/* 90 */     out.write(buf);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.serialization.ObjectSerializationEncoder
 * JD-Core Version:    0.6.0
 */