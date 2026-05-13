/*    */ package flex.messaging.io.amf.translator.decoder;
/*    */ 
/*    */ import flex.messaging.io.SerializationContext;
/*    */ import flex.messaging.io.TypeMarshallingContext;
/*    */ import java.util.Date;
/*    */ import java.util.IdentityHashMap;
/*    */ 
/*    */ public class ReferenceAwareDateDecoder extends DateDecoder
/*    */ {
/*    */   public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
/*    */   {
/* 31 */     Object result = super.decodeObject(shell, encodedObject, desiredClass);
/*    */ 
/* 36 */     if ((result != null) && (SerializationContext.getSerializationContext().supportDatesByReference) && ((encodedObject instanceof Date)))
/*    */     {
/* 40 */       TypeMarshallingContext context = TypeMarshallingContext.getTypeMarshallingContext();
/* 41 */       context.getKnownObjects().put(encodedObject, result);
/*    */     }
/*    */ 
/* 44 */     return result;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.ReferenceAwareDateDecoder
 * JD-Core Version:    0.6.0
 */