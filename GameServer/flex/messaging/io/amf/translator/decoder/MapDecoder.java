/*    */ package flex.messaging.io.amf.translator.decoder;
/*    */ 
/*    */ import flex.messaging.io.amf.translator.TranslationException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.SortedMap;
/*    */ import java.util.TreeMap;
/*    */ 
/*    */ public class MapDecoder extends ActionScriptDecoder
/*    */ {
/*    */   public boolean hasShell()
/*    */   {
/* 35 */     return true;
/*    */   }
/*    */ 
/*    */   protected boolean isSuitableMap(Object encodedObject, Class desiredClass)
/*    */   {
/* 40 */     return (encodedObject != null) && ((encodedObject instanceof Map)) && (desiredClass.isAssignableFrom(encodedObject.getClass()));
/*    */   }
/*    */ 
/*    */   public Object createShell(Object encodedObject, Class desiredClass) {
/*    */     TranslationException ex;
/*    */     try {
/* 47 */       if (isSuitableMap(encodedObject, desiredClass))
/*    */       {
/* 49 */         return encodedObject;
/*    */       }
/*    */ 
/* 53 */       if ((desiredClass.isInterface()) || (!Map.class.isAssignableFrom(desiredClass)))
/*    */       {
/* 55 */         if (SortedMap.class.isAssignableFrom(desiredClass))
/*    */         {
/* 57 */           return new TreeMap();
/*    */         }
/*    */ 
/* 61 */         return new HashMap();
/*    */       }
/*    */ 
/* 66 */       return desiredClass.newInstance();
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 72 */       ex = new TranslationException("Could not create Map " + desiredClass, e);
/* 73 */       ex.setCode("Server.Processing");
/* 74 */     }throw ex;
/*    */   }
/*    */ 
/*    */   public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
/*    */   {
/* 80 */     if ((shell == null) || (encodedObject == null)) {
/* 81 */       return null;
/*    */     }
/*    */ 
/* 84 */     if (isSuitableMap(encodedObject, desiredClass))
/*    */     {
/* 86 */       return encodedObject;
/*    */     }
/*    */ 
/* 89 */     return decodeMap((Map)shell, (Map)encodedObject);
/*    */   }
/*    */ 
/*    */   protected Map decodeMap(Map shell, Map map)
/*    */   {
/* 94 */     if (shell != map)
/* 95 */       shell.putAll(map);
/*    */     else {
/* 97 */       shell = map;
/*    */     }
/* 99 */     return shell;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.MapDecoder
 * JD-Core Version:    0.6.0
 */