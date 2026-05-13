/*     */ package org.apache.log4j.lf5.util;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public abstract class StreamUtils
/*     */ {
/*     */   public static final int DEFAULT_BUFFER_SIZE = 2048;
/*     */ 
/*     */   public static void copy(InputStream input, OutputStream output)
/*     */     throws IOException
/*     */   {
/*  57 */     copy(input, output, 2048);
/*     */   }
/*     */ 
/*     */   public static void copy(InputStream input, OutputStream output, int bufferSize)
/*     */     throws IOException
/*     */   {
/*  69 */     byte[] buf = new byte[bufferSize];
/*  70 */     int bytesRead = input.read(buf);
/*  71 */     while (bytesRead != -1) {
/*  72 */       output.write(buf, 0, bytesRead);
/*  73 */       bytesRead = input.read(buf);
/*     */     }
/*  75 */     output.flush();
/*     */   }
/*     */ 
/*     */   public static void copyThenClose(InputStream input, OutputStream output)
/*     */     throws IOException
/*     */   {
/*  85 */     copy(input, output);
/*  86 */     input.close();
/*  87 */     output.close();
/*     */   }
/*     */ 
/*     */   public static byte[] getBytes(InputStream input)
/*     */     throws IOException
/*     */   {
/*  97 */     ByteArrayOutputStream result = new ByteArrayOutputStream();
/*  98 */     copy(input, result);
/*  99 */     result.close();
/* 100 */     return result.toByteArray();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.util.StreamUtils
 * JD-Core Version:    0.6.0
 */