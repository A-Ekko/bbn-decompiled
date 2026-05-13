/*    */ package flex.messaging.io.amf;
/*    */ 
/*    */ import flex.messaging.io.SerializationContext;
/*    */ import flex.messaging.io.TypeMarshallingContext;
/*    */ 
/*    */ abstract class AmfIO
/*    */ {
/*    */   protected final SerializationContext context;
/*    */   protected boolean isDebug;
/*    */   protected AmfTrace trace;
/* 36 */   private char[] tempCharArray = null;
/* 37 */   private byte[] tempByteArray = null;
/*    */ 
/*    */   AmfIO(SerializationContext context)
/*    */   {
/* 41 */     this.context = context;
/*    */   }
/*    */ 
/*    */   public void setDebugTrace(AmfTrace trace)
/*    */   {
/* 49 */     this.trace = trace;
/* 50 */     this.isDebug = (this.trace != null);
/*    */   }
/*    */ 
/*    */   public void reset()
/*    */   {
/* 62 */     TypeMarshallingContext marshallingContext = TypeMarshallingContext.getTypeMarshallingContext();
/* 63 */     marshallingContext.reset();
/*    */   }
/*    */ 
/*    */   final char[] getTempCharArray(int capacity)
/*    */   {
/* 74 */     char[] result = this.tempCharArray;
/* 75 */     if ((result == null) || (result.length < capacity))
/*    */     {
/* 77 */       result = new char[capacity * 2];
/* 78 */       this.tempCharArray = result;
/*    */     }
/* 80 */     return result;
/*    */   }
/*    */ 
/*    */   final byte[] getTempByteArray(int capacity)
/*    */   {
/* 91 */     byte[] result = this.tempByteArray;
/* 92 */     if ((result == null) || (result.length < capacity))
/*    */     {
/* 94 */       result = new byte[capacity * 2];
/* 95 */       this.tempByteArray = result;
/*    */     }
/* 97 */     return result;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.AmfIO
 * JD-Core Version:    0.6.0
 */