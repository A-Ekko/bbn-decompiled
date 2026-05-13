/*     */ package flex.messaging.io.amf.translator.decoder;
/*     */ 
/*     */ import flex.messaging.io.PropertyProxy;
/*     */ import flex.messaging.io.PropertyProxyRegistry;
/*     */ import flex.messaging.io.TypeMarshallingContext;
/*     */ import flex.messaging.io.amf.translator.TranslationException;
/*     */ import flex.messaging.util.ClassUtil;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class TypedObjectDecoder extends ActionScriptDecoder
/*     */ {
/*     */   public boolean hasShell()
/*     */   {
/*  42 */     return true;
/*     */   }
/*     */ 
/*     */   public Object createShell(Object encodedObject, Class desiredClass)
/*     */   {
/*  47 */     Object shell = null;
/*     */ 
/*  50 */     String type = TypeMarshallingContext.getType(encodedObject);
/*     */     Class cls;
/*     */     Class cls;
/*  52 */     if (type != null)
/*     */     {
/*  54 */       TypeMarshallingContext context = TypeMarshallingContext.getTypeMarshallingContext();
/*  55 */       cls = ClassUtil.createClass(type, context.getClassLoader());
/*     */     }
/*     */     else
/*     */     {
/*  59 */       cls = desiredClass;
/*     */     }
/*     */ 
/*  62 */     shell = ClassUtil.createDefaultInstance(cls, null);
/*     */ 
/*  64 */     return shell;
/*     */   }
/*     */ 
/*     */   public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
/*     */   {
/*  69 */     Object bean = shell;
/*  70 */     if (bean == null) {
/*  71 */       return null;
/*     */     }
/*  73 */     return decodeTypedObject(bean, encodedObject);
/*     */   }
/*     */ 
/*     */   protected Object decodeTypedObject(Object bean, Object encodedObject)
/*     */   {
/*  78 */     PropertyProxy beanProxy = PropertyProxyRegistry.getProxyAndRegister(bean);
/*  79 */     PropertyProxy encodedProxy = PropertyProxyRegistry.getProxyAndRegister(encodedObject);
/*     */ 
/*  81 */     List propertyNames = beanProxy.getPropertyNames(bean);
/*  82 */     if (propertyNames != null)
/*     */     {
/*  84 */       Iterator it = propertyNames.iterator();
/*  85 */       while (it.hasNext())
/*     */       {
/*  87 */         String propName = (String)it.next();
/*     */ 
/*  89 */         Class wClass = beanProxy.getType(bean, propName);
/*     */ 
/*  92 */         Object value = encodedProxy.getValue(encodedObject, propName);
/*     */ 
/*  94 */         Object decodedObject = null;
/*     */         try
/*     */         {
/*  97 */           if (value != null)
/*     */           {
/* 102 */             ActionScriptDecoder decoder = DecoderFactory.getDecoder(value, wClass);
/* 103 */             decodedObject = decoder.decodeObject(value, wClass);
/*     */           }
/*     */ 
/* 108 */           if ((decodedObject == null) && (wClass.isPrimitive()))
/*     */           {
/* 110 */             decodedObject = getDefaultPrimitiveValue(wClass);
/*     */           }
/*     */ 
/* 113 */           beanProxy.setValue(bean, propName, decodedObject);
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 117 */           TranslationException ex = new TranslationException("Could not set object " + decodedObject + " on " + bean.getClass() + "'s " + propName);
/* 118 */           ex.setCode("Server.Processing");
/* 119 */           ex.setRootCause(e);
/* 120 */           throw ex;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 125 */     return bean;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.TypedObjectDecoder
 * JD-Core Version:    0.6.0
 */