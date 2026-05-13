/*    */ package flex.messaging.io.amf.translator;
/*    */ 
/*    */ import flex.messaging.io.SerializationContext;
/*    */ import flex.messaging.io.TypeMarshaller;
/*    */ import flex.messaging.io.amf.translator.decoder.ActionScriptDecoder;
/*    */ import flex.messaging.io.amf.translator.decoder.DecoderFactory;
/*    */ import flex.messaging.util.ClassUtil;
/*    */ import flex.messaging.util.Trace;
/*    */ 
/*    */ public class ASTranslator
/*    */   implements TypeMarshaller
/*    */ {
/*    */   public Object createInstance(Object source, Class desiredClass)
/*    */   {
/* 36 */     ActionScriptDecoder decoder = DecoderFactory.getDecoderForShell(desiredClass);
/*    */ 
/* 38 */     Object instance = null;
/* 39 */     if (decoder.hasShell())
/*    */     {
/* 41 */       instance = decoder.createShell(source, desiredClass);
/*    */     }
/*    */     else
/*    */     {
/* 45 */       instance = ClassUtil.createDefaultInstance(desiredClass, null);
/*    */     }
/*    */ 
/* 48 */     return instance;
/*    */   }
/*    */ 
/*    */   public Object convert(Object source, Class desiredClass)
/*    */   {
/* 57 */     if ((source == null) && (!desiredClass.isPrimitive()))
/*    */     {
/* 59 */       return null;
/*    */     }
/*    */ 
/* 62 */     SerializationContext serializationContext = SerializationContext.getSerializationContext();
/*    */     ActionScriptDecoder decoder;
/*    */     ActionScriptDecoder decoder;
/* 65 */     if (serializationContext.restoreReferences)
/* 66 */       decoder = DecoderFactory.getReferenceAwareDecoder(source, desiredClass);
/*    */     else {
/* 68 */       decoder = DecoderFactory.getDecoder(source, desiredClass);
/*    */     }
/* 70 */     if (Trace.remote)
/*    */     {
/* 72 */       Trace.trace("Decoder for " + (source == null ? "null" : source.getClass().toString()) + " with desired " + desiredClass + " is " + decoder.getClass());
/*    */     }
/*    */ 
/* 76 */     Object result = decoder.decodeObject(source, desiredClass);
/* 77 */     return result;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.ASTranslator
 * JD-Core Version:    0.6.0
 */