/*     */ package com.pst.core.amf;
/*     */ 
/*     */ import flex.messaging.io.SerializationContext;
/*     */ import flex.messaging.io.amf.Amf3Input;
/*     */ import flex.messaging.io.amf.Amf3Output;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.zip.Deflater;
/*     */ import java.util.zip.Inflater;
/*     */ 
/*     */ public class AmfData
/*     */ {
/*     */   public Object readData(byte[] data)
/*     */   {
/*  28 */     Object obj = null;
/*  29 */     Amf3Input amfin = new Amf3Input(new SerializationContext());
/*  30 */     InputStream bIn = new ByteArrayInputStream(decompressBytes(data));
/*  31 */     amfin.setInputStream(bIn);
/*     */     try
/*     */     {
/*  34 */       obj = amfin.readObject();
/*     */     }
/*     */     catch (ClassNotFoundException e)
/*     */     {
/*  38 */       e.printStackTrace();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*  42 */       e.printStackTrace();
/*     */     }
/*  44 */     return obj;
/*     */   }
/*     */ 
/*     */   public byte[] getData(Object obj)
/*     */   {
/*  57 */     Amf3Output amfout = new Amf3Output(new SerializationContext());
/*     */ 
/*  62 */     ByteArrayOutputStream byteoutStream = new ByteArrayOutputStream();
/*     */ 
/*  65 */     DataOutputStream dataoutstream = new DataOutputStream(byteoutStream);
/*     */ 
/*  68 */     amfout.setOutputStream(dataoutstream);
/*     */     try
/*     */     {
/*  71 */       amfout.writeObject(obj);
/*     */ 
/*  73 */       dataoutstream.flush();
/*     */     } catch (IOException e) {
/*  75 */       e.printStackTrace();
/*     */     }
/*  77 */     return compressBytes(byteoutStream.toByteArray());
/*     */   }
/*     */ 
/*     */   public byte[] compressBytes(byte[] input)
/*     */   {
/*  86 */     int cachesize = 1024;
/*  87 */     Deflater compresser = new Deflater();
/*  88 */     compresser.reset();
/*  89 */     compresser.setInput(input);
/*  90 */     compresser.finish();
/*  91 */     byte[] output = new byte[0];
/*  92 */     ByteArrayOutputStream o = new ByteArrayOutputStream(input.length);
/*     */     try {
/*  94 */       byte[] buf = new byte[cachesize];
/*     */ 
/*  96 */       while (!compresser.finished()) {
/*  97 */         int got = compresser.deflate(buf);
/*  98 */         o.write(buf, 0, got);
/*     */       }
/* 100 */       output = o.toByteArray();
/*     */     } finally {
/*     */       try {
/* 103 */         o.close();
/*     */       } catch (IOException e) {
/* 105 */         e.printStackTrace();
/*     */       }
/*     */     }
/* 108 */     return output;
/*     */   }
/*     */ 
/*     */   public byte[] decompressBytes(byte[] input)
/*     */   {
/* 117 */     int cachesize = 1024;
/* 118 */     Inflater decompresser = new Inflater();
/* 119 */     byte[] output = new byte[0];
/*     */ 
/* 121 */     decompresser.reset();
/* 122 */     decompresser.setInput(input);
/* 123 */     ByteArrayOutputStream o = new ByteArrayOutputStream(input.length);
/*     */     try {
/* 125 */       byte[] buf = new byte[cachesize];
/*     */ 
/* 127 */       while (!decompresser.finished()) {
/* 128 */         int got = decompresser.inflate(buf);
/* 129 */         o.write(buf, 0, got);
/*     */       }
/* 131 */       output = o.toByteArray();
/*     */     } catch (Exception e) {
/* 133 */       e.printStackTrace();
/*     */       try
/*     */       {
/* 136 */         o.close();
/*     */       } catch (IOException e) {
/* 138 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 136 */         o.close();
/*     */       } catch (IOException e) {
/* 138 */         e.printStackTrace();
/*     */       }
/*     */     }
/* 141 */     return output;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.amf.AmfData
 * JD-Core Version:    0.6.0
 */