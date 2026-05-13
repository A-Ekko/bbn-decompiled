/*     */ package org.logicalcobwebs.cglib.beans;
/*     */ 
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator.Source;
/*     */ import org.logicalcobwebs.cglib.core.ClassEmitter;
/*     */ import org.logicalcobwebs.cglib.core.Constants;
/*     */ import org.logicalcobwebs.cglib.core.EmitUtils;
/*     */ import org.logicalcobwebs.cglib.core.KeyFactory;
/*     */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*     */ 
/*     */ public class BeanGenerator extends AbstractClassGenerator
/*     */ {
/*  29 */   private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(BeanGenerator.class.getName());
/*  30 */   private static final BeanGeneratorKey KEY_FACTORY = (BeanGeneratorKey)KeyFactory.create(BeanGeneratorKey.class);
/*     */   private Class superclass;
/*  38 */   private Map props = new HashMap();
/*     */   private boolean classOnly;
/*     */ 
/*     */   public BeanGenerator()
/*     */   {
/*  42 */     super(SOURCE);
/*     */   }
/*     */ 
/*     */   public void setSuperclass(Class superclass)
/*     */   {
/*  52 */     if ((superclass != null) && (superclass.equals(Object.class))) {
/*  53 */       superclass = null;
/*     */     }
/*  55 */     this.superclass = superclass;
/*     */   }
/*     */ 
/*     */   public void addProperty(String name, Class type) {
/*  59 */     if (this.props.containsKey(name)) {
/*  60 */       throw new IllegalArgumentException("Duplicate property name \"" + name + "\"");
/*     */     }
/*  62 */     this.props.put(name, Type.getType(type));
/*     */   }
/*     */ 
/*     */   protected ClassLoader getDefaultClassLoader() {
/*  66 */     if (this.superclass != null) {
/*  67 */       return this.superclass.getClassLoader();
/*     */     }
/*  69 */     return null;
/*     */   }
/*     */ 
/*     */   public Object create()
/*     */   {
/*  74 */     this.classOnly = false;
/*  75 */     return createHelper();
/*     */   }
/*     */ 
/*     */   public Object createClass() {
/*  79 */     this.classOnly = true;
/*  80 */     return createHelper();
/*     */   }
/*     */ 
/*     */   private Object createHelper() {
/*  84 */     if (this.superclass != null) {
/*  85 */       setNamePrefix(this.superclass.getName());
/*     */     }
/*  87 */     String superName = this.superclass != null ? this.superclass.getName() : "java.lang.Object";
/*  88 */     Object key = KEY_FACTORY.newInstance(superName, this.props);
/*  89 */     return super.create(key);
/*     */   }
/*     */ 
/*     */   public void generateClass(ClassVisitor v) throws Exception {
/*  93 */     int size = this.props.size();
/*  94 */     String[] names = (String[])(String[])this.props.keySet().toArray(new String[size]);
/*  95 */     Type[] types = new Type[size];
/*  96 */     for (int i = 0; i < size; i++) {
/*  97 */       types[i] = ((Type)this.props.get(names[i]));
/*     */     }
/*  99 */     ClassEmitter ce = new ClassEmitter(v);
/* 100 */     ce.begin_class(46, 1, getClassName(), this.superclass != null ? Type.getType(this.superclass) : Constants.TYPE_OBJECT, null, null);
/*     */ 
/* 106 */     EmitUtils.null_constructor(ce);
/* 107 */     EmitUtils.add_properties(ce, names, types);
/* 108 */     ce.end_class();
/*     */   }
/*     */ 
/*     */   protected Object firstInstance(Class type) {
/* 112 */     if (this.classOnly) {
/* 113 */       return type;
/*     */     }
/* 115 */     return ReflectUtils.newInstance(type);
/*     */   }
/*     */ 
/*     */   protected Object nextInstance(Object instance)
/*     */   {
/* 120 */     Class protoclass = (instance instanceof Class) ? (Class)instance : instance.getClass();
/* 121 */     if (this.classOnly) {
/* 122 */       return protoclass;
/*     */     }
/* 124 */     return ReflectUtils.newInstance(protoclass);
/*     */   }
/*     */ 
/*     */   public static void addProperties(BeanGenerator gen, Map props)
/*     */   {
/* 129 */     for (Iterator it = props.keySet().iterator(); it.hasNext(); ) {
/* 130 */       String name = (String)it.next();
/* 131 */       gen.addProperty(name, (Class)props.get(name));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void addProperties(BeanGenerator gen, Class type) {
/* 136 */     addProperties(gen, ReflectUtils.getBeanProperties(type));
/*     */   }
/*     */ 
/*     */   public static void addProperties(BeanGenerator gen, PropertyDescriptor[] descriptors) {
/* 140 */     for (int i = 0; i < descriptors.length; i++)
/* 141 */       gen.addProperty(descriptors[i].getName(), descriptors[i].getPropertyType());
/*     */   }
/*     */ 
/*     */   static abstract interface BeanGeneratorKey
/*     */   {
/*     */     public abstract Object newInstance(String paramString, Map paramMap);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.beans.BeanGenerator
 * JD-Core Version:    0.6.0
 */