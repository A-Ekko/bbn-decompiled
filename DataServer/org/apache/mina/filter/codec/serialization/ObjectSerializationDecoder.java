/*    */ package org.apache.mina.filter.codec.serialization;
/*    */ 
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*    */ 
/*    */ public class ObjectSerializationDecoder extends CumulativeProtocolDecoder
/*    */ {
/*    */   private final ClassLoader classLoader;
/* 41 */   private int maxObjectSize = 1048576;
/*    */ 
/*    */   public ObjectSerializationDecoder()
/*    */   {
/* 48 */     this(Thread.currentThread().getContextClassLoader());
/*    */   }
/*    */ 
/*    */   public ObjectSerializationDecoder(ClassLoader classLoader)
/*    */   {
/* 55 */     if (classLoader == null) {
/* 56 */       throw new NullPointerException("classLoader");
/*    */     }
/* 58 */     this.classLoader = classLoader;
/*    */   }
/*    */ 
/*    */   public int getMaxObjectSize()
/*    */   {
/* 68 */     return this.maxObjectSize;
/*    */   }
/*    */ 
/*    */   public void setMaxObjectSize(int maxObjectSize)
/*    */   {
/* 78 */     if (maxObjectSize <= 0) {
/* 79 */       throw new IllegalArgumentException("maxObjectSize: " + maxObjectSize);
/*    */     }
/*    */ 
/* 83 */     this.maxObjectSize = maxObjectSize;
/*    */   }
/*    */ 
/*    */   protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
/*    */     throws Exception
/*    */   {
/* 89 */     if (!in.prefixedDataAvailable(4, this.maxObjectSize)) {
/* 90 */       return false;
/*    */     }
/*    */ 
/* 93 */     out.write(in.getObject(this.classLoader));
/* 94 */     return true;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.serialization.ObjectSerializationDecoder
 * JD-Core Version:    0.6.0
 */