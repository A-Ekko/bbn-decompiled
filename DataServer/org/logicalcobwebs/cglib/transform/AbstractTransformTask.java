/*     */ package org.logicalcobwebs.cglib.transform;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.util.zip.CRC32;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipInputStream;
/*     */ import java.util.zip.ZipOutputStream;
/*     */ import org.logicalcobwebs.cglib.asm.Attribute;
/*     */ import org.logicalcobwebs.cglib.asm.ClassReader;
/*     */ import org.logicalcobwebs.cglib.asm.ClassWriter;
/*     */ import org.logicalcobwebs.cglib.asm.attrs.Attributes;
/*     */ import org.logicalcobwebs.cglib.core.ClassNameReader;
/*     */ import org.logicalcobwebs.cglib.core.DebuggingClassWriter;
/*     */ 
/*     */ public abstract class AbstractTransformTask extends AbstractProcessTask
/*     */ {
/*     */   private static final int ZIP_MAGIC = 1347093252;
/*     */   private static final int CLASS_MAGIC = -889275714;
/*     */   private boolean verbose;
/*     */ 
/*     */   public void setVerbose(boolean verbose)
/*     */   {
/*  41 */     this.verbose = verbose;
/*     */   }
/*     */ 
/*     */   protected abstract ClassTransformer getClassTransformer(String[] paramArrayOfString);
/*     */ 
/*     */   protected Attribute[] attributes()
/*     */   {
/*  56 */     return Attributes.getDefaultAttributes();
/*     */   }
/*     */ 
/*     */   protected void processFile(File file) throws Exception
/*     */   {
/*  61 */     if (isClassFile(file))
/*     */     {
/*  63 */       processClassFile(file);
/*     */     }
/*  65 */     else if (isJarFile(file))
/*     */     {
/*  67 */       processJarFile(file);
/*     */     }
/*     */     else
/*     */     {
/*  71 */       log("ignoring " + file.toURL(), 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processClassFile(File file)
/*     */     throws Exception, FileNotFoundException, IOException, MalformedURLException
/*     */   {
/*  86 */     ClassReader reader = getClassReader(file);
/*  87 */     String[] name = ClassNameReader.getClassInfo(reader);
/*  88 */     ClassWriter w = new DebuggingClassWriter(true);
/*  89 */     ClassTransformer t = getClassTransformer(name);
/*  90 */     if (t != null)
/*     */     {
/*  92 */       if (this.verbose) {
/*  93 */         log("processing " + file.toURL());
/*     */       }
/*  95 */       new TransformingClassGenerator(new ClassReaderGenerator(getClassReader(file), attributes(), skipDebug()), t).generateClass(w);
/*     */ 
/*  98 */       FileOutputStream fos = new FileOutputStream(file);
/*     */       try {
/* 100 */         fos.write(w.toByteArray());
/*     */       } finally {
/* 102 */         fos.close();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean skipDebug()
/*     */   {
/* 110 */     return false;
/*     */   }
/*     */ 
/*     */   private static ClassReader getClassReader(File file) throws Exception {
/* 114 */     InputStream in = new BufferedInputStream(new FileInputStream(file));
/*     */     try {
/* 116 */       ClassReader r = new ClassReader(in);
/* 117 */       ClassReader localClassReader1 = r;
/*     */       return localClassReader1;
/*     */     }
/*     */     finally
/*     */     {
/* 119 */       in.close();
/* 120 */     }throw localObject;
/*     */   }
/*     */ 
/*     */   protected boolean isClassFile(File file)
/*     */     throws IOException
/*     */   {
/* 126 */     return checkMagic(file, -889275714L);
/*     */   }
/*     */ 
/*     */   protected void processJarFile(File file)
/*     */     throws Exception
/*     */   {
/* 132 */     if (this.verbose) {
/* 133 */       log("processing " + file.toURL());
/*     */     }
/*     */ 
/* 136 */     File tempFile = File.createTempFile(file.getName(), null, new File(file.getAbsoluteFile().getParent()));
/*     */     try
/*     */     {
/* 140 */       ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
/*     */       try {
/* 142 */         FileOutputStream fout = new FileOutputStream(tempFile, false);
/*     */         try {
/* 144 */           ZipOutputStream out = new ZipOutputStream(fout);
/*     */           ZipEntry entry;
/* 147 */           while ((entry = zip.getNextEntry()) != null)
/*     */           {
/* 150 */             byte[] bytes = getBytes(zip);
/*     */ 
/* 152 */             if (!entry.isDirectory())
/*     */             {
/* 154 */               DataInputStream din = new DataInputStream(new ByteArrayInputStream(bytes));
/*     */ 
/* 158 */               if (din.readInt() == -889275714)
/*     */               {
/* 160 */                 bytes = process(bytes);
/*     */               }
/* 163 */               else if (this.verbose) {
/* 164 */                 log("ignoring " + entry.toString());
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 169 */             ZipEntry outEntry = new ZipEntry(entry.getName());
/* 170 */             outEntry.setMethod(entry.getMethod());
/* 171 */             outEntry.setComment(entry.getComment());
/* 172 */             outEntry.setSize(bytes.length);
/*     */ 
/* 175 */             if (outEntry.getMethod() == 0) {
/* 176 */               CRC32 crc = new CRC32();
/* 177 */               crc.update(bytes);
/* 178 */               outEntry.setCrc(crc.getValue());
/* 179 */               outEntry.setCompressedSize(bytes.length);
/*     */             }
/* 181 */             out.putNextEntry(outEntry);
/* 182 */             out.write(bytes);
/* 183 */             out.closeEntry();
/* 184 */             zip.closeEntry();
/*     */           }
/*     */ 
/* 187 */           out.close();
/*     */         } finally {
/* 189 */           fout.close();
/*     */         }
/*     */       } finally {
/* 192 */         zip.close();
/*     */       }
/*     */ 
/* 196 */       if (file.delete())
/*     */       {
/* 198 */         File newFile = new File(tempFile.getAbsolutePath());
/*     */ 
/* 200 */         if (!newFile.renameTo(file))
/* 201 */           throw new IOException("can not rename " + tempFile + " to " + file);
/*     */       }
/*     */       else
/*     */       {
/* 205 */         throw new IOException("can not delete " + file);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 210 */       tempFile.delete();
/*     */     }
/*     */   }
/*     */ 
/*     */   private byte[] process(byte[] bytes)
/*     */     throws Exception
/*     */   {
/* 224 */     ClassReader reader = new ClassReader(new ByteArrayInputStream(bytes));
/* 225 */     String[] name = ClassNameReader.getClassInfo(reader);
/* 226 */     ClassWriter w = new DebuggingClassWriter(true);
/* 227 */     ClassTransformer t = getClassTransformer(name);
/* 228 */     if (t != null) {
/* 229 */       if (this.verbose) {
/* 230 */         log("processing " + name[0]);
/*     */       }
/* 232 */       new TransformingClassGenerator(new ClassReaderGenerator(new ClassReader(new ByteArrayInputStream(bytes)), attributes(), skipDebug()), t).generateClass(w);
/*     */ 
/* 235 */       ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 236 */       out.write(w.toByteArray());
/* 237 */       return out.toByteArray();
/*     */     }
/* 239 */     return bytes;
/*     */   }
/*     */ 
/*     */   private byte[] getBytes(ZipInputStream zip)
/*     */     throws IOException
/*     */   {
/* 249 */     ByteArrayOutputStream bout = new ByteArrayOutputStream();
/* 250 */     InputStream in = new BufferedInputStream(zip);
/*     */     int b;
/* 252 */     while ((b = in.read()) != -1) {
/* 253 */       bout.write(b);
/*     */     }
/* 255 */     return bout.toByteArray();
/*     */   }
/*     */ 
/*     */   private boolean checkMagic(File file, long magic) throws IOException {
/* 259 */     DataInputStream in = new DataInputStream(new FileInputStream(file));
/*     */     try {
/* 261 */       int m = in.readInt();
/* 262 */       int i = magic == m ? 1 : 0;
/*     */       return i;
/*     */     }
/*     */     finally
/*     */     {
/* 264 */       in.close();
/* 265 */     }throw localObject;
/*     */   }
/*     */ 
/*     */   protected boolean isJarFile(File file) throws IOException {
/* 269 */     return checkMagic(file, 1347093252L);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.AbstractTransformTask
 * JD-Core Version:    0.6.0
 */