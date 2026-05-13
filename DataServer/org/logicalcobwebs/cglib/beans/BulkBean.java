/*     */ package org.logicalcobwebs.cglib.beans;
/*     */ 
/*     */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator.Source;
/*     */ import org.logicalcobwebs.cglib.core.KeyFactory;
/*     */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*     */ 
/*     */ public abstract class BulkBean
/*     */ {
/*  30 */   private static final BulkBeanKey KEY_FACTORY = (BulkBeanKey)KeyFactory.create(BulkBeanKey.class);
/*     */   protected Class target;
/*     */   protected String[] getters;
/*     */   protected String[] setters;
/*     */   protected Class[] types;
/*     */ 
/*     */   public abstract void getPropertyValues(Object paramObject, Object[] paramArrayOfObject);
/*     */ 
/*     */   public abstract void setPropertyValues(Object paramObject, Object[] paramArrayOfObject);
/*     */ 
/*     */   public Object[] getPropertyValues(Object bean)
/*     */   {
/*  47 */     Object[] values = new Object[this.getters.length];
/*  48 */     getPropertyValues(bean, values);
/*  49 */     return values;
/*     */   }
/*     */ 
/*     */   public Class[] getPropertyTypes() {
/*  53 */     return (Class[])(Class[])this.types.clone();
/*     */   }
/*     */ 
/*     */   public String[] getGetters() {
/*  57 */     return (String[])(String[])this.getters.clone();
/*     */   }
/*     */ 
/*     */   public String[] getSetters() {
/*  61 */     return (String[])(String[])this.setters.clone();
/*     */   }
/*     */ 
/*     */   public static BulkBean create(Class target, String[] getters, String[] setters, Class[] types) {
/*  65 */     Generator gen = new Generator();
/*  66 */     gen.setTarget(target);
/*  67 */     gen.setGetters(getters);
/*  68 */     gen.setSetters(setters);
/*  69 */     gen.setTypes(types);
/*  70 */     return gen.create(); } 
/*  74 */   public static class Generator extends AbstractClassGenerator { private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(BulkBean.class.getName());
/*     */     private Class target;
/*     */     private String[] getters;
/*     */     private String[] setters;
/*     */     private Class[] types;
/*     */ 
/*  81 */     public Generator() { super(); }
/*     */ 
/*     */     public void setTarget(Class target)
/*     */     {
/*  85 */       this.target = target;
/*     */     }
/*     */ 
/*     */     public void setGetters(String[] getters) {
/*  89 */       this.getters = getters;
/*     */     }
/*     */ 
/*     */     public void setSetters(String[] setters) {
/*  93 */       this.setters = setters;
/*     */     }
/*     */ 
/*     */     public void setTypes(Class[] types) {
/*  97 */       this.types = types;
/*     */     }
/*     */ 
/*     */     protected ClassLoader getDefaultClassLoader() {
/* 101 */       return this.target.getClassLoader();
/*     */     }
/*     */ 
/*     */     public BulkBean create() {
/* 105 */       setNamePrefix(this.target.getName());
/* 106 */       String targetClassName = this.target.getName();
/* 107 */       String[] typeClassNames = ReflectUtils.getNames(this.types);
/* 108 */       Object key = BulkBean.KEY_FACTORY.newInstance(targetClassName, this.getters, this.setters, typeClassNames);
/* 109 */       return (BulkBean)super.create(key);
/*     */     }
/*     */ 
/*     */     public void generateClass(ClassVisitor v) throws Exception {
/* 113 */       new BulkBeanEmitter(v, getClassName(), this.target, this.getters, this.setters, this.types);
/*     */     }
/*     */ 
/*     */     protected Object firstInstance(Class type) {
/* 117 */       BulkBean instance = (BulkBean)ReflectUtils.newInstance(type);
/* 118 */       instance.target = this.target;
/*     */ 
/* 120 */       int length = this.getters.length;
/* 121 */       instance.getters = new String[length];
/* 122 */       System.arraycopy(this.getters, 0, instance.getters, 0, length);
/*     */ 
/* 124 */       instance.setters = new String[length];
/* 125 */       System.arraycopy(this.setters, 0, instance.setters, 0, length);
/*     */ 
/* 127 */       instance.types = new Class[this.types.length];
/* 128 */       System.arraycopy(this.types, 0, instance.types, 0, this.types.length);
/*     */ 
/* 130 */       return instance;
/*     */     }
/*     */ 
/*     */     protected Object nextInstance(Object instance) {
/* 134 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface BulkBeanKey
/*     */   {
/*     */     public abstract Object newInstance(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.beans.BulkBean
 * JD-Core Version:    0.6.0
 */