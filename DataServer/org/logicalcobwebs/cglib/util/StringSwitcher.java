/*     */ package org.logicalcobwebs.cglib.util;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*     */ import org.logicalcobwebs.cglib.asm.Label;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator.Source;
/*     */ import org.logicalcobwebs.cglib.core.ClassEmitter;
/*     */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*     */ import org.logicalcobwebs.cglib.core.EmitUtils;
/*     */ import org.logicalcobwebs.cglib.core.KeyFactory;
/*     */ import org.logicalcobwebs.cglib.core.ObjectSwitchCallback;
/*     */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*     */ import org.logicalcobwebs.cglib.core.Signature;
/*     */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*     */ 
/*     */ public abstract class StringSwitcher
/*     */ {
/*  28 */   private static final Type STRING_SWITCHER = TypeUtils.parseType("org.logicalcobwebs.cglib.util.StringSwitcher");
/*     */ 
/*  30 */   private static final Signature INT_VALUE = TypeUtils.parseSignature("int intValue(String)");
/*     */ 
/*  32 */   private static final StringSwitcherKey KEY_FACTORY = (StringSwitcherKey)KeyFactory.create(StringSwitcherKey.class);
/*     */ 
/*     */   public static StringSwitcher create(String[] strings, int[] ints, boolean fixedInput)
/*     */   {
/*  49 */     Generator gen = new Generator();
/*  50 */     gen.setStrings(strings);
/*  51 */     gen.setInts(ints);
/*  52 */     gen.setFixedInput(fixedInput);
/*  53 */     return gen.create();
/*     */   }
/*     */   public abstract int intValue(String paramString);
/*     */ 
/*     */   public static class Generator extends AbstractClassGenerator {
/*  69 */     private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(StringSwitcher.class.getName());
/*     */     private String[] strings;
/*     */     private int[] ints;
/*     */     private boolean fixedInput;
/*     */ 
/*  76 */     public Generator() { super();
/*     */     }
/*     */ 
/*     */     public void setStrings(String[] strings)
/*     */     {
/*  85 */       this.strings = strings;
/*     */     }
/*     */ 
/*     */     public void setInts(int[] ints)
/*     */     {
/*  94 */       this.ints = ints;
/*     */     }
/*     */ 
/*     */     public void setFixedInput(boolean fixedInput)
/*     */     {
/* 103 */       this.fixedInput = fixedInput;
/*     */     }
/*     */ 
/*     */     protected ClassLoader getDefaultClassLoader() {
/* 107 */       return getClass().getClassLoader();
/*     */     }
/*     */ 
/*     */     public StringSwitcher create()
/*     */     {
/* 114 */       setNamePrefix(StringSwitcher.class.getName());
/* 115 */       Object key = StringSwitcher.KEY_FACTORY.newInstance(this.strings, this.ints, this.fixedInput);
/* 116 */       return (StringSwitcher)super.create(key);
/*     */     }
/*     */ 
/*     */     public void generateClass(ClassVisitor v) throws Exception {
/* 120 */       ClassEmitter ce = new ClassEmitter(v);
/* 121 */       ce.begin_class(46, 1, getClassName(), StringSwitcher.STRING_SWITCHER, null, "<generated>");
/*     */ 
/* 127 */       EmitUtils.null_constructor(ce);
/* 128 */       CodeEmitter e = ce.begin_method(1, StringSwitcher.INT_VALUE, null, null);
/* 129 */       e.load_arg(0);
/* 130 */       List stringList = Arrays.asList(this.strings);
/* 131 */       int style = this.fixedInput ? 2 : 1;
/* 132 */       EmitUtils.string_switch(e, this.strings, style, new ObjectSwitchCallback(e, stringList) {
/*     */         public void processCase(Object key, Label end) {
/* 134 */           this.val$e.push(StringSwitcher.Generator.this.ints[this.val$stringList.indexOf(key)]);
/* 135 */           this.val$e.return_value();
/*     */         }
/*     */         public void processDefault() {
/* 138 */           this.val$e.push(-1);
/* 139 */           this.val$e.return_value();
/*     */         }
/*     */       });
/* 142 */       e.end_method();
/* 143 */       ce.end_class();
/*     */     }
/*     */ 
/*     */     protected Object firstInstance(Class type) {
/* 147 */       return (StringSwitcher)ReflectUtils.newInstance(type);
/*     */     }
/*     */ 
/*     */     protected Object nextInstance(Object instance) {
/* 151 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface StringSwitcherKey
/*     */   {
/*     */     public abstract Object newInstance(String[] paramArrayOfString, int[] paramArrayOfInt, boolean paramBoolean);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.util.StringSwitcher
 * JD-Core Version:    0.6.0
 */