/*    */ package flex.messaging.io.amf.translator.decoder;
/*    */ 
/*    */ import java.util.Calendar;
/*    */ import java.util.Date;
/*    */ 
/*    */ public class CalendarDecoder extends ActionScriptDecoder
/*    */ {
/*    */   public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
/*    */   {
/* 42 */     Object result = null;
/*    */ 
/* 44 */     if ((encodedObject instanceof Date))
/*    */     {
/* 46 */       Calendar calendar = Calendar.getInstance();
/* 47 */       calendar.setTime((Date)encodedObject);
/* 48 */       result = calendar;
/*    */     }
/* 50 */     else if ((encodedObject instanceof Calendar))
/*    */     {
/* 52 */       result = encodedObject;
/*    */     }
/* 54 */     else if ((encodedObject instanceof Number))
/*    */     {
/* 56 */       Calendar calendar = Calendar.getInstance();
/* 57 */       Number number = (Number)encodedObject;
/* 58 */       calendar.setTimeInMillis(number.longValue());
/* 59 */       result = calendar;
/*    */     }
/*    */     else
/*    */     {
/* 63 */       DecoderFactory.invalidType(encodedObject, desiredClass);
/*    */     }
/*    */ 
/* 67 */     return result;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.CalendarDecoder
 * JD-Core Version:    0.6.0
 */