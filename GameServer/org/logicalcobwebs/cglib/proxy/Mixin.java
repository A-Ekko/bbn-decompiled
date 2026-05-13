/*     */ package org.logicalcobwebs.cglib.proxy;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator.Source;
/*     */ import org.logicalcobwebs.cglib.core.ClassesKey;
/*     */ import org.logicalcobwebs.cglib.core.KeyFactory;
/*     */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*     */ 
/*     */ public abstract class Mixin
/*     */ {
/*  35 */   private static final MixinKey KEY_FACTORY = (MixinKey)KeyFactory.create(MixinKey.class, KeyFactory.CLASS_BY_NAME);
/*     */ 
/*  37 */   private static final Map ROUTE_CACHE = Collections.synchronizedMap(new HashMap());
/*     */   public static final int STYLE_INTERFACES = 0;
/*     */   public static final int STYLE_BEANS = 1;
/*     */   public static final int STYLE_EVERYTHING = 2;
/*     */ 
/*     */   public abstract Mixin newInstance(Object[] paramArrayOfObject);
/*     */ 
/*     */   public static Mixin create(Object[] delegates)
/*     */   {
/*  56 */     Generator gen = new Generator();
/*  57 */     gen.setDelegates(delegates);
/*  58 */     return gen.create();
/*     */   }
/*     */ 
/*     */   public static Mixin create(Class[] interfaces, Object[] delegates)
/*     */   {
/*  68 */     Generator gen = new Generator();
/*  69 */     gen.setClasses(interfaces);
/*  70 */     gen.setDelegates(delegates);
/*  71 */     return gen.create();
/*     */   }
/*     */ 
/*     */   public static Mixin createBean(Object[] beans)
/*     */   {
/*  77 */     return createBean(null, beans);
/*     */   }
/*     */ 
/*     */   public static Mixin createBean(ClassLoader loader, Object[] beans)
/*     */   {
/*  87 */     Generator gen = new Generator();
/*  88 */     gen.setStyle(1);
/*  89 */     gen.setDelegates(beans);
/*  90 */     gen.setClassLoader(loader);
/*  91 */     return gen.create();
/*     */   }
/*     */ 
/*     */   public static Class[] getClasses(Object[] delegates)
/*     */   {
/* 191 */     return (Class[])(Class[])route(delegates).classes.clone();
/*     */   }
/*     */ 
/*     */   private static Route route(Object[] delegates)
/*     */   {
/* 199 */     Object key = ClassesKey.create(delegates);
/* 200 */     Route route = (Route)ROUTE_CACHE.get(key);
/* 201 */     if (route == null) {
/* 202 */       ROUTE_CACHE.put(key, route = new Route(delegates));
/*     */     }
/* 204 */     return route;
/*     */   }
/*     */   private static class Route {
/*     */     private Class[] classes;
/*     */     private int[] route;
/*     */ 
/*     */     Route(Object[] delegates) {
/* 213 */       Map map = new HashMap();
/* 214 */       ArrayList collect = new ArrayList();
/*     */       Iterator it;
/* 215 */       for (int i = 0; i < delegates.length; i++) {
/* 216 */         Class delegate = delegates[i].getClass();
/* 217 */         collect.clear();
/* 218 */         ReflectUtils.addAllInterfaces(delegate, collect);
/* 219 */         for (it = collect.iterator(); it.hasNext(); ) {
/* 220 */           Class iface = (Class)it.next();
/* 221 */           if (!map.containsKey(iface)) {
/* 222 */             map.put(iface, new Integer(i));
/*     */           }
/*     */         }
/*     */       }
/* 226 */       this.classes = new Class[map.size()];
/* 227 */       this.route = new int[map.size()];
/* 228 */       int index = 0;
/* 229 */       for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
/* 230 */         Class key = (Class)it.next();
/* 231 */         this.classes[index] = key;
/* 232 */         this.route[index] = ((Integer)map.get(key)).intValue();
/* 233 */         index++;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Generator extends AbstractClassGenerator
/*     */   {
/*  95 */     private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(Mixin.class.getName());
/*     */     private Class[] classes;
/*     */     private Object[] delegates;
/*  99 */     private int style = 0;
/*     */     private int[] route;
/*     */ 
/*     */     public Generator()
/*     */     {
/* 104 */       super();
/*     */     }
/*     */ 
/*     */     protected ClassLoader getDefaultClassLoader() {
/* 108 */       return this.classes[0].getClassLoader();
/*     */     }
/*     */ 
/*     */     public void setStyle(int style) {
/* 112 */       switch (style) {
/*     */       case 0:
/*     */       case 1:
/*     */       case 2:
/* 116 */         this.style = style;
/* 117 */         break;
/*     */       default:
/* 119 */         throw new IllegalArgumentException("Unknown mixin style: " + style);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void setClasses(Class[] classes) {
/* 124 */       this.classes = classes;
/*     */     }
/*     */ 
/*     */     public void setDelegates(Object[] delegates) {
/* 128 */       this.delegates = delegates;
/*     */     }
/*     */ 
/*     */     public Mixin create() {
/* 132 */       if ((this.classes == null) && (this.delegates == null)) {
/* 133 */         throw new IllegalStateException("Either classes or delegates must be set");
/*     */       }
/* 135 */       switch (this.style) {
/*     */       case 0:
/* 137 */         if (this.classes != null) break;
/* 138 */         Mixin.Route r = Mixin.access$000(this.delegates);
/* 139 */         this.classes = r.classes;
/* 140 */         this.route = r.route;
/* 141 */         break;
/*     */       case 1:
/*     */       case 2:
/* 146 */         if (this.classes == null) {
/* 147 */           this.classes = ReflectUtils.getClasses(this.delegates);
/*     */         } else {
/* 149 */           if (this.delegates == null) break;
/* 150 */           Class[] temp = ReflectUtils.getClasses(this.delegates);
/* 151 */           if (this.classes.length != temp.length) {
/* 152 */             throw new IllegalStateException("Specified classes are incompatible with delegates");
/*     */           }
/* 154 */           for (int i = 0; i < this.classes.length; i++) {
/* 155 */             if (!this.classes[i].isAssignableFrom(temp[i])) {
/* 156 */               throw new IllegalStateException("Specified class " + this.classes[i] + " is incompatible with delegate class " + temp[i] + " (index " + i + ")");
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 162 */       setNamePrefix(this.classes[ReflectUtils.findPackageProtected(this.classes)].getName());
/*     */ 
/* 164 */       return (Mixin)super.create(Mixin.KEY_FACTORY.newInstance(this.style, ReflectUtils.getNames(this.classes), this.route));
/*     */     }
/*     */ 
/*     */     public void generateClass(ClassVisitor v) {
/* 168 */       switch (this.style) {
/*     */       case 0:
/* 170 */         new MixinEmitter(v, getClassName(), this.classes, this.route);
/* 171 */         break;
/*     */       case 1:
/* 173 */         new MixinBeanEmitter(v, getClassName(), this.classes);
/* 174 */         break;
/*     */       case 2:
/* 176 */         new MixinEverythingEmitter(v, getClassName(), this.classes);
/*     */       }
/*     */     }
/*     */ 
/*     */     protected Object firstInstance(Class type)
/*     */     {
/* 182 */       return ((Mixin)ReflectUtils.newInstance(type)).newInstance(this.delegates);
/*     */     }
/*     */ 
/*     */     protected Object nextInstance(Object instance) {
/* 186 */       return ((Mixin)instance).newInstance(this.delegates);
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface MixinKey
/*     */   {
/*     */     public abstract Object newInstance(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.Mixin
 * JD-Core Version:    0.6.0
 */