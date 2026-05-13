/*     */ package org.logicalcobwebs.cglib.transform.hook;
/*     */ 
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import org.codehaus.aspectwerkz.hook.ClassLoaderPatcher;
/*     */ import org.codehaus.aspectwerkz.hook.ClassLoaderPreProcessor;
/*     */ import org.logicalcobwebs.cglib.asm.Attribute;
/*     */ import org.logicalcobwebs.cglib.asm.ClassReader;
/*     */ import org.logicalcobwebs.cglib.asm.ClassWriter;
/*     */ import org.logicalcobwebs.cglib.asm.CodeVisitor;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ import org.logicalcobwebs.cglib.core.RemappingCodeVisitor;
/*     */ 
/*     */ public class AsmClassLoaderPreProcessor
/*     */   implements ClassLoaderPreProcessor
/*     */ {
/*     */   private static final String DESC_CORE = "Ljava/lang/String;[BIILjava/security/ProtectionDomain;";
/*     */   private static final String DESC_PREFIX = "(Ljava/lang/String;[BIILjava/security/ProtectionDomain;";
/*     */   private static final String DESC_HELPER = "(Ljava/lang/ClassLoader;Ljava/lang/String;[BIILjava/security/ProtectionDomain;)[B";
/*     */ 
/*     */   public byte[] preProcess(byte[] b)
/*     */   {
/*     */     try
/*     */     {
/*  40 */       ClassWriter w = new ClassWriter(true) { private boolean flag;
/*     */ 
/*  43 */         public void visit(int version, int access, String name, String superName, String[] interfaces, String sourceFile) { super.visit(version, access, name, superName, interfaces, sourceFile);
/*  44 */           this.flag = name.equals("java/lang/ClassLoader"); }
/*     */ 
/*     */         public CodeVisitor visitMethod(int access, String name, String desc, String[] exceptions, Attribute attrs) {
/*  47 */           CodeVisitor v = super.visitMethod(access, name, desc, exceptions, attrs);
/*  48 */           if (this.flag) {
/*  49 */             v = new AsmClassLoaderPreProcessor.PreProcessingVisitor(v, access, desc);
/*     */           }
/*  51 */           return v;
/*     */         }
/*     */       };
/*  54 */       new ClassReader(b).accept(w, false);
/*  55 */       return w.toByteArray();
/*     */     } catch (Exception e) {
/*  57 */       System.err.println("failed to patch ClassLoader:");
/*  58 */       e.printStackTrace();
/*  59 */     }return b;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/* 103 */     ClassLoaderPreProcessor me = new AsmClassLoaderPreProcessor();
/* 104 */     InputStream is = ClassLoader.getSystemClassLoader().getParent().getResourceAsStream("java/lang/ClassLoader.class");
/* 105 */     byte[] out = me.preProcess(ClassLoaderPatcher.inputStreamToByteArray(is));
/* 106 */     is.close();
/* 107 */     OutputStream os = new FileOutputStream("_boot/java/lang/ClassLoader.class");
/* 108 */     os.write(out);
/* 109 */     os.close();
/*     */   }
/*     */ 
/*     */   private static class PreProcessingVisitor extends RemappingCodeVisitor
/*     */   {
/*     */     public PreProcessingVisitor(CodeVisitor v, int access, String desc)
/*     */     {
/*  65 */       super(access, Type.getArgumentTypes(desc));
/*     */     }
/*     */ 
/*     */     public void visitMethodInsn(int opcode, String owner, String name, String desc) {
/*  69 */       if (("defineClass0".equals(name)) && ("java/lang/ClassLoader".equals(owner))) {
/*  70 */         Type[] args = Type.getArgumentTypes(desc);
/*  71 */         if ((args.length < 5) || (!desc.startsWith("(Ljava/lang/String;[BIILjava/security/ProtectionDomain;"))) {
/*  72 */           throw new Error("non standard JDK, native call not supported: " + desc);
/*     */         }
/*  74 */         int[] locals = new int[args.length];
/*  75 */         for (int i = args.length - 1; i >= 0; i--) {
/*  76 */           this.cv.visitVarInsn(args[i].getOpcode(54), locals[i] = nextLocal(args[i].getSize()));
/*     */         }
/*     */ 
/*  79 */         for (int i = 0; i < 5; i++) {
/*  80 */           this.cv.visitVarInsn(args[i].getOpcode(21), locals[i]);
/*     */         }
/*  82 */         super.visitMethodInsn(184, "org/codehaus/aspectwerkz/hook/impl/ClassPreProcessorHelper", "defineClass0Pre", "(Ljava/lang/ClassLoader;Ljava/lang/String;[BIILjava/security/ProtectionDomain;)[B");
/*     */ 
/*  86 */         this.cv.visitVarInsn(58, locals[1]);
/*  87 */         this.cv.visitVarInsn(25, 0);
/*  88 */         this.cv.visitVarInsn(25, locals[0]);
/*  89 */         this.cv.visitVarInsn(25, locals[1]);
/*  90 */         this.cv.visitInsn(3);
/*  91 */         this.cv.visitVarInsn(25, locals[1]);
/*  92 */         this.cv.visitInsn(190);
/*  93 */         this.cv.visitVarInsn(25, locals[4]);
/*  94 */         for (int i = 5; i < args.length; i++) {
/*  95 */           this.cv.visitVarInsn(args[i].getOpcode(21), locals[i]);
/*     */         }
/*     */       }
/*  98 */       super.visitMethodInsn(opcode, owner, name, desc);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.hook.AsmClassLoaderPreProcessor
 * JD-Core Version:    0.6.0
 */