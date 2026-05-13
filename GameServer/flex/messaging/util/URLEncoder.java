/*     */ package flex.messaging.util;
/*     */ 
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ public final class URLEncoder
/*     */ {
/*  29 */   public static String charset = "UTF8";
/*     */ 
/*     */   public static String encode(String s)
/*     */   {
/*     */     try
/*     */     {
/*  39 */       return encode(s, charset);
/*     */     }
/*     */     catch (UnsupportedEncodingException ex) {
/*     */     }
/*  43 */     throw new IllegalArgumentException(charset);
/*     */   }
/*     */ 
/*     */   public static String encode(String s, String enc)
/*     */     throws UnsupportedEncodingException
/*     */   {
/*  49 */     if (!needsEncoding(s))
/*     */     {
/*  51 */       return s;
/*     */     }
/*     */ 
/*  54 */     int length = s.length();
/*     */ 
/*  56 */     StringBuffer out = new StringBuffer(length);
/*     */ 
/*  58 */     ByteArrayOutputStream buf = new ByteArrayOutputStream(10);
/*     */ 
/*  60 */     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(buf, enc));
/*     */ 
/*  62 */     for (int i = 0; i < length; i++)
/*     */     {
/*  64 */       int c = s.charAt(i);
/*  65 */       if (((c >= 97) && (c <= 122)) || ((c >= 65) && (c <= 90)) || ((c >= 48) && (c <= 57)) || (c == 32))
/*     */       {
/*  67 */         if (c == 32)
/*     */         {
/*  69 */           c = 43;
/*     */         }
/*     */ 
/*  72 */         toHex(out, buf.toByteArray());
/*  73 */         buf.reset();
/*     */ 
/*  75 */         out.append((char)c);
/*     */       }
/*     */       else
/*     */       {
/*     */         try
/*     */         {
/*  81 */           writer.write(c);
/*     */ 
/*  83 */           if ((c >= 55296) && (c <= 56319) && (i < length - 1))
/*     */           {
/*  85 */             int d = s.charAt(i + 1);
/*  86 */             if ((d >= 56320) && (d <= 57343))
/*     */             {
/*  88 */               writer.write(d);
/*  89 */               i++;
/*     */             }
/*     */           }
/*     */ 
/*  93 */           writer.flush();
/*     */         }
/*     */         catch (IOException ex)
/*     */         {
/*  97 */           throw new IllegalArgumentException(s);
/*     */         }
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 103 */       writer.close();
/*     */     }
/*     */     catch (IOException ioe)
/*     */     {
/*     */     }
/*     */ 
/* 110 */     toHex(out, buf.toByteArray());
/*     */ 
/* 112 */     return out.toString();
/*     */   }
/*     */ 
/*     */   private static void toHex(StringBuffer buffer, byte[] b)
/*     */   {
/* 117 */     for (int i = 0; i < b.length; i++)
/*     */     {
/* 119 */       buffer.append('%');
/*     */ 
/* 121 */       char ch = Character.forDigit(b[i] >> 4 & 0xF, 16);
/* 122 */       if (Character.isLetter(ch))
/*     */       {
/* 124 */         ch = (char)(ch - ' ');
/*     */       }
/* 126 */       buffer.append(ch);
/*     */ 
/* 128 */       ch = Character.forDigit(b[i] & 0xF, 16);
/* 129 */       if (Character.isLetter(ch))
/*     */       {
/* 131 */         ch = (char)(ch - ' ');
/*     */       }
/* 133 */       buffer.append(ch);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean needsEncoding(String s)
/*     */   {
/* 139 */     if (s == null)
/*     */     {
/* 141 */       return false;
/*     */     }
/*     */ 
/* 144 */     int length = s.length();
/*     */ 
/* 146 */     for (int i = 0; i < length; i++)
/*     */     {
/* 148 */       int c = s.charAt(i);
/* 149 */       if (((c < 97) || (c > 122)) && ((c < 65) || (c > 90)) && ((c < 48) || (c > 57)))
/*     */       {
/* 155 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 159 */     return false;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.URLEncoder
 * JD-Core Version:    0.6.0
 */