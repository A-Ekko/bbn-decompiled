/*     */ package flex.messaging.io.amf.translator.decoder;
/*     */ 
/*     */ import flex.messaging.io.SerializationContext;
/*     */ import flex.messaging.io.amf.translator.TranslationException;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Collection;
/*     */ 
/*     */ public class ArrayDecoder extends ActionScriptDecoder
/*     */ {
/*     */   public boolean hasShell()
/*     */   {
/*  42 */     return true;
/*     */   }
/*     */ 
/*     */   public Object createShell(Object encodedObject, Class desiredClass)
/*     */   {
/*  47 */     Class arrayElementClass = desiredClass.getComponentType();
/*     */ 
/*  49 */     int size = 10;
/*     */ 
/*  53 */     if (encodedObject != null)
/*     */     {
/*  55 */       if (encodedObject.getClass().isArray())
/*     */       {
/*  57 */         size = Array.getLength(encodedObject);
/*     */       }
/*  59 */       else if ((encodedObject instanceof Collection))
/*     */       {
/*  61 */         size = ((Collection)encodedObject).size();
/*     */       }
/*     */       else
/*     */       {
/*  65 */         TranslationException ex = new TranslationException("Could not create Array " + arrayElementClass);
/*  66 */         ex.setCode("Server.Processing");
/*  67 */         throw ex;
/*     */       }
/*     */     }
/*     */ 
/*  71 */     Object shell = Array.newInstance(arrayElementClass, size);
/*  72 */     return shell;
/*     */   }
/*     */ 
/*     */   public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
/*     */   {
/*  77 */     if ((shell == null) || (encodedObject == null)) {
/*  78 */       return null;
/*     */     }
/*  80 */     Class arrayElementClass = desiredClass.getComponentType();
/*     */ 
/*  82 */     if ((encodedObject instanceof Collection))
/*     */     {
/*  84 */       return decodeArray(shell, (Collection)encodedObject, arrayElementClass);
/*     */     }
/*  86 */     if (encodedObject.getClass().isArray())
/*     */     {
/*  88 */       return decodeArray(shell, encodedObject, arrayElementClass);
/*     */     }
/*  90 */     if (((encodedObject instanceof String)) && (Character.class.equals(arrayElementClass)))
/*     */     {
/*  92 */       return decodeArray(shell, (String)encodedObject, arrayElementClass);
/*     */     }
/*     */ 
/*  95 */     return null;
/*     */   }
/*     */ 
/*     */   protected Object decodeArray(Object shellArray, String string, Class arrayElementClass)
/*     */   {
/* 100 */     if (Character.class.equals(arrayElementClass))
/*     */     {
/* 102 */       return string.toCharArray();
/*     */     }
/*     */ 
/* 105 */     return null;
/*     */   }
/*     */ 
/*     */   protected Object decodeArray(Object shellArray, Collection collection, Class arrayElementClass)
/*     */   {
/* 110 */     return decodeArray(shellArray, collection.toArray(), arrayElementClass);
/*     */   }
/*     */ 
/*     */   protected Object decodeArray(Object shellArray, Object array, Class arrayElementClass)
/*     */   {
/* 115 */     Object encodedValue = null;
/* 116 */     Object decodedValue = null;
/*     */ 
/* 118 */     int n = 0;
/* 119 */     int len = Array.getLength(array);
/*     */ 
/* 121 */     for (int i = 0; i < len; i++)
/*     */     {
/* 123 */       encodedValue = Array.get(array, i);
/*     */ 
/* 125 */       if (encodedValue == null)
/*     */       {
/* 127 */         Array.set(shellArray, n, null);
/*     */       }
/*     */       else
/*     */       {
/*     */         ActionScriptDecoder decoder;
/*     */         ActionScriptDecoder decoder;
/* 135 */         if (SerializationContext.getSerializationContext().restoreReferences)
/* 136 */           decoder = DecoderFactory.getReferenceAwareDecoder(encodedValue, arrayElementClass);
/*     */         else {
/* 138 */           decoder = DecoderFactory.getDecoder(encodedValue, arrayElementClass);
/*     */         }
/* 140 */         decodedValue = decoder.decodeObject(encodedValue, arrayElementClass);
/*     */         try
/*     */         {
/* 144 */           Array.set(shellArray, n, decodedValue);
/*     */         }
/*     */         catch (IllegalArgumentException ex)
/*     */         {
/* 151 */           Array.set(shellArray, n, null);
/*     */         }
/*     */       }
/* 154 */       n++;
/*     */     }
/*     */ 
/* 157 */     return shellArray;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.ArrayDecoder
 * JD-Core Version:    0.6.0
 */