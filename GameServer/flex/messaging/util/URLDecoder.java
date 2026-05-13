/*     */ package flex.messaging.util;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ public final class URLDecoder
/*     */ {
/*     */   public static String decode(String s)
/*     */   {
/*     */     try
/*     */     {
/*  29 */       return decode(s, "UTF8");
/*     */     }
/*     */     catch (UnsupportedEncodingException ex) {
/*     */     }
/*  33 */     throw new IllegalArgumentException("UTF8");
/*     */   }
/*     */ 
/*     */   public static String decode(String s, String enc)
/*     */     throws UnsupportedEncodingException
/*     */   {
/*  39 */     if (!needsDecoding(s))
/*     */     {
/*  41 */       return s;
/*     */     }
/*     */ 
/*  44 */     int length = s.length();
/*  45 */     byte[] bytes = new byte[length];
/*     */ 
/*  47 */     s.getBytes(0, length, bytes, 0);
/*  48 */     int k = 0;
/*  49 */     length = bytes.length;
/*  50 */     for (int i = 0; i < length; i++)
/*     */     {
/*  52 */       if (bytes[i] == 37)
/*     */       {
/*  54 */         while (bytes[(i + 1)] == 37)
/*     */         {
/*  56 */           i++;
/*     */         }
/*  58 */         if (i < length - 2)
/*     */         {
/*  60 */           bytes[k] = x2c(bytes, i);
/*  61 */           i += 2;
/*     */         }
/*     */         else
/*     */         {
/*  65 */           throw new IllegalArgumentException(s);
/*     */         }
/*     */       }
/*  68 */       else if (bytes[i] == 43)
/*     */       {
/*  70 */         bytes[k] = 32;
/*     */       }
/*     */       else
/*     */       {
/*  74 */         bytes[k] = bytes[i];
/*     */       }
/*  76 */       k++;
/*     */     }
/*     */ 
/*  79 */     return new String(bytes, 0, k, enc);
/*     */   }
/*     */ 
/*     */   private static boolean needsDecoding(String s)
/*     */   {
/*  84 */     if (s == null)
/*     */     {
/*  86 */       return false;
/*     */     }
/*     */ 
/*  89 */     int length = s.length();
/*     */ 
/*  91 */     for (int i = 0; i < length; i++)
/*     */     {
/*  93 */       int c = s.charAt(i);
/*  94 */       if ((c == 43) || (c == 37))
/*     */       {
/*  96 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 100 */     return false;
/*     */   }
/*     */ 
/*     */   private static byte x2c(byte[] b, int i)
/*     */   {
/* 107 */     byte b1 = b[(i + 1)];
/* 108 */     byte b2 = b[(i + 2)];
/*     */ 
/* 112 */     if ((b1 < 48) || ((b1 > 70) && (b1 < 97)) || (b1 > 102) || (b2 < 48) || ((b2 > 70) && (b2 < 97)) || (b2 > 102))
/*     */     {
/* 115 */       throw new IllegalArgumentException("%" + (char)b1 + (char)b2);
/*     */     }
/*     */ 
/* 118 */     int result = b1 >= 65 ? (b1 & 0xDF) - 65 + 10 : b1 - 48;
/* 119 */     result *= 16;
/* 120 */     result += (b2 >= 65 ? (b2 & 0xDF) - 65 + 10 : b2 - 48);
/* 121 */     return (byte)result;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.URLDecoder
 * JD-Core Version:    0.6.0
 */