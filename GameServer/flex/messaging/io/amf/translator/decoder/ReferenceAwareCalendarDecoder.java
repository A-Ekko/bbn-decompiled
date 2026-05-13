/*    */ package flex.messaging.io.amf.translator.decoder;
/*    */ 
/*    */ import flex.messaging.io.SerializationContext;
/*    */ import flex.messaging.io.TypeMarshallingContext;
/*    */ import java.util.Date;
/*    */ import java.util.IdentityHashMap;
/*    */ 
/*    */ public class ReferenceAwareCalendarDecoder extends CalendarDecoder
/*    */ {
/*    */   public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
/*    */   {
/* 28 */     Object result = super.decodeObject(shell, encodedObject, desiredClass);
/*    */ 
/* 33 */     if ((result != null) && (SerializationContext.getSerializationContext().supportDatesByReference) && ((encodedObject instanceof Date)))
/*    */     {
/* 37 */       TypeMarshallingContext context = TypeMarshallingContext.getTypeMarshallingContext();
/* 38 */       context.getKnownObjects().put(encodedObject, result);
/*    */     }
/*    */ 
/* 41 */     return result;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.ReferenceAwareCalendarDecoder
 * JD-Core Version:    0.6.0
 */