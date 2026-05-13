/*     */ package org.logicalcobwebs.cglib.core;
/*     */ 
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.ProtectionDomain;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.logicalcobwebs.cglib.asm.Attribute;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ 
/*     */ public class ReflectUtils
/*     */ {
/*  33 */   private static final Map primitives = new HashMap(8);
/*  34 */   private static final Map transforms = new HashMap(8);
/*  35 */   private static final ClassLoader defaultLoader = ReflectUtils.class.getClassLoader();
/*     */   private static Method DEFINE_CLASS;
/*  40 */   private static final ProtectionDomain PROTECTION_DOMAIN = (ProtectionDomain)AccessController.doPrivileged(new PrivilegedAction() {
/*     */     public Object run() {
/*  42 */       return ReflectUtils.class.getProtectionDomain();
/*     */     }
/*     */   });
/*     */   private static final String[] CGLIB_PACKAGES;
/*     */ 
/*     */   public static Type[] getExceptionTypes(Member member)
/*     */   {
/*  92 */     if ((member instanceof Method))
/*  93 */       return TypeUtils.getTypes(((Method)member).getExceptionTypes());
/*  94 */     if ((member instanceof Constructor)) {
/*  95 */       return TypeUtils.getTypes(((Constructor)member).getExceptionTypes());
/*     */     }
/*  97 */     throw new IllegalArgumentException("Cannot get exception types of a field");
/*     */   }
/*     */ 
/*     */   public static Signature getSignature(Member member)
/*     */   {
/* 102 */     if ((member instanceof Method))
/* 103 */       return new Signature(member.getName(), Type.getMethodDescriptor((Method)member));
/* 104 */     if ((member instanceof Constructor)) {
/* 105 */       Type[] types = TypeUtils.getTypes(((Constructor)member).getParameterTypes());
/* 106 */       return new Signature("<init>", Type.getMethodDescriptor(Type.VOID_TYPE, types));
/*     */     }
/*     */ 
/* 110 */     throw new IllegalArgumentException("Cannot get signature of a field");
/*     */   }
/*     */ 
/*     */   public static Constructor findConstructor(String desc)
/*     */   {
/* 115 */     return findConstructor(desc, defaultLoader);
/*     */   }
/*     */ 
/*     */   public static Constructor findConstructor(String desc, ClassLoader loader) {
/*     */     try {
/* 120 */       int lparen = desc.indexOf('(');
/* 121 */       String className = desc.substring(0, lparen).trim();
/* 122 */       return getClass(className, loader).getConstructor(parseTypes(desc, loader));
/*     */     } catch (ClassNotFoundException e) {
/* 124 */       throw new CodeGenerationException(e); } catch (NoSuchMethodException e) {
/*     */     }
/* 126 */     throw new CodeGenerationException(e);
/*     */   }
/*     */ 
/*     */   public static Method findMethod(String desc)
/*     */   {
/* 131 */     return findMethod(desc, defaultLoader);
/*     */   }
/*     */ 
/*     */   public static Method findMethod(String desc, ClassLoader loader) {
/*     */     try {
/* 136 */       int lparen = desc.indexOf('(');
/* 137 */       int dot = desc.lastIndexOf('.', lparen);
/* 138 */       String className = desc.substring(0, dot).trim();
/* 139 */       String methodName = desc.substring(dot + 1, lparen).trim();
/* 140 */       return getClass(className, loader).getDeclaredMethod(methodName, parseTypes(desc, loader));
/*     */     } catch (ClassNotFoundException e) {
/* 142 */       throw new CodeGenerationException(e); } catch (NoSuchMethodException e) {
/*     */     }
/* 144 */     throw new CodeGenerationException(e);
/*     */   }
/*     */ 
/*     */   private static Class[] parseTypes(String desc, ClassLoader loader) throws ClassNotFoundException
/*     */   {
/* 149 */     int lparen = desc.indexOf('(');
/* 150 */     int rparen = desc.indexOf(')', lparen);
/* 151 */     List params = new ArrayList();
/* 152 */     int start = lparen + 1;
/*     */     while (true) {
/* 154 */       int comma = desc.indexOf(',', start);
/* 155 */       if (comma < 0) {
/*     */         break;
/*     */       }
/* 158 */       params.add(desc.substring(start, comma).trim());
/* 159 */       start = comma + 1;
/*     */     }
/* 161 */     if (start < rparen) {
/* 162 */       params.add(desc.substring(start, rparen).trim());
/*     */     }
/* 164 */     Class[] types = new Class[params.size()];
/* 165 */     for (int i = 0; i < types.length; i++) {
/* 166 */       types[i] = getClass((String)params.get(i), loader);
/*     */     }
/* 168 */     return types;
/*     */   }
/*     */ 
/*     */   private static Class getClass(String className, ClassLoader loader) throws ClassNotFoundException {
/* 172 */     return getClass(className, loader, CGLIB_PACKAGES);
/*     */   }
/*     */ 
/*     */   private static Class getClass(String className, ClassLoader loader, String[] packages) throws ClassNotFoundException {
/* 176 */     String save = className;
/* 177 */     int dimensions = 0;
/* 178 */     int index = 0;
/* 179 */     while ((index = className.indexOf("[]", index) + 1) > 0) {
/* 180 */       dimensions++;
/*     */     }
/* 182 */     StringBuffer brackets = new StringBuffer(className.length() - dimensions);
/* 183 */     for (int i = 0; i < dimensions; i++) {
/* 184 */       brackets.append('[');
/*     */     }
/* 186 */     className = className.substring(0, className.length() - 2 * dimensions);
/*     */ 
/* 188 */     String prefix = dimensions > 0 ? brackets + "L" : "";
/* 189 */     String suffix = dimensions > 0 ? ";" : "";
/*     */     try {
/* 191 */       return Class.forName(prefix + className + suffix, false, loader);
/*     */     } catch (ClassNotFoundException transform) {
/* 193 */       for (int i = 0; i < packages.length; i++)
/*     */         try {
/* 195 */           return Class.forName(prefix + packages[i] + '.' + className + suffix, false, loader);
/*     */         } catch (ClassNotFoundException ignore) {
/*     */         }
/* 198 */       if (dimensions == 0) {
/* 199 */         Class c = (Class)primitives.get(className);
/* 200 */         if (c != null)
/* 201 */           return c;
/*     */       }
/*     */       else {
/* 204 */         String transform = (String)transforms.get(className);
/* 205 */         if (transform != null)
/*     */           try {
/* 207 */             return Class.forName(brackets + transform, false, loader); } catch (ClassNotFoundException ignore) {
/*     */           }
/*     */       }
/*     */     }
/* 211 */     throw new ClassNotFoundException(save);
/*     */   }
/*     */ 
/*     */   public static Object newInstance(Class type)
/*     */   {
/* 216 */     return newInstance(type, Constants.EMPTY_CLASS_ARRAY, null);
/*     */   }
/*     */ 
/*     */   public static Object newInstance(Class type, Class[] parameterTypes, Object[] args) {
/* 220 */     return newInstance(getConstructor(type, parameterTypes), args);
/*     */   }
/*     */ 
/*     */   public static Object newInstance(Constructor cstruct, Object[] args)
/*     */   {
/* 225 */     boolean flag = cstruct.isAccessible();
/*     */     try {
/* 227 */       cstruct.setAccessible(true);
/* 228 */       Object result = cstruct.newInstance(args);
/* 229 */       Object localObject1 = result;
/*     */       return localObject1;
/*     */     }
/*     */     catch (InstantiationException e)
/*     */     {
/* 231 */       throw new CodeGenerationException(e);
/*     */     } catch (IllegalAccessException e) {
/* 233 */       throw new CodeGenerationException(e);
/*     */     } catch (InvocationTargetException e) {
/* 235 */       throw new CodeGenerationException(e.getTargetException());
/*     */     } finally {
/* 237 */       cstruct.setAccessible(flag);
/* 238 */     }throw localObject2;
/*     */   }
/*     */ 
/*     */   public static Constructor getConstructor(Class type, Class[] parameterTypes)
/*     */   {
/*     */     try {
/* 244 */       Constructor constructor = type.getDeclaredConstructor(parameterTypes);
/* 245 */       constructor.setAccessible(true);
/* 246 */       return constructor; } catch (NoSuchMethodException e) {
/*     */     }
/* 248 */     throw new CodeGenerationException(e);
/*     */   }
/*     */ 
/*     */   public static String[] getNames(Class[] classes)
/*     */   {
/* 254 */     if (classes == null)
/* 255 */       return null;
/* 256 */     String[] names = new String[classes.length];
/* 257 */     for (int i = 0; i < names.length; i++) {
/* 258 */       names[i] = classes[i].getName();
/*     */     }
/* 260 */     return names;
/*     */   }
/*     */ 
/*     */   public static Class[] getClasses(Object[] objects) {
/* 264 */     Class[] classes = new Class[objects.length];
/* 265 */     for (int i = 0; i < objects.length; i++) {
/* 266 */       classes[i] = objects[i].getClass();
/*     */     }
/* 268 */     return classes;
/*     */   }
/*     */ 
/*     */   public static Method findNewInstance(Class iface) {
/* 272 */     Method m = findInterfaceMethod(iface);
/* 273 */     if (!m.getName().equals("newInstance")) {
/* 274 */       throw new IllegalArgumentException(iface + " missing newInstance method");
/*     */     }
/* 276 */     return m;
/*     */   }
/*     */ 
/*     */   public static Method[] getPropertyMethods(PropertyDescriptor[] properties, boolean read, boolean write) {
/* 280 */     Set methods = new HashSet();
/* 281 */     for (int i = 0; i < properties.length; i++) {
/* 282 */       PropertyDescriptor pd = properties[i];
/* 283 */       if (read) {
/* 284 */         methods.add(pd.getReadMethod());
/*     */       }
/* 286 */       if (write) {
/* 287 */         methods.add(pd.getWriteMethod());
/*     */       }
/*     */     }
/* 290 */     methods.remove(null);
/* 291 */     return (Method[])(Method[])methods.toArray(new Method[methods.size()]);
/*     */   }
/*     */ 
/*     */   public static PropertyDescriptor[] getBeanProperties(Class type) {
/* 295 */     return getPropertiesHelper(type, true, true);
/*     */   }
/*     */ 
/*     */   public static PropertyDescriptor[] getBeanGetters(Class type) {
/* 299 */     return getPropertiesHelper(type, true, false);
/*     */   }
/*     */ 
/*     */   public static PropertyDescriptor[] getBeanSetters(Class type) {
/* 303 */     return getPropertiesHelper(type, false, true);
/*     */   }
/*     */ 
/*     */   private static PropertyDescriptor[] getPropertiesHelper(Class type, boolean read, boolean write) {
/*     */     try {
/* 308 */       BeanInfo info = Introspector.getBeanInfo(type, Object.class);
/* 309 */       PropertyDescriptor[] all = info.getPropertyDescriptors();
/* 310 */       if ((read) && (write)) {
/* 311 */         return all;
/*     */       }
/* 313 */       List properties = new ArrayList(all.length);
/* 314 */       for (int i = 0; i < all.length; i++) {
/* 315 */         PropertyDescriptor pd = all[i];
/* 316 */         if (((!read) || (pd.getReadMethod() == null)) && ((!write) || (pd.getWriteMethod() == null)))
/*     */           continue;
/* 318 */         properties.add(pd);
/*     */       }
/*     */ 
/* 321 */       return (PropertyDescriptor[])(PropertyDescriptor[])properties.toArray(new PropertyDescriptor[properties.size()]); } catch (IntrospectionException e) {
/*     */     }
/* 323 */     throw new CodeGenerationException(e);
/*     */   }
/*     */ 
/*     */   public static Method findDeclaredMethod(Class type, String methodName, Class[] parameterTypes)
/*     */     throws NoSuchMethodException
/*     */   {
/* 333 */     Class cl = type;
/* 334 */     while (cl != null) {
/*     */       try {
/* 336 */         return cl.getDeclaredMethod(methodName, parameterTypes);
/*     */       } catch (NoSuchMethodException e) {
/* 338 */         cl = cl.getSuperclass();
/*     */       }
/*     */     }
/* 341 */     throw new NoSuchMethodException(methodName);
/*     */   }
/*     */ 
/*     */   public static List addAllMethods(Class type, List list)
/*     */   {
/* 348 */     list.addAll(Arrays.asList(type.getDeclaredMethods()));
/* 349 */     Class superclass = type.getSuperclass();
/* 350 */     if (superclass != null) {
/* 351 */       addAllMethods(superclass, list);
/*     */     }
/* 353 */     Class[] interfaces = type.getInterfaces();
/* 354 */     for (int i = 0; i < interfaces.length; i++) {
/* 355 */       addAllMethods(interfaces[i], list);
/*     */     }
/*     */ 
/* 358 */     return list;
/*     */   }
/*     */ 
/*     */   public static List addAllInterfaces(Class type, List list) {
/* 362 */     Class superclass = type.getSuperclass();
/* 363 */     if (superclass != null) {
/* 364 */       list.addAll(Arrays.asList(type.getInterfaces()));
/* 365 */       addAllInterfaces(superclass, list);
/*     */     }
/* 367 */     return list;
/*     */   }
/*     */ 
/*     */   public static Method findInterfaceMethod(Class iface)
/*     */   {
/* 372 */     if (!iface.isInterface()) {
/* 373 */       throw new IllegalArgumentException(iface + " is not an interface");
/*     */     }
/* 375 */     Method[] methods = iface.getDeclaredMethods();
/* 376 */     if (methods.length != 1) {
/* 377 */       throw new IllegalArgumentException("expecting exactly 1 method in " + iface);
/*     */     }
/* 379 */     return methods[0];
/*     */   }
/*     */ 
/*     */   public static Class defineClass(String className, byte[] b, ClassLoader loader) throws Exception {
/* 383 */     Object[] args = { className, b, new Integer(0), new Integer(b.length), PROTECTION_DOMAIN };
/* 384 */     return (Class)DEFINE_CLASS.invoke(loader, args);
/*     */   }
/*     */ 
/*     */   public static int findPackageProtected(Class[] classes) {
/* 388 */     for (int i = 0; i < classes.length; i++) {
/* 389 */       if (!Modifier.isPublic(classes[i].getModifiers())) {
/* 390 */         return i;
/*     */       }
/*     */     }
/* 393 */     return 0;
/*     */   }
/*     */ 
/*     */   public static MethodInfo getMethodInfo(Member member, int modifiers) {
/* 397 */     Signature sig = getSignature(member);
/* 398 */     return new MethodInfo(member, modifiers, sig) { private ClassInfo ci;
/*     */ 
/* 401 */       public ClassInfo getClassInfo() { if (this.ci == null)
/* 402 */           this.ci = ReflectUtils.getClassInfo(this.val$member.getDeclaringClass());
/* 403 */         return this.ci; }
/*     */ 
/*     */       public int getModifiers() {
/* 406 */         return this.val$modifiers;
/*     */       }
/*     */       public Signature getSignature() {
/* 409 */         return this.val$sig;
/*     */       }
/*     */       public Type[] getExceptionTypes() {
/* 412 */         return ReflectUtils.getExceptionTypes(this.val$member);
/*     */       }
/*     */       public Attribute getAttribute() {
/* 415 */         return null;
/*     */       } } ;
/*     */   }
/*     */ 
/*     */   public static MethodInfo getMethodInfo(Member member) {
/* 421 */     return getMethodInfo(member, member.getModifiers());
/*     */   }
/*     */ 
/*     */   public static ClassInfo getClassInfo(Class clazz) {
/* 425 */     Type type = Type.getType(clazz);
/* 426 */     Type sc = clazz.getSuperclass() == null ? null : Type.getType(clazz.getSuperclass());
/* 427 */     return new ClassInfo(type, sc, clazz) {
/*     */       public Type getType() {
/* 429 */         return this.val$type;
/*     */       }
/*     */       public Type getSuperType() {
/* 432 */         return this.val$sc;
/*     */       }
/*     */       public Type[] getInterfaces() {
/* 435 */         return TypeUtils.getTypes(this.val$clazz.getInterfaces());
/*     */       }
/*     */       public int getModifiers() {
/* 438 */         return this.val$clazz.getModifiers();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  46 */     AccessController.doPrivileged(new PrivilegedAction() {
/*     */       public Object run() {
/*     */         try {
/*  49 */           Class loader = Class.forName("java.lang.ClassLoader");
/*  50 */           ReflectUtils.access$002(loader.getDeclaredMethod("defineClass", new Class[] { String.class, new byte[0].getClass(), Integer.TYPE, Integer.TYPE, ProtectionDomain.class }));
/*     */ 
/*  56 */           ReflectUtils.DEFINE_CLASS.setAccessible(true);
/*     */         } catch (ClassNotFoundException e) {
/*  58 */           throw new CodeGenerationException(e);
/*     */         } catch (NoSuchMethodException e) {
/*  60 */           throw new CodeGenerationException(e);
/*     */         }
/*  62 */         return null;
/*     */       }
/*     */     });
/*  67 */     CGLIB_PACKAGES = new String[] { "java.lang" };
/*     */ 
/*  72 */     primitives.put("byte", Byte.TYPE);
/*  73 */     primitives.put("char", Character.TYPE);
/*  74 */     primitives.put("double", Double.TYPE);
/*  75 */     primitives.put("float", Float.TYPE);
/*  76 */     primitives.put("int", Integer.TYPE);
/*  77 */     primitives.put("long", Long.TYPE);
/*  78 */     primitives.put("short", Short.TYPE);
/*  79 */     primitives.put("boolean", Boolean.TYPE);
/*     */ 
/*  81 */     transforms.put("byte", "B");
/*  82 */     transforms.put("char", "C");
/*  83 */     transforms.put("double", "D");
/*  84 */     transforms.put("float", "F");
/*  85 */     transforms.put("int", "I");
/*  86 */     transforms.put("long", "J");
/*  87 */     transforms.put("short", "S");
/*  88 */     transforms.put("boolean", "Z");
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.ReflectUtils
 * JD-Core Version:    0.6.0
 */