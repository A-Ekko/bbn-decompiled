/*     */ package flex.messaging.io.amf.translator.decoder;
/*     */ 
/*     */ import flex.messaging.io.PropertyProxy;
/*     */ import flex.messaging.io.PropertyProxyRegistry;
/*     */ import flex.messaging.io.TypeMarshallingContext;
/*     */ import flex.messaging.io.amf.translator.TranslationException;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ReferenceAwareTypedObjectDecoder extends TypedObjectDecoder
/*     */ {
/*     */   protected Object decodeTypedObject(Object bean, Object encodedObject)
/*     */   {
/*  34 */     TypeMarshallingContext context = TypeMarshallingContext.getTypeMarshallingContext();
/*  35 */     context.getKnownObjects().put(encodedObject, bean);
/*     */ 
/*  37 */     PropertyProxy beanProxy = PropertyProxyRegistry.getProxy(bean);
/*  38 */     PropertyProxy encodedProxy = PropertyProxyRegistry.getProxy(encodedObject);
/*     */ 
/*  40 */     List propertyNames = beanProxy.getPropertyNames(bean);
/*  41 */     if (propertyNames != null)
/*     */     {
/*  43 */       Iterator it = propertyNames.iterator();
/*  44 */       while (it.hasNext())
/*     */       {
/*  46 */         String propName = (String)it.next();
/*     */ 
/*  48 */         Class wClass = beanProxy.getType(bean, propName);
/*     */ 
/*  51 */         Object value = encodedProxy.getValue(encodedObject, propName);
/*     */ 
/*  53 */         Object decodedObject = null;
/*     */         try
/*     */         {
/*  56 */           if (value != null)
/*     */           {
/*  60 */             Object ref = null;
/*     */ 
/*  62 */             if (canUseByReference(value)) {
/*  63 */               ref = context.getKnownObjects().get(value);
/*     */             }
/*  65 */             if (ref == null)
/*     */             {
/*  67 */               ActionScriptDecoder decoder = DecoderFactory.getReferenceAwareDecoder(value, wClass);
/*  68 */               decodedObject = decoder.decodeObject(value, wClass);
/*     */ 
/*  70 */               if (canUseByReference(decodedObject))
/*     */               {
/*  72 */                 context.getKnownObjects().put(value, decodedObject);
/*     */               }
/*     */             }
/*     */             else
/*     */             {
/*  77 */               decodedObject = ref;
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*  83 */           if ((decodedObject == null) && (wClass.isPrimitive()))
/*     */           {
/*  85 */             decodedObject = getDefaultPrimitiveValue(wClass);
/*     */           }
/*     */ 
/*  88 */           beanProxy.setValue(bean, propName, decodedObject);
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*  92 */           TranslationException ex = new TranslationException("Could not set object " + decodedObject + " on " + bean.getClass() + "'s " + propName);
/*  93 */           ex.setCode("Server.Processing");
/*  94 */           ex.setRootCause(e);
/*  95 */           throw ex;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 100 */     return bean;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.ReferenceAwareTypedObjectDecoder
 * JD-Core Version:    0.6.0
 */