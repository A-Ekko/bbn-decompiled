/*    */ package flex.messaging.io.amf.translator.decoder;
/*    */ 
/*    */ public class CharacterDecoder extends ActionScriptDecoder
/*    */ {
/*    */   public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
/*    */   {
/* 35 */     Character result = null;
/*    */ 
/* 37 */     if (encodedObject == null)
/*    */     {
/* 39 */       char c = '\000';
/* 40 */       result = new Character(c);
/*    */     }
/* 42 */     else if ((encodedObject instanceof String))
/*    */     {
/* 44 */       String str = (String)encodedObject;
/*    */ 
/* 46 */       char[] chars = str.toCharArray();
/*    */ 
/* 48 */       if (chars.length > 0)
/*    */       {
/* 50 */         result = new Character(chars[0]);
/*    */       }
/*    */     }
/* 53 */     else if ((encodedObject instanceof Character))
/*    */     {
/* 55 */       result = (Character)encodedObject;
/*    */     }
/*    */ 
/* 58 */     if (result == null)
/*    */     {
/* 60 */       DecoderFactory.invalidType(encodedObject, desiredClass);
/*    */     }
/*    */ 
/* 63 */     return result;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.CharacterDecoder
 * JD-Core Version:    0.6.0
 */