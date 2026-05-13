/*    */ package flex.messaging.io.amf.translator.decoder;
/*    */ 
/*    */ import flex.messaging.io.TypeMarshallingContext;
/*    */ import java.util.IdentityHashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class ReferenceAwareMapDecoder extends MapDecoder
/*    */ {
/*    */   public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
/*    */   {
/* 30 */     if (shell == null) return null;
/*    */ 
/* 32 */     Map shellMap = (Map)shell;
/* 33 */     Map encodedMap = (Map)encodedObject;
/*    */ 
/* 35 */     TypeMarshallingContext context = TypeMarshallingContext.getTypeMarshallingContext();
/* 36 */     context.getKnownObjects().put(encodedObject, shell);
/*    */ 
/* 38 */     ActionScriptDecoder decoder = null;
/* 39 */     Object key = null;
/* 40 */     Object value = null;
/* 41 */     Object decodedValue = null;
/* 42 */     for (Iterator keys = encodedMap.keySet().iterator(); keys.hasNext(); )
/*    */     {
/* 44 */       key = keys.next();
/* 45 */       value = encodedMap.get(key);
/*    */ 
/* 47 */       if (value == null)
/*    */       {
/* 49 */         shellMap.put(key, null);
/* 50 */         continue;
/*    */       }
/*    */ 
/* 55 */       Object ref = null;
/*    */ 
/* 57 */       if (canUseByReference(value)) {
/* 58 */         ref = context.getKnownObjects().get(value);
/*    */       }
/* 60 */       if (ref == null)
/*    */       {
/* 62 */         decoder = DecoderFactory.getReferenceAwareDecoder(value, value.getClass());
/* 63 */         decodedValue = decoder.decodeObject(value, value.getClass());
/*    */ 
/* 65 */         if (canUseByReference(decodedValue))
/*    */         {
/* 67 */           context.getKnownObjects().put(value, decodedValue);
/*    */         }
/*    */       }
/*    */       else
/*    */       {
/* 72 */         decodedValue = ref;
/*    */       }
/*    */ 
/* 75 */       shellMap.put(key, decodedValue);
/*    */     }
/*    */ 
/* 78 */     return shellMap;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.ReferenceAwareMapDecoder
 * JD-Core Version:    0.6.0
 */