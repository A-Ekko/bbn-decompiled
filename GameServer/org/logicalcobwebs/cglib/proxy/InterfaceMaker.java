/*     */ package org.logicalcobwebs.cglib.proxy;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator.Source;
/*     */ import org.logicalcobwebs.cglib.core.ClassEmitter;
/*     */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*     */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*     */ import org.logicalcobwebs.cglib.core.Signature;
/*     */ 
/*     */ public class InterfaceMaker extends AbstractClassGenerator
/*     */ {
/*  34 */   private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(InterfaceMaker.class.getName());
/*  35 */   private Map signatures = new HashMap();
/*     */ 
/*     */   public InterfaceMaker()
/*     */   {
/*  43 */     super(SOURCE);
/*     */   }
/*     */ 
/*     */   public void add(Signature sig, Type[] exceptions)
/*     */   {
/*  52 */     this.signatures.put(sig, exceptions);
/*     */   }
/*     */ 
/*     */   public void add(Method method)
/*     */   {
/*  61 */     add(ReflectUtils.getSignature(method), ReflectUtils.getExceptionTypes(method));
/*     */   }
/*     */ 
/*     */   public void add(Class clazz)
/*     */   {
/*  72 */     Method[] methods = clazz.getMethods();
/*  73 */     for (int i = 0; i < methods.length; i++) {
/*  74 */       Method m = methods[i];
/*  75 */       if (!m.getDeclaringClass().getName().equals("java.lang.Object"))
/*  76 */         add(m);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Class create()
/*     */   {
/*  85 */     setUseCache(false);
/*  86 */     return (Class)super.create(this);
/*     */   }
/*     */ 
/*     */   protected ClassLoader getDefaultClassLoader() {
/*  90 */     return null;
/*     */   }
/*     */ 
/*     */   protected Object firstInstance(Class type) {
/*  94 */     return type;
/*     */   }
/*     */ 
/*     */   protected Object nextInstance(Object instance) {
/*  98 */     throw new IllegalStateException("InterfaceMaker does not cache");
/*     */   }
/*     */ 
/*     */   public void generateClass(ClassVisitor v) throws Exception {
/* 102 */     ClassEmitter ce = new ClassEmitter(v);
/* 103 */     ce.begin_class(46, 513, getClassName(), null, null, "<generated>");
/*     */ 
/* 109 */     for (Iterator it = this.signatures.keySet().iterator(); it.hasNext(); ) {
/* 110 */       Signature sig = (Signature)it.next();
/* 111 */       Type[] exceptions = (Type[])(Type[])this.signatures.get(sig);
/* 112 */       ce.begin_method(1025, sig, exceptions, null).end_method();
/*     */     }
/*     */ 
/* 117 */     ce.end_class();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.InterfaceMaker
 * JD-Core Version:    0.6.0
 */