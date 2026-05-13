/*    */ package flex.messaging.io;
/*    */ 
/*    */ import flex.messaging.io.amf.translator.ASTranslator;
/*    */ import flex.messaging.io.amf.translator.decoder.EnumDecoder;
/*    */ 
/*    */ public class Java15TypeMarshaller extends ASTranslator
/*    */ {
/*  8 */   private static EnumDecoder enumDecoder = new EnumDecoder();
/*    */ 
/*    */   public Object createInstance(Object source, Class desiredClass)
/*    */   {
/* 12 */     if (desiredClass.isEnum())
/*    */     {
/* 14 */       Object instance = enumDecoder.createShell(source, desiredClass);
/* 15 */       return instance;
/*    */     }
/*    */ 
/* 19 */     return super.createInstance(source, desiredClass);
/*    */   }
/*    */ 
/*    */   public Object convert(Object source, Class desiredClass)
/*    */   {
/* 25 */     if (desiredClass.isEnum())
/*    */     {
/* 27 */       return enumDecoder.decodeObject(source, desiredClass);
/*    */     }
/*    */ 
/* 31 */     return super.convert(source, desiredClass);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.Java15TypeMarshaller
 * JD-Core Version:    0.6.0
 */