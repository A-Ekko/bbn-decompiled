/*     */ package org.logicalcobwebs.cglib.core;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ 
/*     */ public class TypeUtils
/*     */ {
/*  22 */   private static final Map transforms = new HashMap();
/*  23 */   private static final Map rtransforms = new HashMap();
/*     */ 
/*     */   public static Type getType(String className)
/*     */   {
/*  43 */     return Type.getType("L" + className.replace('.', '/') + ";");
/*     */   }
/*     */ 
/*     */   public static boolean isFinal(int access) {
/*  47 */     return (0x10 & access) != 0;
/*     */   }
/*     */ 
/*     */   public static boolean isStatic(int access) {
/*  51 */     return (0x8 & access) != 0;
/*     */   }
/*     */ 
/*     */   public static boolean isProtected(int access) {
/*  55 */     return (0x4 & access) != 0;
/*     */   }
/*     */ 
/*     */   public static boolean isPublic(int access) {
/*  59 */     return (0x1 & access) != 0;
/*     */   }
/*     */ 
/*     */   public static boolean isAbstract(int access) {
/*  63 */     return (0x400 & access) != 0;
/*     */   }
/*     */ 
/*     */   public static boolean isInterface(int access) {
/*  67 */     return (0x200 & access) != 0;
/*     */   }
/*     */ 
/*     */   public static boolean isPrivate(int access) {
/*  71 */     return (0x2 & access) != 0;
/*     */   }
/*     */ 
/*     */   public static boolean isSynthetic(int access) {
/*  75 */     return (0x1000 & access) != 0;
/*     */   }
/*     */ 
/*     */   public static String getPackageName(Type type)
/*     */   {
/*  80 */     return getPackageName(getClassName(type));
/*     */   }
/*     */ 
/*     */   public static String getPackageName(String className) {
/*  84 */     int idx = className.lastIndexOf('.');
/*  85 */     return idx < 0 ? "" : className.substring(0, idx);
/*     */   }
/*     */ 
/*     */   public static String upperFirst(String s) {
/*  89 */     if ((s == null) || (s.length() == 0)) {
/*  90 */       return s;
/*     */     }
/*  92 */     return Character.toUpperCase(s.charAt(0)) + s.substring(1);
/*     */   }
/*     */ 
/*     */   public static String getClassName(Type type) {
/*  96 */     if (isPrimitive(type))
/*  97 */       return (String)rtransforms.get(type.getDescriptor());
/*  98 */     if (isArray(type)) {
/*  99 */       return getClassName(getComponentType(type)) + "[]";
/*     */     }
/* 101 */     return type.getClassName();
/*     */   }
/*     */ 
/*     */   public static Type[] add(Type[] types, Type extra)
/*     */   {
/* 106 */     if (types == null) {
/* 107 */       return new Type[] { extra };
/*     */     }
/* 109 */     List list = Arrays.asList(types);
/* 110 */     if (list.contains(extra)) {
/* 111 */       return types;
/*     */     }
/* 113 */     Type[] copy = new Type[types.length + 1];
/* 114 */     System.arraycopy(types, 0, copy, 0, types.length);
/* 115 */     copy[types.length] = extra;
/* 116 */     return copy;
/*     */   }
/*     */ 
/*     */   public static Type[] add(Type[] t1, Type[] t2)
/*     */   {
/* 122 */     Type[] all = new Type[t1.length + t2.length];
/* 123 */     System.arraycopy(t1, 0, all, 0, t1.length);
/* 124 */     System.arraycopy(t2, 0, all, t1.length, t2.length);
/* 125 */     return all;
/*     */   }
/*     */ 
/*     */   public static Type fromInternalName(String name)
/*     */   {
/* 130 */     return Type.getType("L" + name + ";");
/*     */   }
/*     */ 
/*     */   public static Type[] fromInternalNames(String[] names) {
/* 134 */     if (names == null) {
/* 135 */       return null;
/*     */     }
/* 137 */     Type[] types = new Type[names.length];
/* 138 */     for (int i = 0; i < names.length; i++) {
/* 139 */       types[i] = fromInternalName(names[i]);
/*     */     }
/* 141 */     return types;
/*     */   }
/*     */ 
/*     */   public static int getStackSize(Type[] types) {
/* 145 */     int size = 0;
/* 146 */     for (int i = 0; i < types.length; i++) {
/* 147 */       size += types[i].getSize();
/*     */     }
/* 149 */     return size;
/*     */   }
/*     */ 
/*     */   public static String[] toInternalNames(Type[] types) {
/* 153 */     if (types == null) {
/* 154 */       return null;
/*     */     }
/* 156 */     String[] names = new String[types.length];
/* 157 */     for (int i = 0; i < types.length; i++) {
/* 158 */       names[i] = types[i].getInternalName();
/*     */     }
/* 160 */     return names;
/*     */   }
/*     */ 
/*     */   public static Signature parseSignature(String s) {
/* 164 */     int space = s.indexOf(' ');
/* 165 */     int lparen = s.indexOf('(', space);
/* 166 */     int rparen = s.indexOf(')', lparen);
/* 167 */     String returnType = s.substring(0, space);
/* 168 */     String methodName = s.substring(space + 1, lparen);
/* 169 */     StringBuffer sb = new StringBuffer();
/* 170 */     sb.append('(');
/* 171 */     for (Iterator it = parseTypes(s, lparen + 1, rparen).iterator(); it.hasNext(); ) {
/* 172 */       sb.append(it.next());
/*     */     }
/* 174 */     sb.append(')');
/* 175 */     sb.append(map(returnType));
/* 176 */     return new Signature(methodName, sb.toString());
/*     */   }
/*     */ 
/*     */   public static Type parseType(String s) {
/* 180 */     return Type.getType(map(s));
/*     */   }
/*     */ 
/*     */   public static Type[] parseTypes(String s) {
/* 184 */     List names = parseTypes(s, 0, s.length());
/* 185 */     Type[] types = new Type[names.size()];
/* 186 */     for (int i = 0; i < types.length; i++) {
/* 187 */       types[i] = Type.getType((String)names.get(i));
/*     */     }
/* 189 */     return types;
/*     */   }
/*     */ 
/*     */   public static Signature parseConstructor(Type[] types) {
/* 193 */     StringBuffer sb = new StringBuffer();
/* 194 */     sb.append("(");
/* 195 */     for (int i = 0; i < types.length; i++) {
/* 196 */       sb.append(types[i].getDescriptor());
/*     */     }
/* 198 */     sb.append(")");
/* 199 */     sb.append("V");
/* 200 */     return new Signature("<init>", sb.toString());
/*     */   }
/*     */ 
/*     */   public static Signature parseConstructor(String sig) {
/* 204 */     return parseSignature("void <init>(" + sig + ")");
/*     */   }
/*     */ 
/*     */   private static List parseTypes(String s, int mark, int end) {
/* 208 */     List types = new ArrayList(5);
/*     */     while (true) {
/* 210 */       int next = s.indexOf(',', mark);
/* 211 */       if (next < 0) {
/*     */         break;
/*     */       }
/* 214 */       types.add(map(s.substring(mark, next).trim()));
/* 215 */       mark = next + 1;
/*     */     }
/* 217 */     types.add(map(s.substring(mark, end).trim()));
/* 218 */     return types;
/*     */   }
/*     */ 
/*     */   private static String map(String type) {
/* 222 */     if (type.equals("")) {
/* 223 */       return type;
/*     */     }
/* 225 */     String t = (String)transforms.get(type);
/* 226 */     if (t != null)
/* 227 */       return t;
/* 228 */     if (type.indexOf('.') < 0) {
/* 229 */       return map("java.lang." + type);
/*     */     }
/* 231 */     StringBuffer sb = new StringBuffer();
/* 232 */     int index = 0;
/* 233 */     while ((index = type.indexOf("[]", index) + 1) > 0) {
/* 234 */       sb.append('[');
/*     */     }
/* 236 */     type = type.substring(0, type.length() - sb.length() * 2);
/* 237 */     sb.append('L').append(type.replace('.', '/')).append(';');
/* 238 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static Type getBoxedType(Type type)
/*     */   {
/* 243 */     switch (type.getSort()) {
/*     */     case 2:
/* 245 */       return Constants.TYPE_CHARACTER;
/*     */     case 1:
/* 247 */       return Constants.TYPE_BOOLEAN;
/*     */     case 8:
/* 249 */       return Constants.TYPE_DOUBLE;
/*     */     case 6:
/* 251 */       return Constants.TYPE_FLOAT;
/*     */     case 7:
/* 253 */       return Constants.TYPE_LONG;
/*     */     case 5:
/* 255 */       return Constants.TYPE_INTEGER;
/*     */     case 4:
/* 257 */       return Constants.TYPE_SHORT;
/*     */     case 3:
/* 259 */       return Constants.TYPE_BYTE;
/*     */     }
/* 261 */     return type;
/*     */   }
/*     */ 
/*     */   public static Type getUnboxedType(Type type)
/*     */   {
/* 266 */     if (Constants.TYPE_INTEGER.equals(type))
/* 267 */       return Type.INT_TYPE;
/* 268 */     if (Constants.TYPE_BOOLEAN.equals(type))
/* 269 */       return Type.BOOLEAN_TYPE;
/* 270 */     if (Constants.TYPE_DOUBLE.equals(type))
/* 271 */       return Type.DOUBLE_TYPE;
/* 272 */     if (Constants.TYPE_LONG.equals(type))
/* 273 */       return Type.LONG_TYPE;
/* 274 */     if (Constants.TYPE_CHARACTER.equals(type))
/* 275 */       return Type.CHAR_TYPE;
/* 276 */     if (Constants.TYPE_BYTE.equals(type))
/* 277 */       return Type.BYTE_TYPE;
/* 278 */     if (Constants.TYPE_FLOAT.equals(type))
/* 279 */       return Type.FLOAT_TYPE;
/* 280 */     if (Constants.TYPE_SHORT.equals(type)) {
/* 281 */       return Type.SHORT_TYPE;
/*     */     }
/* 283 */     return type;
/*     */   }
/*     */ 
/*     */   public static boolean isArray(Type type)
/*     */   {
/* 288 */     return type.getSort() == 9;
/*     */   }
/*     */ 
/*     */   public static Type getComponentType(Type type) {
/* 292 */     if (!isArray(type)) {
/* 293 */       throw new IllegalArgumentException("Type " + type + " is not an array");
/*     */     }
/* 295 */     return Type.getType(type.getDescriptor().substring(1));
/*     */   }
/*     */ 
/*     */   public static boolean isPrimitive(Type type) {
/* 299 */     switch (type.getSort()) {
/*     */     case 9:
/*     */     case 10:
/* 302 */       return false;
/*     */     }
/* 304 */     return true;
/*     */   }
/*     */ 
/*     */   public static String emulateClassGetName(Type type)
/*     */   {
/* 309 */     if (isArray(type)) {
/* 310 */       return type.getDescriptor().replace('/', '.');
/*     */     }
/* 312 */     return getClassName(type);
/*     */   }
/*     */ 
/*     */   public static boolean isConstructor(MethodInfo method)
/*     */   {
/* 317 */     return method.getSignature().getName().equals("<init>");
/*     */   }
/*     */ 
/*     */   public static Type[] getTypes(Class[] classes) {
/* 321 */     if (classes == null) {
/* 322 */       return null;
/*     */     }
/* 324 */     Type[] types = new Type[classes.length];
/* 325 */     for (int i = 0; i < classes.length; i++) {
/* 326 */       types[i] = Type.getType(classes[i]);
/*     */     }
/* 328 */     return types;
/*     */   }
/*     */ 
/*     */   public static int ICONST(int value) {
/* 332 */     switch (value) { case -1:
/* 333 */       return 2;
/*     */     case 0:
/* 334 */       return 3;
/*     */     case 1:
/* 335 */       return 4;
/*     */     case 2:
/* 336 */       return 5;
/*     */     case 3:
/* 337 */       return 6;
/*     */     case 4:
/* 338 */       return 7;
/*     */     case 5:
/* 339 */       return 8;
/*     */     }
/* 341 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int LCONST(long value) {
/* 345 */     if (value == 0L)
/* 346 */       return 9;
/* 347 */     if (value == 1L) {
/* 348 */       return 10;
/*     */     }
/* 350 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int FCONST(float value)
/*     */   {
/* 355 */     if (value == 0.0F)
/* 356 */       return 11;
/* 357 */     if (value == 1.0F)
/* 358 */       return 12;
/* 359 */     if (value == 2.0F) {
/* 360 */       return 13;
/*     */     }
/* 362 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int DCONST(double value)
/*     */   {
/* 367 */     if (value == 0.0D)
/* 368 */       return 14;
/* 369 */     if (value == 1.0D) {
/* 370 */       return 15;
/*     */     }
/* 372 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int NEWARRAY(Type type)
/*     */   {
/* 377 */     switch (type.getSort()) {
/*     */     case 3:
/* 379 */       return 8;
/*     */     case 2:
/* 381 */       return 5;
/*     */     case 8:
/* 383 */       return 7;
/*     */     case 6:
/* 385 */       return 6;
/*     */     case 5:
/* 387 */       return 10;
/*     */     case 7:
/* 389 */       return 11;
/*     */     case 4:
/* 391 */       return 9;
/*     */     case 1:
/* 393 */       return 4;
/*     */     }
/* 395 */     return -1;
/*     */   }
/*     */ 
/*     */   public static String escapeType(String s)
/*     */   {
/* 400 */     StringBuffer sb = new StringBuffer();
/* 401 */     int i = 0; for (int len = s.length(); i < len; i++) {
/* 402 */       char c = s.charAt(i);
/* 403 */       switch (c) { case '$':
/* 404 */         sb.append("$24"); break;
/*     */       case '.':
/* 405 */         sb.append("$2E"); break;
/*     */       case '[':
/* 406 */         sb.append("$5B"); break;
/*     */       case ';':
/* 407 */         sb.append("$3B"); break;
/*     */       case '(':
/* 408 */         sb.append("$28"); break;
/*     */       case ')':
/* 409 */         sb.append("$29"); break;
/*     */       case '/':
/* 410 */         sb.append("$2F"); break;
/*     */       default:
/* 412 */         sb.append(c);
/*     */       }
/*     */     }
/* 415 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  29 */     transforms.put("void", "V");
/*  30 */     transforms.put("byte", "B");
/*  31 */     transforms.put("char", "C");
/*  32 */     transforms.put("double", "D");
/*  33 */     transforms.put("float", "F");
/*  34 */     transforms.put("int", "I");
/*  35 */     transforms.put("long", "J");
/*  36 */     transforms.put("short", "S");
/*  37 */     transforms.put("boolean", "Z");
/*     */ 
/*  39 */     CollectionUtils.reverse(transforms, rtransforms);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.TypeUtils
 * JD-Core Version:    0.6.0
 */