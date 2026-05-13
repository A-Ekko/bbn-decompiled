/*     */ package flex.messaging.io.amf.translator.decoder;
/*     */ 
/*     */ import flex.messaging.io.TypeMarshallingContext;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Collection;
/*     */ import java.util.IdentityHashMap;
/*     */ 
/*     */ public class ReferenceAwareCollectionDecoder extends CollectionDecoder
/*     */ {
/*     */   protected boolean isSuitableCollection(Object encodedObject, Class desiredClass)
/*     */   {
/*  49 */     return false;
/*     */   }
/*     */ 
/*     */   protected Collection decodeCollection(Collection shell, Object encodedObject)
/*     */   {
/*  54 */     Collection decodedCollection = shell;
/*  55 */     Object decodedObject = null;
/*  56 */     Object obj = null;
/*     */ 
/*  58 */     TypeMarshallingContext context = TypeMarshallingContext.getTypeMarshallingContext();
/*  59 */     ActionScriptDecoder decoder = null;
/*     */ 
/*  61 */     if ((encodedObject instanceof String))
/*     */     {
/*  63 */       encodedObject = ((String)encodedObject).toCharArray();
/*     */     }
/*     */     else
/*     */     {
/*  67 */       if ((encodedObject instanceof Collection))
/*     */       {
/*  69 */         encodedObject = ((Collection)encodedObject).toArray();
/*     */       }
/*     */ 
/*  72 */       context.getKnownObjects().put(encodedObject, shell);
/*     */     }
/*     */ 
/*  75 */     int len = Array.getLength(encodedObject);
/*     */ 
/*  77 */     for (int i = 0; i < len; i++)
/*     */     {
/*  79 */       obj = Array.get(encodedObject, i);
/*     */ 
/*  81 */       if (obj == null)
/*     */       {
/*  83 */         decodedCollection.add(null);
/*     */       }
/*     */       else
/*     */       {
/*  89 */         Object ref = null;
/*     */ 
/*  91 */         if (canUseByReference(obj)) {
/*  92 */           ref = context.getKnownObjects().get(obj);
/*     */         }
/*  94 */         if (ref == null)
/*     */         {
/*  96 */           decoder = DecoderFactory.getReferenceAwareDecoder(obj, obj.getClass());
/*  97 */           decodedObject = decoder.decodeObject(obj, obj.getClass());
/*     */ 
/*  99 */           if (canUseByReference(decodedObject))
/*     */           {
/* 101 */             context.getKnownObjects().put(obj, decodedObject);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 106 */           decodedObject = ref;
/*     */         }
/*     */ 
/* 109 */         decodedCollection.add(decodedObject);
/*     */       }
/*     */     }
/*     */ 
/* 113 */     return decodedCollection;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.ReferenceAwareCollectionDecoder
 * JD-Core Version:    0.6.0
 */