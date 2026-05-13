/*    */ package flex.messaging.io.amf.translator.decoder;
/*    */ 
/*    */ public class EnumDecoder extends ActionScriptDecoder
/*    */ {
/*    */   public boolean hasShell()
/*    */   {
/*  7 */     return true;
/*    */   }
/*    */ 
/*    */   public Object createShell(Object encodedObject, Class desiredClass)
/*    */   {
/* 12 */     if ((encodedObject instanceof Enum))
/*    */     {
/* 14 */       return encodedObject;
/*    */     }
/*    */ 
/* 19 */     Enum value = Enum.valueOf(desiredClass, encodedObject.toString());
/* 20 */     return value;
/*    */   }
/*    */ 
/*    */   public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
/*    */   {
/* 26 */     if ((shell == null) || (encodedObject == null)) {
/* 27 */       return null;
/*    */     }
/* 29 */     return shell;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.EnumDecoder
 * JD-Core Version:    0.6.0
 */