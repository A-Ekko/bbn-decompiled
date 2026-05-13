/*     */ package flex.messaging.io.amf.translator.decoder;
/*     */ 
/*     */ import flex.messaging.io.SerializationContext;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ 
/*     */ public abstract class ActionScriptDecoder
/*     */ {
/*     */   public boolean hasShell()
/*     */   {
/*  33 */     return false;
/*     */   }
/*     */ 
/*     */   public Object createShell(Object encodedObject, Class desiredClass)
/*     */   {
/*  41 */     return null;
/*     */   }
/*     */ 
/*     */   public abstract Object decodeObject(Object paramObject1, Object paramObject2, Class paramClass);
/*     */ 
/*     */   public Object decodeObject(Object encodedObject, Class desiredClass)
/*     */   {
/*  52 */     Object shell = null;
/*     */ 
/*  54 */     if (hasShell())
/*     */     {
/*  56 */       shell = createShell(encodedObject, desiredClass);
/*     */     }
/*     */ 
/*  59 */     return decodeObject(shell, encodedObject, desiredClass);
/*     */   }
/*     */ 
/*     */   protected boolean canUseByReference(Object o)
/*     */   {
/*  64 */     if (o == null) {
/*  65 */       return false;
/*     */     }
/*  67 */     if ((o instanceof String)) {
/*  68 */       return false;
/*     */     }
/*  70 */     if ((o instanceof Number)) {
/*  71 */       return false;
/*     */     }
/*  73 */     if ((o instanceof Boolean)) {
/*  74 */       return false;
/*     */     }
/*  76 */     if ((o instanceof Date))
/*     */     {
/*  79 */       return SerializationContext.getSerializationContext().supportDatesByReference;
/*     */     }
/*     */ 
/*  84 */     if ((o instanceof Calendar)) {
/*  85 */       return false;
/*     */     }
/*     */ 
/*  88 */     return !(o instanceof Character);
/*     */   }
/*     */ 
/*     */   protected static Object getDefaultPrimitiveValue(Class type)
/*     */   {
/*  95 */     if (type == Boolean.TYPE)
/*  96 */       return Boolean.FALSE;
/*  97 */     if (type == Integer.TYPE)
/*  98 */       return new Integer(0);
/*  99 */     if (type == Double.TYPE)
/* 100 */       return new Double(0.0D);
/* 101 */     if (type == Long.TYPE)
/* 102 */       return new Long(0L);
/* 103 */     if (type == Float.TYPE)
/* 104 */       return new Float(0.0F);
/* 105 */     if (type == Character.TYPE)
/* 106 */       return new Character('\000');
/* 107 */     if (type == Short.TYPE)
/* 108 */       return new Short(0);
/* 109 */     if (type == Byte.TYPE) {
/* 110 */       return new Byte(0);
/*     */     }
/* 112 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.ActionScriptDecoder
 * JD-Core Version:    0.6.0
 */