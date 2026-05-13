/*     */ package flex.messaging.io.amf.translator.decoder;
/*     */ 
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
/*     */ 
/*     */ public class DateDecoder extends ActionScriptDecoder
/*     */ {
/*     */   public Object decodeObject(Object shell, Object encodedObject, Class desiredClass)
/*     */   {
/*  41 */     java.util.Date result = null;
/*     */ 
/*  43 */     if (java.sql.Date.class.isAssignableFrom(desiredClass))
/*     */     {
/*  45 */       if ((encodedObject instanceof java.util.Date))
/*     */       {
/*  47 */         java.util.Date date = (java.util.Date)encodedObject;
/*  48 */         result = new java.sql.Date(date.getTime());
/*     */       }
/*  50 */       else if ((encodedObject instanceof Calendar))
/*     */       {
/*  52 */         Calendar calendar = (Calendar)encodedObject;
/*  53 */         result = new java.sql.Date(calendar.getTimeInMillis());
/*     */       }
/*  55 */       else if ((encodedObject instanceof Number))
/*     */       {
/*  57 */         Number number = (Number)encodedObject;
/*  58 */         result = new java.sql.Date(number.longValue());
/*     */       }
/*     */     }
/*  61 */     else if (Timestamp.class.isAssignableFrom(desiredClass))
/*     */     {
/*  63 */       if ((encodedObject instanceof java.util.Date))
/*     */       {
/*  65 */         java.util.Date date = (java.util.Date)encodedObject;
/*  66 */         result = new Timestamp(date.getTime());
/*     */       }
/*  68 */       else if ((encodedObject instanceof Calendar))
/*     */       {
/*  70 */         Calendar calendar = (Calendar)encodedObject;
/*  71 */         result = new Timestamp(calendar.getTimeInMillis());
/*     */       }
/*  73 */       else if ((encodedObject instanceof Number))
/*     */       {
/*  75 */         Number number = (Number)encodedObject;
/*  76 */         result = new Timestamp(number.longValue());
/*     */       }
/*     */     }
/*  79 */     else if (Time.class.isAssignableFrom(desiredClass))
/*     */     {
/*  81 */       if ((encodedObject instanceof java.util.Date))
/*     */       {
/*  83 */         java.util.Date date = (java.util.Date)encodedObject;
/*  84 */         result = new Time(date.getTime());
/*     */       }
/*  86 */       else if ((encodedObject instanceof Calendar))
/*     */       {
/*  88 */         Calendar calendar = (Calendar)encodedObject;
/*  89 */         result = new Time(calendar.getTimeInMillis());
/*     */       }
/*  91 */       else if ((encodedObject instanceof Number))
/*     */       {
/*  93 */         Number number = (Number)encodedObject;
/*  94 */         result = new Time(number.longValue());
/*     */       }
/*     */     }
/*  97 */     else if (java.util.Date.class.isAssignableFrom(desiredClass))
/*     */     {
/*  99 */       if ((encodedObject instanceof java.util.Date))
/*     */       {
/* 101 */         result = (java.util.Date)encodedObject;
/*     */       }
/* 103 */       else if ((encodedObject instanceof Calendar))
/*     */       {
/* 105 */         Calendar calendar = (Calendar)encodedObject;
/* 106 */         result = calendar.getTime();
/*     */       }
/* 108 */       else if ((encodedObject instanceof Number))
/*     */       {
/* 110 */         Number number = (Number)encodedObject;
/* 111 */         result = new java.util.Date(number.longValue());
/*     */       }
/*     */     }
/*     */ 
/* 115 */     if (result == null)
/*     */     {
/* 117 */       DecoderFactory.invalidType(encodedObject, desiredClass);
/*     */     }
/*     */ 
/* 120 */     return result;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.translator.decoder.DateDecoder
 * JD-Core Version:    0.6.0
 */