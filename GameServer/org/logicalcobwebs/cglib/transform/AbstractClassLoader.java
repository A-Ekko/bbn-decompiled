/*     */ package org.logicalcobwebs.cglib.transform;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.ProtectionDomain;
/*     */ import org.logicalcobwebs.cglib.asm.Attribute;
/*     */ import org.logicalcobwebs.cglib.asm.ClassReader;
/*     */ import org.logicalcobwebs.cglib.asm.ClassWriter;
/*     */ import org.logicalcobwebs.cglib.asm.attrs.Attributes;
/*     */ import org.logicalcobwebs.cglib.core.ClassGenerator;
/*     */ import org.logicalcobwebs.cglib.core.CodeGenerationException;
/*     */ import org.logicalcobwebs.cglib.core.DebuggingClassWriter;
/*     */ 
/*     */ public abstract class AbstractClassLoader extends ClassLoader
/*     */ {
/*     */   private ClassFilter filter;
/*     */   private ClassLoader classPath;
/*  36 */   private static ProtectionDomain DOMAIN = (ProtectionDomain)AccessController.doPrivileged(new PrivilegedAction()
/*     */   {
/*     */     public Object run()
/*     */     {
/*  40 */       return AbstractClassLoader.class.getProtectionDomain();
/*     */     }
/*     */   });
/*     */ 
/*     */   protected AbstractClassLoader(ClassLoader parent, ClassLoader classPath, ClassFilter filter)
/*     */   {
/*  46 */     super(parent);
/*  47 */     this.filter = filter;
/*  48 */     this.classPath = classPath;
/*     */   }
/*     */ 
/*     */   public Class loadClass(String name) throws ClassNotFoundException
/*     */   {
/*  53 */     Class loaded = findLoadedClass(name);
/*     */ 
/*  55 */     if ((loaded != null) && 
/*  56 */       (loaded.getClassLoader() == this)) {
/*  57 */       return loaded;
/*     */     }
/*     */ 
/*  61 */     if (!this.filter.accept(name))
/*  62 */       return super.loadClass(name);
/*     */     ClassReader r;
/*     */     try
/*     */     {
/*  67 */       InputStream is = this.classPath.getResourceAsStream(name.replace('.', '/') + ".class");
/*     */ 
/*  71 */       if (is == null)
/*     */       {
/*  73 */         throw new ClassNotFoundException(name);
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/*  78 */         r = new ClassReader(is);
/*     */       }
/*     */       finally
/*     */       {
/*  82 */         is.close();
/*     */       }
/*     */     }
/*     */     catch (IOException e) {
/*  86 */       throw new ClassNotFoundException(name + ":" + e.getMessage());
/*     */     }
/*     */     try
/*     */     {
/*  90 */       ClassWriter w = new DebuggingClassWriter(true);
/*  91 */       getGenerator(r).generateClass(w);
/*  92 */       byte[] b = w.toByteArray();
/*  93 */       Class c = super.defineClass(name, b, 0, b.length, DOMAIN);
/*  94 */       postProcess(c);
/*  95 */       return c;
/*     */     } catch (RuntimeException e) {
/*  97 */       throw e;
/*     */     } catch (Error e) {
/*  99 */       throw e; } catch (Exception e) {
/*     */     }
/* 101 */     throw new CodeGenerationException(e);
/*     */   }
/*     */ 
/*     */   protected ClassGenerator getGenerator(ClassReader r)
/*     */   {
/* 106 */     return new ClassReaderGenerator(r, attributes(), skipDebug());
/*     */   }
/*     */ 
/*     */   protected boolean skipDebug() {
/* 110 */     return false;
/*     */   }
/*     */ 
/*     */   protected Attribute[] attributes() {
/* 114 */     return Attributes.getDefaultAttributes();
/*     */   }
/*     */ 
/*     */   protected void postProcess(Class c)
/*     */   {
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.AbstractClassLoader
 * JD-Core Version:    0.6.0
 */