/*     */ package flex.messaging.io.amf.translator.decoder;
/*     */ 
/*     */ import flex.messaging.io.SerializationContext;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ public class NumberDecoder extends ActionScriptDecoder
/*     */ {
/*     */   public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
/*     */   {
/*  33 */     Object result = null;
/*     */ 
/*  35 */     if ((encodedObject != null) && ((encodedObject instanceof String)))
/*     */     {
/*  37 */       String str = ((String)encodedObject).trim();
/*     */       try
/*     */       {
/*  42 */         if (!SerializationContext.getSerializationContext().legacyBigNumbers)
/*     */         {
/*  44 */           if (BigInteger.class.equals(desiredClass))
/*     */           {
/*  46 */             result = new BigInteger(str);
/*  47 */             return result;
/*     */           }
/*  49 */           if (BigDecimal.class.equals(desiredClass))
/*     */           {
/*  51 */             result = new BigDecimal(str);
/*  52 */             return result;
/*     */           }
/*     */         }
/*     */ 
/*  56 */         Double dbl = new Double(str);
/*  57 */         encodedObject = dbl;
/*     */       }
/*     */       catch (NumberFormatException nfe)
/*     */       {
/*  61 */         DecoderFactory.invalidType(encodedObject, desiredClass);
/*     */       }
/*     */     }
/*     */ 
/*  65 */     if (((encodedObject instanceof Number)) || (encodedObject == null))
/*     */     {
/*  69 */       if (desiredClass.isPrimitive())
/*     */       {
/*     */         Double dbl;
/*     */         Double dbl;
/*  71 */         if (encodedObject == null)
/*  72 */           dbl = new Double(0.0D);
/*     */         else {
/*  74 */           dbl = new Double(((Number)encodedObject).doubleValue());
/*     */         }
/*  76 */         if ((Object.class.equals(desiredClass)) || (Double.TYPE.equals(desiredClass)))
/*  77 */           result = dbl;
/*  78 */         else if (Integer.TYPE.equals(desiredClass))
/*  79 */           result = new Integer(dbl.intValue());
/*  80 */         else if (Long.TYPE.equals(desiredClass))
/*  81 */           result = new Long(dbl.longValue());
/*  82 */         else if (Float.TYPE.equals(desiredClass))
/*  83 */           result = new Float(dbl.floatValue());
/*  84 */         else if (Short.TYPE.equals(desiredClass))
/*  85 */           result = new Short(dbl.shortValue());
/*  86 */         else if (Byte.TYPE.equals(desiredClass))
/*  87 */           result = new Byte(dbl.byteValue());
/*     */       }
/*  89 */       else if (encodedObject != null)
/*     */       {
/*  91 */         Double dbl = new Double(((Number)encodedObject).doubleValue());
/*     */ 
/*  93 */         if ((Object.class.equals(desiredClass)) || (Number.class.equals(desiredClass)) || (Double.class.equals(desiredClass)))
/*     */         {
/*  95 */           result = dbl;
/*  96 */         } else if (Integer.class.equals(desiredClass)) {
/*  97 */           result = new Integer(dbl.intValue());
/*  98 */         } else if (Long.class.equals(desiredClass)) {
/*  99 */           result = new Long(dbl.longValue());
/* 100 */         } else if (Float.class.equals(desiredClass)) {
/* 101 */           result = new Float(dbl.floatValue());
/* 102 */         } else if (Short.class.equals(desiredClass)) {
/* 103 */           result = new Short(dbl.shortValue());
/* 104 */         } else if (BigDecimal.class.equals(desiredClass))
/*     */         {
/* 110 */           if (SerializationContext.getSerializationContext().legacyBigNumbers)
/* 111 */             result = new BigDecimal(dbl.doubleValue());
/*     */           else
/* 113 */             result = new BigDecimal(String.valueOf(dbl));
/*     */         }
/* 115 */         else if (Byte.class.equals(desiredClass)) {
/* 116 */           result = new Byte(dbl.byteValue());
/* 117 */         } else if (BigInteger.class.equals(desiredClass))
/*     */         {
/* 120 */           String val = null;
/* 121 */           long l = dbl.longValue();
/* 122 */           if (l > 2147483647L)
/*     */           {
/* 124 */             Long lo = new Long(dbl.longValue());
/* 125 */             val = lo.toString().toUpperCase();
/* 126 */             int suffix = val.indexOf("L");
/* 127 */             if (suffix != -1)
/* 128 */               val = val.substring(0, suffix);
/*     */           }
/*     */           else
/*     */           {
/* 132 */             Integer i = new Integer(dbl.intValue());
/* 133 */             val = i.toString();
/*     */           }
/* 135 */           result = new BigInteger(val.trim());
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 140 */         result = null;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 145 */       DecoderFactory.invalidType(encodedObject, desiredClass);
/*     */     }
/*     */ 
/* 148 */     return result;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.NumberDecoder
 * JD-Core Version:    0.6.0
 */