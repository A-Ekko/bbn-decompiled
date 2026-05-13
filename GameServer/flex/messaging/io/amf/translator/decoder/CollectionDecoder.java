/*     */ package flex.messaging.io.amf.translator.decoder;
/*     */ 
/*     */ import flex.messaging.io.amf.translator.TranslationException;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ public class CollectionDecoder extends ActionScriptDecoder
/*     */ {
/*     */   public boolean hasShell()
/*     */   {
/*  59 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean isSuitableCollection(Object encodedObject, Class desiredClass)
/*     */   {
/*  64 */     return ((encodedObject instanceof Collection)) && (desiredClass.isAssignableFrom(encodedObject.getClass()));
/*     */   }
/*     */ 
/*     */   public Object createShell(Object encodedObject, Class desiredClass)
/*     */   {
/*  69 */     Collection col = null;
/*     */     try
/*     */     {
/*  73 */       if (encodedObject != null)
/*     */       {
/*  75 */         if (isSuitableCollection(encodedObject, desiredClass))
/*     */         {
/*  77 */           col = (Collection)encodedObject;
/*     */         }
/*  81 */         else if (desiredClass.isInterface())
/*     */         {
/*  83 */           if (List.class.isAssignableFrom(desiredClass))
/*     */           {
/*  85 */             col = new ArrayList();
/*     */           }
/*  87 */           else if (SortedSet.class.isAssignableFrom(desiredClass))
/*     */           {
/*  89 */             col = new TreeSet();
/*     */           }
/*  91 */           else if (Set.class.isAssignableFrom(desiredClass))
/*     */           {
/*  93 */             col = new HashSet();
/*     */           }
/*  95 */           else if (Collection.class.isAssignableFrom(desiredClass))
/*     */           {
/*  97 */             col = new ArrayList();
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 102 */           col = (Collection)desiredClass.newInstance();
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 108 */         col = (Collection)desiredClass.newInstance();
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 113 */       TranslationException ex = new TranslationException("Could not create Collection " + desiredClass, e);
/* 114 */       ex.setCode("Server.Processing");
/* 115 */       throw ex;
/*     */     }
/*     */ 
/* 118 */     if (col == null)
/*     */     {
/* 120 */       DecoderFactory.invalidType(encodedObject, desiredClass);
/*     */     }
/*     */ 
/* 123 */     return col;
/*     */   }
/*     */ 
/*     */   public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
/*     */   {
/* 128 */     if ((shell == null) || (encodedObject == null)) {
/* 129 */       return null;
/*     */     }
/*     */ 
/* 132 */     if (isSuitableCollection(encodedObject, desiredClass))
/*     */     {
/* 134 */       return encodedObject;
/*     */     }
/*     */ 
/* 137 */     return decodeCollection((Collection)shell, encodedObject);
/*     */   }
/*     */ 
/*     */   protected Collection decodeCollection(Collection collectionShell, Object encodedObject)
/*     */   {
/* 142 */     Object obj = null;
/*     */ 
/* 144 */     if ((encodedObject instanceof String))
/*     */     {
/* 146 */       encodedObject = ((String)encodedObject).toCharArray();
/*     */     }
/* 148 */     else if ((encodedObject instanceof Collection))
/*     */     {
/* 150 */       encodedObject = ((Collection)encodedObject).toArray();
/*     */     }
/*     */ 
/* 153 */     int len = Array.getLength(encodedObject);
/*     */ 
/* 155 */     for (int i = 0; i < len; i++)
/*     */     {
/* 157 */       obj = Array.get(encodedObject, i);
/* 158 */       collectionShell.add(obj);
/*     */     }
/*     */ 
/* 161 */     return collectionShell;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.CollectionDecoder
 * JD-Core Version:    0.6.0
 */