/*     */ package org.logicalcobwebs.cglib.core;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import org.logicalcobwebs.cglib.asm.ClassReader;
/*     */ import org.logicalcobwebs.cglib.asm.ClassWriter;
/*     */ import org.logicalcobwebs.cglib.asm.util.TraceClassVisitor;
/*     */ 
/*     */ public class DebuggingClassWriter extends ClassWriter
/*     */ {
/*     */   public static final String DEBUG_LOCATION_PROPERTY = "cglib.debugLocation";
/*  35 */   private static String debugLocation = System.getProperty("cglib.debugLocation");
/*     */   private static boolean traceEnabled;
/*     */   private String className;
/*     */   private String superName;
/*     */ 
/*     */   public DebuggingClassWriter(boolean computeMaxs)
/*     */   {
/*  47 */     super(computeMaxs);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public DebuggingClassWriter(boolean computeMaxs, int major, int minor)
/*     */   {
/*  54 */     super(computeMaxs);
/*     */   }
/*     */ 
/*     */   public void visit(int version, int access, String name, String superName, String[] interfaces, String sourceFile) {
/*  58 */     this.className = name.replace('/', '.');
/*  59 */     this.superName = superName.replace('/', '.');
/*  60 */     super.visit(version, access, name, superName, interfaces, sourceFile);
/*     */   }
/*     */ 
/*     */   public String getClassName() {
/*  64 */     return this.className;
/*     */   }
/*     */ 
/*     */   public String getSuperName() {
/*  68 */     return this.superName;
/*     */   }
/*     */ 
/*     */   public byte[] toByteArray()
/*     */   {
/*  73 */     return (byte[])(byte[])AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run()
/*     */       {
/*  78 */         byte[] b = DebuggingClassWriter.this.toByteArray();
/*  79 */         if (DebuggingClassWriter.debugLocation != null) {
/*  80 */           String dirs = DebuggingClassWriter.this.className.replace('.', File.separatorChar);
/*     */           try {
/*  82 */             new File(DebuggingClassWriter.debugLocation + File.separatorChar + dirs).getParentFile().mkdirs();
/*     */ 
/*  84 */             File file = new File(new File(DebuggingClassWriter.debugLocation), dirs + ".class");
/*  85 */             OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
/*     */             try {
/*  87 */               out.write(b);
/*     */             } finally {
/*  89 */               out.close();
/*     */             }
/*     */ 
/*  92 */             if (DebuggingClassWriter.traceEnabled) {
/*  93 */               file = new File(new File(DebuggingClassWriter.debugLocation), dirs + ".asm");
/*  94 */               out = new BufferedOutputStream(new FileOutputStream(file));
/*     */               try {
/*  96 */                 ClassReader cr = new ClassReader(b);
/*  97 */                 PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
/*  98 */                 TraceClassVisitor tcv = new TraceClassVisitor(null, pw);
/*  99 */                 cr.accept(tcv, false);
/* 100 */                 pw.flush();
/*     */               } finally {
/* 102 */                 out.close();
/*     */               }
/*     */             }
/*     */           } catch (IOException e) {
/* 106 */             throw new CodeGenerationException(e);
/*     */           }
/*     */         }
/* 109 */         return b;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  36 */     if (debugLocation != null) {
/*  37 */       System.err.println("CGLIB debugging enabled, writing to '" + debugLocation + "'");
/*     */       try {
/*  39 */         Class.forName("org.logicalcobwebs.cglib.asm.util.TraceClassVisitor");
/*  40 */         traceEnabled = true;
/*     */       }
/*     */       catch (Throwable ignore)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.DebuggingClassWriter
 * JD-Core Version:    0.6.0
 */