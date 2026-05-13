/*    */ package flex.messaging.io.amf.translator.decoder;
/*    */ 
/*    */ public class BooleanDecoder extends ActionScriptDecoder
/*    */ {
/*    */   public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
/*    */   {
/* 37 */     Object result = null;
/*    */ 
/* 39 */     if (encodedObject == null)
/*    */     {
/* 41 */       result = Boolean.FALSE;
/*    */     }
/* 43 */     else if ((encodedObject instanceof Boolean))
/*    */     {
/* 45 */       result = encodedObject;
/*    */     }
/* 47 */     else if ((encodedObject instanceof String))
/*    */     {
/* 49 */       String str = (String)encodedObject;
/* 50 */       result = Boolean.valueOf(str);
/*    */     }
/*    */     else
/*    */     {
/* 54 */       DecoderFactory.invalidType(encodedObject, desiredClass);
/*    */     }
/*    */ 
/* 57 */     return result;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.BooleanDecoder
 * JD-Core Version:    0.6.0
 */