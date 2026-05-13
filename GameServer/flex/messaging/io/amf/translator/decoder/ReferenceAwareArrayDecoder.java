/*     */ package flex.messaging.io.amf.translator.decoder;
/*     */ 
/*     */ import flex.messaging.io.TypeMarshallingContext;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Collection;
/*     */ import java.util.IdentityHashMap;
/*     */ 
/*     */ public class ReferenceAwareArrayDecoder extends ArrayDecoder
/*     */ {
/*     */   public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
/*     */   {
/*  30 */     if ((shell == null) || (encodedObject == null)) {
/*  31 */       return null;
/*     */     }
/*  33 */     Class arrayElementClass = desiredClass.getComponentType();
/*     */ 
/*  35 */     if ((encodedObject instanceof Collection))
/*     */     {
/*  37 */       return decodeArray(shell, (Collection)encodedObject, arrayElementClass);
/*     */     }
/*  39 */     if (encodedObject.getClass().isArray())
/*     */     {
/*  41 */       return decodeArray(shell, encodedObject, arrayElementClass);
/*     */     }
/*  43 */     if (((encodedObject instanceof String)) && (Character.class.equals(arrayElementClass)))
/*     */     {
/*  45 */       return decodeArray(shell, (String)encodedObject, arrayElementClass);
/*     */     }
/*     */ 
/*  49 */     return shell;
/*     */   }
/*     */ 
/*     */   protected Object decodeArray(Object shellArray, Collection collection, Class arrayElementClass)
/*     */   {
/*  55 */     Object[] array = collection.toArray();
/*  56 */     TypeMarshallingContext.getTypeMarshallingContext().getKnownObjects().put(array, shellArray);
/*  57 */     return decodeArray(shellArray, array, arrayElementClass);
/*     */   }
/*     */ 
/*     */   protected Object decodeArray(Object shellArray, Object array, Class arrayElementClass)
/*     */   {
/*  62 */     Object encodedValue = null;
/*  63 */     Object decodedValue = null;
/*  64 */     TypeMarshallingContext context = TypeMarshallingContext.getTypeMarshallingContext();
/*     */ 
/*  66 */     ActionScriptDecoder decoder = null;
/*  67 */     int n = 0;
/*  68 */     int len = Array.getLength(array);
/*  69 */     for (int i = 0; i < len; i++)
/*     */     {
/*  71 */       encodedValue = Array.get(array, i);
/*     */ 
/*  73 */       if (encodedValue == null)
/*     */       {
/*  75 */         Array.set(shellArray, n, null);
/*     */       }
/*     */       else
/*     */       {
/*  81 */         Object ref = null;
/*     */ 
/*  83 */         if (canUseByReference(encodedValue)) {
/*  84 */           ref = context.getKnownObjects().get(encodedValue);
/*     */         }
/*  86 */         if (ref == null)
/*     */         {
/*  88 */           decoder = DecoderFactory.getReferenceAwareDecoder(encodedValue, arrayElementClass);
/*  89 */           decodedValue = decoder.decodeObject(encodedValue, arrayElementClass);
/*     */ 
/*  91 */           if (canUseByReference(decodedValue))
/*     */           {
/*  93 */             context.getKnownObjects().put(encodedValue, decodedValue);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/*  98 */           decodedValue = ref;
/*     */         }
/*     */ 
/*     */         try
/*     */         {
/* 103 */           Array.set(shellArray, n, decodedValue);
/*     */         }
/*     */         catch (IllegalArgumentException ex)
/*     */         {
/* 107 */           Array.set(shellArray, n, null);
/*     */         }
/*     */       }
/* 110 */       n++;
/*     */     }
/*     */ 
/* 113 */     return shellArray;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.ReferenceAwareArrayDecoder
 * JD-Core Version:    0.6.0
 */