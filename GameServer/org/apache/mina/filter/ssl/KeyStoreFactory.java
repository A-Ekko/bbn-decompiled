/*     */ package org.apache.mina.filter.ssl;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.security.cert.CertificateException;
/*     */ 
/*     */ public class KeyStoreFactory
/*     */ {
/*  44 */   private String type = "JKS";
/*  45 */   private String provider = null;
/*  46 */   private char[] password = null;
/*  47 */   private byte[] data = null;
/*     */ 
/*     */   public KeyStore newInstance()
/*     */     throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException
/*     */   {
/*  56 */     if (this.data == null)
/*  57 */       throw new IllegalStateException("data property is not set.");
/*     */     KeyStore ks;
/*     */     KeyStore ks;
/*  61 */     if (this.provider == null)
/*  62 */       ks = KeyStore.getInstance(this.type);
/*     */     else {
/*  64 */       ks = KeyStore.getInstance(this.type, this.provider);
/*     */     }
/*     */ 
/*  67 */     InputStream is = new ByteArrayInputStream(this.data);
/*     */     try {
/*  69 */       ks.load(is, this.password);
/*     */     } finally {
/*     */       try {
/*  72 */         is.close();
/*     */       }
/*     */       catch (IOException ignored) {
/*     */       }
/*     */     }
/*  77 */     return ks;
/*     */   }
/*     */ 
/*     */   public void setType(String type)
/*     */   {
/*  89 */     if (type == null) {
/*  90 */       throw new NullPointerException("type");
/*     */     }
/*  92 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public void setPassword(String password)
/*     */   {
/* 103 */     if (password != null)
/* 104 */       this.password = password.toCharArray();
/*     */     else
/* 106 */       this.password = null;
/*     */   }
/*     */ 
/*     */   public void setProvider(String provider)
/*     */   {
/* 117 */     this.provider = provider;
/*     */   }
/*     */ 
/*     */   public void setData(byte[] data)
/*     */   {
/* 126 */     byte[] copy = new byte[data.length];
/* 127 */     System.arraycopy(data, 0, copy, 0, data.length);
/* 128 */     this.data = copy;
/*     */   }
/*     */ 
/*     */   private void setData(InputStream dataStream)
/*     */     throws IOException
/*     */   {
/* 137 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/*     */     try {
/*     */       while (true) {
/* 140 */         int data = dataStream.read();
/* 141 */         if (data < 0) {
/*     */           break;
/*     */         }
/* 144 */         out.write(data);
/*     */       }
/* 146 */       setData(out.toByteArray());
/*     */     } finally {
/*     */       try {
/* 149 */         dataStream.close();
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setDataFile(File dataFile)
/*     */     throws IOException
/*     */   {
/* 162 */     setData(new BufferedInputStream(new FileInputStream(dataFile)));
/*     */   }
/*     */ 
/*     */   public void setDataUrl(URL dataUrl)
/*     */     throws IOException
/*     */   {
/* 171 */     setData(dataUrl.openStream());
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.ssl.KeyStoreFactory
 * JD-Core Version:    0.6.0
 */