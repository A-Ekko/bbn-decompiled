/*    */ package flex.messaging.io.amf.translator.decoder;
/*    */ 
/*    */ public class StringDecoder extends ActionScriptDecoder
/*    */ {
/*    */   public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
/*    */   {
/* 32 */     if ((encodedObject instanceof String))
/*    */     {
/* 34 */       return encodedObject;
/*    */     }
/* 36 */     if ((encodedObject instanceof Number))
/*    */     {
/* 38 */       Number num = (Number)encodedObject;
/* 39 */       return new Double(num.doubleValue()).toString();
/*    */     }
/* 41 */     if ((encodedObject instanceof Boolean))
/*    */     {
/* 43 */       Boolean bool = (Boolean)encodedObject;
/* 44 */       if (bool.booleanValue())
/*    */       {
/* 46 */         return "true";
/*    */       }
/*    */ 
/* 50 */       return "false";
/*    */     }
/*    */ 
/* 54 */     return shell;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.StringDecoder
 * JD-Core Version:    0.6.0
 */