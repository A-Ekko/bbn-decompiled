/*     */ package org.logicalcobwebs.cglib.core;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Arrays;
/*     */ import java.util.BitSet;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.logicalcobwebs.cglib.asm.Label;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ 
/*     */ public class EmitUtils
/*     */ {
/*  25 */   private static final Signature CSTRUCT_NULL = TypeUtils.parseConstructor("");
/*     */ 
/*  27 */   private static final Signature CSTRUCT_THROWABLE = TypeUtils.parseConstructor("Throwable");
/*     */ 
/*  30 */   private static final Signature GET_NAME = TypeUtils.parseSignature("String getName()");
/*     */ 
/*  32 */   private static final Signature HASH_CODE = TypeUtils.parseSignature("int hashCode()");
/*     */ 
/*  34 */   private static final Signature EQUALS = TypeUtils.parseSignature("boolean equals(Object)");
/*     */ 
/*  36 */   private static final Signature STRING_LENGTH = TypeUtils.parseSignature("int length()");
/*     */ 
/*  38 */   private static final Signature STRING_CHAR_AT = TypeUtils.parseSignature("char charAt(int)");
/*     */ 
/*  40 */   private static final Signature FOR_NAME = TypeUtils.parseSignature("Class forName(String)");
/*     */ 
/*  42 */   private static final Signature DOUBLE_TO_LONG_BITS = TypeUtils.parseSignature("long doubleToLongBits(double)");
/*     */ 
/*  44 */   private static final Signature FLOAT_TO_INT_BITS = TypeUtils.parseSignature("int floatToIntBits(float)");
/*     */ 
/*  46 */   private static final Signature TO_STRING = TypeUtils.parseSignature("String toString()");
/*     */ 
/*  48 */   private static final Signature APPEND_STRING = TypeUtils.parseSignature("StringBuffer append(String)");
/*     */ 
/*  50 */   private static final Signature APPEND_INT = TypeUtils.parseSignature("StringBuffer append(int)");
/*     */ 
/*  52 */   private static final Signature APPEND_DOUBLE = TypeUtils.parseSignature("StringBuffer append(double)");
/*     */ 
/*  54 */   private static final Signature APPEND_FLOAT = TypeUtils.parseSignature("StringBuffer append(float)");
/*     */ 
/*  56 */   private static final Signature APPEND_CHAR = TypeUtils.parseSignature("StringBuffer append(char)");
/*     */ 
/*  58 */   private static final Signature APPEND_LONG = TypeUtils.parseSignature("StringBuffer append(long)");
/*     */ 
/*  60 */   private static final Signature APPEND_BOOLEAN = TypeUtils.parseSignature("StringBuffer append(boolean)");
/*     */ 
/*  62 */   private static final Signature LENGTH = TypeUtils.parseSignature("int length()");
/*     */ 
/*  64 */   private static final Signature SET_LENGTH = TypeUtils.parseSignature("void setLength(int)");
/*     */ 
/*  66 */   private static final Signature GET_DECLARED_METHOD = TypeUtils.parseSignature("java.lang.reflect.Method getDeclaredMethod(String, Class[])");
/*     */ 
/*  71 */   public static final ArrayDelimiters DEFAULT_DELIMITERS = new ArrayDelimiters("{", ", ", "}");
/*     */ 
/*     */   public static void factory_method(ClassEmitter ce, Signature sig)
/*     */   {
/*  77 */     CodeEmitter e = ce.begin_method(1, sig, null, null);
/*  78 */     e.new_instance_this();
/*  79 */     e.dup();
/*  80 */     e.load_args();
/*  81 */     e.invoke_constructor_this(TypeUtils.parseConstructor(sig.getArgumentTypes()));
/*  82 */     e.return_value();
/*  83 */     e.end_method();
/*     */   }
/*     */ 
/*     */   public static void null_constructor(ClassEmitter ce) {
/*  87 */     CodeEmitter e = ce.begin_method(1, CSTRUCT_NULL, null, null);
/*  88 */     e.load_this();
/*  89 */     e.super_invoke_constructor();
/*  90 */     e.return_value();
/*  91 */     e.end_method();
/*     */   }
/*     */ 
/*     */   public static void process_array(CodeEmitter e, Type type, ProcessArrayCallback callback)
/*     */   {
/* 102 */     Type componentType = TypeUtils.getComponentType(type);
/* 103 */     Local array = e.make_local();
/* 104 */     Local loopvar = e.make_local(Type.INT_TYPE);
/* 105 */     Label loopbody = e.make_label();
/* 106 */     Label checkloop = e.make_label();
/* 107 */     e.store_local(array);
/* 108 */     e.push(0);
/* 109 */     e.store_local(loopvar);
/* 110 */     e.goTo(checkloop);
/*     */ 
/* 112 */     e.mark(loopbody);
/* 113 */     e.load_local(array);
/* 114 */     e.load_local(loopvar);
/* 115 */     e.array_load(componentType);
/* 116 */     callback.processElement(componentType);
/* 117 */     e.iinc(loopvar, 1);
/*     */ 
/* 119 */     e.mark(checkloop);
/* 120 */     e.load_local(loopvar);
/* 121 */     e.load_local(array);
/* 122 */     e.arraylength();
/* 123 */     e.if_icmp(155, loopbody);
/*     */   }
/*     */ 
/*     */   public static void process_arrays(CodeEmitter e, Type type, ProcessArrayCallback callback)
/*     */   {
/* 134 */     Type componentType = TypeUtils.getComponentType(type);
/* 135 */     Local array1 = e.make_local();
/* 136 */     Local array2 = e.make_local();
/* 137 */     Local loopvar = e.make_local(Type.INT_TYPE);
/* 138 */     Label loopbody = e.make_label();
/* 139 */     Label checkloop = e.make_label();
/* 140 */     e.store_local(array1);
/* 141 */     e.store_local(array2);
/* 142 */     e.push(0);
/* 143 */     e.store_local(loopvar);
/* 144 */     e.goTo(checkloop);
/*     */ 
/* 146 */     e.mark(loopbody);
/* 147 */     e.load_local(array1);
/* 148 */     e.load_local(loopvar);
/* 149 */     e.array_load(componentType);
/* 150 */     e.load_local(array2);
/* 151 */     e.load_local(loopvar);
/* 152 */     e.array_load(componentType);
/* 153 */     callback.processElement(componentType);
/* 154 */     e.iinc(loopvar, 1);
/*     */ 
/* 156 */     e.mark(checkloop);
/* 157 */     e.load_local(loopvar);
/* 158 */     e.load_local(array1);
/* 159 */     e.arraylength();
/* 160 */     e.if_icmp(155, loopbody);
/*     */   }
/*     */ 
/*     */   public static void string_switch(CodeEmitter e, String[] strings, int switchStyle, ObjectSwitchCallback callback) {
/*     */     try {
/* 165 */       switch (switchStyle) {
/*     */       case 0:
/* 167 */         string_switch_trie(e, strings, callback);
/* 168 */         break;
/*     */       case 1:
/* 170 */         string_switch_hash(e, strings, callback, false);
/* 171 */         break;
/*     */       case 2:
/* 173 */         string_switch_hash(e, strings, callback, true);
/* 174 */         break;
/*     */       default:
/* 176 */         throw new IllegalArgumentException("unknown switch style " + switchStyle);
/*     */       }
/*     */     } catch (RuntimeException ex) {
/* 179 */       throw ex;
/*     */     } catch (Error ex) {
/* 181 */       throw ex;
/*     */     } catch (Exception ex) {
/* 183 */       throw new CodeGenerationException(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void string_switch_trie(CodeEmitter e, String[] strings, ObjectSwitchCallback callback)
/*     */     throws Exception
/*     */   {
/* 190 */     Label def = e.make_label();
/* 191 */     Label end = e.make_label();
/* 192 */     Map buckets = CollectionUtils.bucket(Arrays.asList(strings), new Transformer() {
/*     */       public Object transform(Object value) {
/* 194 */         return new Integer(((String)value).length());
/*     */       }
/*     */     });
/* 197 */     e.dup();
/* 198 */     e.invoke_virtual(Constants.TYPE_STRING, STRING_LENGTH);
/* 199 */     e.process_switch(getSwitchKeys(buckets), new ProcessSwitchCallback(buckets, e, callback, def, end) {
/*     */       public void processCase(int key, Label ignore_end) throws Exception {
/* 201 */         List bucket = (List)this.val$buckets.get(new Integer(key));
/* 202 */         EmitUtils.access$000(this.val$e, bucket, this.val$callback, this.val$def, this.val$end, 0);
/*     */       }
/*     */       public void processDefault() {
/* 205 */         this.val$e.goTo(this.val$def);
/*     */       }
/*     */     });
/* 208 */     e.mark(def);
/* 209 */     e.pop();
/* 210 */     callback.processDefault();
/* 211 */     e.mark(end);
/*     */   }
/*     */ 
/*     */   private static void stringSwitchHelper(CodeEmitter e, List strings, ObjectSwitchCallback callback, Label def, Label end, int index)
/*     */     throws Exception
/*     */   {
/* 220 */     int len = ((String)strings.get(0)).length();
/* 221 */     Map buckets = CollectionUtils.bucket(strings, new Transformer(index) {
/*     */       public Object transform(Object value) {
/* 223 */         return new Integer(((String)value).charAt(this.val$index));
/*     */       }
/*     */     });
/* 226 */     e.dup();
/* 227 */     e.push(index);
/* 228 */     e.invoke_virtual(Constants.TYPE_STRING, STRING_CHAR_AT);
/* 229 */     e.process_switch(getSwitchKeys(buckets), new ProcessSwitchCallback(buckets, index, len, e, callback, end, def) {
/*     */       public void processCase(int key, Label ignore_end) throws Exception {
/* 231 */         List bucket = (List)this.val$buckets.get(new Integer(key));
/* 232 */         if (this.val$index + 1 == this.val$len) {
/* 233 */           this.val$e.pop();
/* 234 */           this.val$callback.processCase(bucket.get(0), this.val$end);
/*     */         } else {
/* 236 */           EmitUtils.access$000(this.val$e, bucket, this.val$callback, this.val$def, this.val$end, this.val$index + 1);
/*     */         }
/*     */       }
/*     */ 
/*     */       public void processDefault() {
/* 240 */         this.val$e.goTo(this.val$def);
/*     */       } } );
/*     */   }
/*     */ 
/*     */   static int[] getSwitchKeys(Map buckets) {
/* 246 */     int[] keys = new int[buckets.size()];
/* 247 */     int index = 0;
/* 248 */     for (Iterator it = buckets.keySet().iterator(); it.hasNext(); ) {
/* 249 */       keys[(index++)] = ((Integer)it.next()).intValue();
/*     */     }
/* 251 */     Arrays.sort(keys);
/* 252 */     return keys;
/*     */   }
/*     */ 
/*     */   private static void string_switch_hash(CodeEmitter e, String[] strings, ObjectSwitchCallback callback, boolean skipEquals)
/*     */     throws Exception
/*     */   {
/* 259 */     Map buckets = CollectionUtils.bucket(Arrays.asList(strings), new Transformer() {
/*     */       public Object transform(Object value) {
/* 261 */         return new Integer(value.hashCode());
/*     */       }
/*     */     });
/* 264 */     Label def = e.make_label();
/* 265 */     Label end = e.make_label();
/* 266 */     e.dup();
/* 267 */     e.invoke_virtual(Constants.TYPE_OBJECT, HASH_CODE);
/* 268 */     e.process_switch(getSwitchKeys(buckets), new ProcessSwitchCallback(buckets, skipEquals, e, callback, end, def) {
/*     */       public void processCase(int key, Label ignore_end) throws Exception {
/* 270 */         List bucket = (List)this.val$buckets.get(new Integer(key));
/* 271 */         Label next = null;
/*     */         Iterator it;
/* 272 */         if ((this.val$skipEquals) && (bucket.size() == 1)) {
/* 273 */           if (this.val$skipEquals)
/* 274 */             this.val$e.pop();
/* 275 */           this.val$callback.processCase((String)bucket.get(0), this.val$end);
/*     */         } else {
/* 277 */           for (it = bucket.iterator(); it.hasNext(); ) {
/* 278 */             String string = (String)it.next();
/* 279 */             if (next != null) {
/* 280 */               this.val$e.mark(next);
/*     */             }
/* 282 */             if (it.hasNext()) {
/* 283 */               this.val$e.dup();
/*     */             }
/* 285 */             this.val$e.push(string);
/* 286 */             this.val$e.invoke_virtual(Constants.TYPE_OBJECT, EmitUtils.EQUALS);
/* 287 */             if (it.hasNext()) {
/* 288 */               this.val$e.if_jump(153, next = this.val$e.make_label());
/* 289 */               this.val$e.pop();
/*     */             } else {
/* 291 */               this.val$e.if_jump(153, this.val$def);
/*     */             }
/* 293 */             this.val$callback.processCase(string, this.val$end);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */       public void processDefault() {
/* 298 */         this.val$e.pop();
/*     */       }
/*     */     });
/* 301 */     e.mark(def);
/* 302 */     callback.processDefault();
/* 303 */     e.mark(end);
/*     */   }
/*     */ 
/*     */   public static void load_class_this(CodeEmitter e) {
/* 307 */     load_class_helper(e, e.getClassEmitter().getClassType());
/*     */   }
/*     */ 
/*     */   public static void load_class(CodeEmitter e, Type type) {
/* 311 */     if (TypeUtils.isPrimitive(type)) {
/* 312 */       if (type == Type.VOID_TYPE) {
/* 313 */         throw new IllegalArgumentException("cannot load void type");
/*     */       }
/* 315 */       e.getstatic(TypeUtils.getBoxedType(type), "TYPE", Constants.TYPE_CLASS);
/*     */     } else {
/* 317 */       load_class_helper(e, type);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void load_class_helper(CodeEmitter e, Type type) {
/* 322 */     if (e.isStaticHook())
/*     */     {
/* 324 */       e.push(TypeUtils.emulateClassGetName(type));
/* 325 */       e.invoke_static(Constants.TYPE_CLASS, FOR_NAME);
/*     */     } else {
/* 327 */       ClassEmitter ce = e.getClassEmitter();
/* 328 */       String typeName = TypeUtils.emulateClassGetName(type);
/*     */ 
/* 331 */       String fieldName = "CGLIB$load_class$" + TypeUtils.escapeType(typeName);
/* 332 */       if (!ce.isFieldDeclared(fieldName)) {
/* 333 */         ce.declare_field(26, fieldName, Constants.TYPE_CLASS, null, null);
/* 334 */         CodeEmitter hook = ce.getStaticHook();
/* 335 */         hook.push(typeName);
/* 336 */         hook.invoke_static(Constants.TYPE_CLASS, FOR_NAME);
/* 337 */         hook.putstatic(ce.getClassType(), fieldName, Constants.TYPE_CLASS);
/*     */       }
/* 339 */       e.getfield(fieldName);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void push_array(CodeEmitter e, Object[] array) {
/* 344 */     e.push(array.length);
/* 345 */     e.newarray(Type.getType(remapComponentType(array.getClass().getComponentType())));
/* 346 */     for (int i = 0; i < array.length; i++) {
/* 347 */       e.dup();
/* 348 */       e.push(i);
/* 349 */       push_object(e, array[i]);
/* 350 */       e.aastore();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Class remapComponentType(Class componentType) {
/* 355 */     if (componentType.equals(Type.class))
/* 356 */       return Class.class;
/* 357 */     return componentType;
/*     */   }
/*     */ 
/*     */   public static void push_object(CodeEmitter e, Object obj) {
/* 361 */     if (obj == null) {
/* 362 */       e.aconst_null();
/*     */     } else {
/* 364 */       Class type = obj.getClass();
/* 365 */       if (type.isArray()) {
/* 366 */         push_array(e, (Object[])(Object[])obj);
/* 367 */       } else if ((obj instanceof String)) {
/* 368 */         e.push((String)obj);
/* 369 */       } else if ((obj instanceof Type)) {
/* 370 */         load_class(e, (Type)obj);
/* 371 */       } else if ((obj instanceof Class)) {
/* 372 */         load_class(e, Type.getType((Class)obj));
/* 373 */       } else if ((obj instanceof BigInteger)) {
/* 374 */         e.new_instance(Constants.TYPE_BIG_INTEGER);
/* 375 */         e.dup();
/* 376 */         e.push(obj.toString());
/* 377 */         e.invoke_constructor(Constants.TYPE_BIG_INTEGER);
/* 378 */       } else if ((obj instanceof BigDecimal)) {
/* 379 */         e.new_instance(Constants.TYPE_BIG_DECIMAL);
/* 380 */         e.dup();
/* 381 */         e.push(obj.toString());
/* 382 */         e.invoke_constructor(Constants.TYPE_BIG_DECIMAL);
/*     */       } else {
/* 384 */         throw new IllegalArgumentException("unknown type: " + obj.getClass());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void hash_code(CodeEmitter e, Type type, int multiplier, Customizer customizer) {
/* 390 */     if (TypeUtils.isArray(type)) {
/* 391 */       hash_array(e, type, multiplier, customizer);
/*     */     } else {
/* 393 */       e.swap(Type.INT_TYPE, type);
/* 394 */       e.push(multiplier);
/* 395 */       e.math(104, Type.INT_TYPE);
/* 396 */       e.swap(type, Type.INT_TYPE);
/* 397 */       if (TypeUtils.isPrimitive(type))
/* 398 */         hash_primitive(e, type);
/*     */       else {
/* 400 */         hash_object(e, type, customizer);
/*     */       }
/* 402 */       e.math(96, Type.INT_TYPE);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void hash_array(CodeEmitter e, Type type, int multiplier, Customizer customizer) {
/* 407 */     Label skip = e.make_label();
/* 408 */     Label end = e.make_label();
/* 409 */     e.dup();
/* 410 */     e.ifnull(skip);
/* 411 */     process_array(e, type, new ProcessArrayCallback(e, multiplier, customizer) {
/*     */       public void processElement(Type type) {
/* 413 */         EmitUtils.hash_code(this.val$e, type, this.val$multiplier, this.val$customizer);
/*     */       }
/*     */     });
/* 416 */     e.goTo(end);
/* 417 */     e.mark(skip);
/* 418 */     e.pop();
/* 419 */     e.mark(end);
/*     */   }
/*     */ 
/*     */   private static void hash_object(CodeEmitter e, Type type, Customizer customizer)
/*     */   {
/* 424 */     Label skip = e.make_label();
/* 425 */     Label end = e.make_label();
/* 426 */     e.dup();
/* 427 */     e.ifnull(skip);
/* 428 */     if (customizer != null) {
/* 429 */       customizer.customize(e, type);
/*     */     }
/* 431 */     e.invoke_virtual(Constants.TYPE_OBJECT, HASH_CODE);
/* 432 */     e.goTo(end);
/* 433 */     e.mark(skip);
/* 434 */     e.pop();
/* 435 */     e.push(0);
/* 436 */     e.mark(end);
/*     */   }
/*     */ 
/*     */   private static void hash_primitive(CodeEmitter e, Type type) {
/* 440 */     switch (type.getSort())
/*     */     {
/*     */     case 1:
/* 443 */       e.push(1);
/* 444 */       e.math(130, Type.INT_TYPE);
/* 445 */       break;
/*     */     case 6:
/* 448 */       e.invoke_static(Constants.TYPE_FLOAT, FLOAT_TO_INT_BITS);
/* 449 */       break;
/*     */     case 8:
/* 452 */       e.invoke_static(Constants.TYPE_DOUBLE, DOUBLE_TO_LONG_BITS);
/*     */     case 7:
/* 455 */       hash_long(e);
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     }
/*     */   }
/* 461 */   private static void hash_long(CodeEmitter e) { e.dup2();
/* 462 */     e.push(32);
/* 463 */     e.math(124, Type.LONG_TYPE);
/* 464 */     e.math(130, Type.LONG_TYPE);
/* 465 */     e.cast_numeric(Type.LONG_TYPE, Type.INT_TYPE);
/*     */   }
/*     */ 
/*     */   public static void not_equals(CodeEmitter e, Type type, Label notEquals, Customizer customizer)
/*     */   {
/* 480 */     new ProcessArrayCallback(e, notEquals, customizer) {
/*     */       public void processElement(Type type) {
/* 482 */         EmitUtils.access$200(this.val$e, type, this.val$notEquals, this.val$customizer, this);
/*     */       }
/*     */     }
/* 480 */     .processElement(type);
/*     */   }
/*     */ 
/*     */   private static void not_equals_helper(CodeEmitter e, Type type, Label notEquals, Customizer customizer, ProcessArrayCallback callback)
/*     */   {
/* 492 */     if (TypeUtils.isPrimitive(type)) {
/* 493 */       e.if_cmp(type, 154, notEquals);
/*     */     } else {
/* 495 */       Label end = e.make_label();
/* 496 */       nullcmp(e, notEquals, end);
/* 497 */       if (TypeUtils.isArray(type)) {
/* 498 */         Label checkContents = e.make_label();
/* 499 */         e.dup2();
/* 500 */         e.arraylength();
/* 501 */         e.swap();
/* 502 */         e.arraylength();
/* 503 */         e.if_icmp(153, checkContents);
/* 504 */         e.pop2();
/* 505 */         e.goTo(notEquals);
/* 506 */         e.mark(checkContents);
/* 507 */         process_arrays(e, type, callback);
/*     */       } else {
/* 509 */         if (customizer != null) {
/* 510 */           customizer.customize(e, type);
/* 511 */           e.swap();
/* 512 */           customizer.customize(e, type);
/*     */         }
/* 514 */         e.invoke_virtual(Constants.TYPE_OBJECT, EQUALS);
/* 515 */         e.if_jump(153, notEquals);
/*     */       }
/* 517 */       e.mark(end);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void nullcmp(CodeEmitter e, Label oneNull, Label bothNull)
/*     */   {
/* 529 */     e.dup2();
/* 530 */     Label nonNull = e.make_label();
/* 531 */     Label oneNullHelper = e.make_label();
/* 532 */     Label end = e.make_label();
/* 533 */     e.ifnonnull(nonNull);
/* 534 */     e.ifnonnull(oneNullHelper);
/* 535 */     e.pop2();
/* 536 */     e.goTo(bothNull);
/*     */ 
/* 538 */     e.mark(nonNull);
/* 539 */     e.ifnull(oneNullHelper);
/* 540 */     e.goTo(end);
/*     */ 
/* 542 */     e.mark(oneNullHelper);
/* 543 */     e.pop2();
/* 544 */     e.goTo(oneNull);
/*     */ 
/* 546 */     e.mark(end);
/*     */   }
/*     */ 
/*     */   public static void append_string(CodeEmitter e, Type type, ArrayDelimiters delims, Customizer customizer)
/*     */   {
/* 567 */     ArrayDelimiters d = delims != null ? delims : DEFAULT_DELIMITERS;
/* 568 */     ProcessArrayCallback callback = new ProcessArrayCallback(e, d, customizer) {
/*     */       public void processElement(Type type) {
/* 570 */         EmitUtils.access$300(this.val$e, type, this.val$d, this.val$customizer, this);
/* 571 */         this.val$e.push(this.val$d.inside);
/* 572 */         this.val$e.invoke_virtual(Constants.TYPE_STRING_BUFFER, EmitUtils.APPEND_STRING);
/*     */       }
/*     */     };
/* 575 */     append_string_helper(e, type, d, customizer, callback);
/*     */   }
/*     */ 
/*     */   private static void append_string_helper(CodeEmitter e, Type type, ArrayDelimiters delims, Customizer customizer, ProcessArrayCallback callback)
/*     */   {
/* 583 */     Label skip = e.make_label();
/* 584 */     Label end = e.make_label();
/* 585 */     if (TypeUtils.isPrimitive(type)) {
/* 586 */       switch (type.getSort()) {
/*     */       case 3:
/*     */       case 4:
/*     */       case 5:
/* 590 */         e.invoke_virtual(Constants.TYPE_STRING_BUFFER, APPEND_INT);
/* 591 */         break;
/*     */       case 8:
/* 593 */         e.invoke_virtual(Constants.TYPE_STRING_BUFFER, APPEND_DOUBLE);
/* 594 */         break;
/*     */       case 6:
/* 596 */         e.invoke_virtual(Constants.TYPE_STRING_BUFFER, APPEND_FLOAT);
/* 597 */         break;
/*     */       case 7:
/* 599 */         e.invoke_virtual(Constants.TYPE_STRING_BUFFER, APPEND_LONG);
/* 600 */         break;
/*     */       case 1:
/* 602 */         e.invoke_virtual(Constants.TYPE_STRING_BUFFER, APPEND_BOOLEAN);
/* 603 */         break;
/*     */       case 2:
/* 605 */         e.invoke_virtual(Constants.TYPE_STRING_BUFFER, APPEND_CHAR);
/*     */       }
/*     */     }
/* 608 */     else if (TypeUtils.isArray(type)) {
/* 609 */       e.dup();
/* 610 */       e.ifnull(skip);
/* 611 */       e.swap();
/* 612 */       if ((delims != null) && (delims.before != null) && (!"".equals(delims.before))) {
/* 613 */         e.push(delims.before);
/* 614 */         e.invoke_virtual(Constants.TYPE_STRING_BUFFER, APPEND_STRING);
/* 615 */         e.swap();
/*     */       }
/* 617 */       process_array(e, type, callback);
/* 618 */       shrinkStringBuffer(e, 2);
/* 619 */       if ((delims != null) && (delims.after != null) && (!"".equals(delims.after))) {
/* 620 */         e.push(delims.after);
/* 621 */         e.invoke_virtual(Constants.TYPE_STRING_BUFFER, APPEND_STRING);
/*     */       }
/*     */     } else {
/* 624 */       e.dup();
/* 625 */       e.ifnull(skip);
/* 626 */       if (customizer != null) {
/* 627 */         customizer.customize(e, type);
/*     */       }
/* 629 */       e.invoke_virtual(Constants.TYPE_OBJECT, TO_STRING);
/* 630 */       e.invoke_virtual(Constants.TYPE_STRING_BUFFER, APPEND_STRING);
/*     */     }
/* 632 */     e.goTo(end);
/* 633 */     e.mark(skip);
/* 634 */     e.pop();
/* 635 */     e.push("null");
/* 636 */     e.invoke_virtual(Constants.TYPE_STRING_BUFFER, APPEND_STRING);
/* 637 */     e.mark(end);
/*     */   }
/*     */ 
/*     */   private static void shrinkStringBuffer(CodeEmitter e, int amt) {
/* 641 */     e.dup();
/* 642 */     e.dup();
/* 643 */     e.invoke_virtual(Constants.TYPE_STRING_BUFFER, LENGTH);
/* 644 */     e.push(amt);
/* 645 */     e.math(100, Type.INT_TYPE);
/* 646 */     e.invoke_virtual(Constants.TYPE_STRING_BUFFER, SET_LENGTH);
/*     */   }
/*     */ 
/*     */   public static void load_method(CodeEmitter e, MethodInfo method)
/*     */   {
/* 662 */     load_class(e, method.getClassInfo().getType());
/* 663 */     e.push(method.getSignature().getName());
/* 664 */     push_object(e, method.getSignature().getArgumentTypes());
/* 665 */     e.invoke_virtual(Constants.TYPE_CLASS, GET_DECLARED_METHOD);
/*     */   }
/*     */ 
/*     */   public static void method_switch(CodeEmitter e, List methods, ObjectSwitchCallback callback)
/*     */   {
/* 675 */     member_switch_helper(e, methods, callback, true);
/*     */   }
/*     */ 
/*     */   public static void constructor_switch(CodeEmitter e, List constructors, ObjectSwitchCallback callback)
/*     */   {
/* 681 */     member_switch_helper(e, constructors, callback, false);
/*     */   }
/*     */ 
/*     */   private static void member_switch_helper(CodeEmitter e, List members, ObjectSwitchCallback callback, boolean useName)
/*     */   {
/*     */     try
/*     */     {
/* 689 */       Map cache = new HashMap();
/* 690 */       ParameterTyper cached = new ParameterTyper(cache) {
/*     */         public Type[] getParameterTypes(MethodInfo member) {
/* 692 */           Type[] types = (Type[])(Type[])this.val$cache.get(member);
/* 693 */           if (types == null) {
/* 694 */             this.val$cache.put(member, types = member.getSignature().getArgumentTypes());
/*     */           }
/* 696 */           return types;
/*     */         }
/*     */       };
/* 699 */       Label def = e.make_label();
/* 700 */       Label end = e.make_label();
/* 701 */       if (useName) {
/* 702 */         e.swap();
/* 703 */         Map buckets = CollectionUtils.bucket(members, new Transformer() {
/*     */           public Object transform(Object value) {
/* 705 */             return ((MethodInfo)value).getSignature().getName();
/*     */           }
/*     */         });
/* 708 */         String[] names = (String[])(String[])buckets.keySet().toArray(new String[buckets.size()]);
/* 709 */         string_switch(e, names, 1, new ObjectSwitchCallback(e, buckets, callback, cached, def, end) {
/*     */           public void processCase(Object key, Label dontUseEnd) throws Exception {
/* 711 */             EmitUtils.access$800(this.val$e, (List)this.val$buckets.get(key), this.val$callback, this.val$cached, this.val$def, this.val$end);
/*     */           }
/*     */           public void processDefault() throws Exception {
/* 714 */             this.val$e.goTo(this.val$def);
/*     */           } } );
/*     */       } else {
/* 718 */         member_helper_size(e, members, callback, cached, def, end);
/*     */       }
/* 720 */       e.mark(def);
/* 721 */       e.pop();
/* 722 */       callback.processDefault();
/* 723 */       e.mark(end);
/*     */     } catch (RuntimeException ex) {
/* 725 */       throw ex;
/*     */     } catch (Error ex) {
/* 727 */       throw ex;
/*     */     } catch (Exception ex) {
/* 729 */       throw new CodeGenerationException(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void member_helper_size(CodeEmitter e, List members, ObjectSwitchCallback callback, ParameterTyper typer, Label def, Label end)
/*     */     throws Exception
/*     */   {
/* 739 */     Map buckets = CollectionUtils.bucket(members, new Transformer(typer) {
/*     */       public Object transform(Object value) {
/* 741 */         return new Integer(this.val$typer.getParameterTypes((MethodInfo)value).length);
/*     */       }
/*     */     });
/* 744 */     e.dup();
/* 745 */     e.arraylength();
/* 746 */     e.process_switch(getSwitchKeys(buckets), new ProcessSwitchCallback(buckets, e, callback, typer, def, end) {
/*     */       public void processCase(int key, Label dontUseEnd) throws Exception {
/* 748 */         List bucket = (List)this.val$buckets.get(new Integer(key));
/* 749 */         EmitUtils.access$900(this.val$e, bucket, this.val$callback, this.val$typer, this.val$def, this.val$end, new BitSet());
/*     */       }
/*     */       public void processDefault() throws Exception {
/* 752 */         this.val$e.goTo(this.val$def);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private static void member_helper_type(CodeEmitter e, List members, ObjectSwitchCallback callback, ParameterTyper typer, Label def, Label end, BitSet checked)
/*     */     throws Exception
/*     */   {
/* 764 */     if (members.size() == 1) {
/* 765 */       MethodInfo member = (MethodInfo)members.get(0);
/* 766 */       Type[] types = typer.getParameterTypes(member);
/*     */ 
/* 768 */       for (int i = 0; i < types.length; i++) {
/* 769 */         if ((checked == null) || (!checked.get(i))) {
/* 770 */           e.dup();
/* 771 */           e.aaload(i);
/* 772 */           e.invoke_virtual(Constants.TYPE_CLASS, GET_NAME);
/* 773 */           e.push(TypeUtils.emulateClassGetName(types[i]));
/* 774 */           e.invoke_virtual(Constants.TYPE_OBJECT, EQUALS);
/* 775 */           e.if_jump(153, def);
/*     */         }
/*     */       }
/* 778 */       e.pop();
/* 779 */       callback.processCase(member, end);
/*     */     }
/*     */     else {
/* 782 */       Type[] example = typer.getParameterTypes((MethodInfo)members.get(0));
/* 783 */       Map buckets = null;
/* 784 */       int index = -1;
/* 785 */       for (int i = 0; i < example.length; i++) {
/* 786 */         int j = i;
/* 787 */         Map test = CollectionUtils.bucket(members, new Transformer(typer, j) {
/*     */           public Object transform(Object value) {
/* 789 */             return TypeUtils.emulateClassGetName(this.val$typer.getParameterTypes((MethodInfo)value)[this.val$j]);
/*     */           }
/*     */         });
/* 792 */         if ((buckets == null) || (test.size() > buckets.size())) {
/* 793 */           buckets = test;
/* 794 */           index = i;
/*     */         }
/*     */       }
/* 797 */       if ((buckets == null) || (buckets.size() == 1))
/*     */       {
/* 800 */         e.goTo(def);
/*     */       } else {
/* 802 */         checked.set(index);
/*     */ 
/* 804 */         e.dup();
/* 805 */         e.aaload(index);
/* 806 */         e.invoke_virtual(Constants.TYPE_CLASS, GET_NAME);
/*     */ 
/* 808 */         Map fbuckets = buckets;
/* 809 */         String[] names = (String[])(String[])buckets.keySet().toArray(new String[buckets.size()]);
/* 810 */         string_switch(e, names, 1, new ObjectSwitchCallback(e, fbuckets, callback, typer, def, end, checked) {
/*     */           public void processCase(Object key, Label dontUseEnd) throws Exception {
/* 812 */             EmitUtils.access$900(this.val$e, (List)this.val$fbuckets.get(key), this.val$callback, this.val$typer, this.val$def, this.val$end, this.val$checked);
/*     */           }
/*     */           public void processDefault() throws Exception {
/* 815 */             this.val$e.goTo(this.val$def);
/*     */           } } );
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void wrap_throwable(Block block, Type wrapper) {
/* 823 */     CodeEmitter e = block.getCodeEmitter();
/* 824 */     e.catch_exception(block, Constants.TYPE_THROWABLE);
/* 825 */     e.new_instance(wrapper);
/* 826 */     e.dup_x1();
/* 827 */     e.swap();
/* 828 */     e.invoke_constructor(wrapper, CSTRUCT_THROWABLE);
/* 829 */     e.athrow();
/*     */   }
/*     */ 
/*     */   public static void add_properties(ClassEmitter ce, String[] names, Type[] types) {
/* 833 */     for (int i = 0; i < names.length; i++) {
/* 834 */       String fieldName = "$cglib_prop_" + names[i];
/* 835 */       ce.declare_field(2, fieldName, types[i], null, null);
/* 836 */       add_property(ce, names[i], types[i], fieldName);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void add_property(ClassEmitter ce, String name, Type type, String fieldName) {
/* 841 */     String property = TypeUtils.upperFirst(name);
/*     */ 
/* 843 */     CodeEmitter e = ce.begin_method(1, new Signature("get" + property, type, Constants.TYPES_EMPTY), null, null);
/*     */ 
/* 849 */     e.load_this();
/* 850 */     e.getfield(fieldName);
/* 851 */     e.return_value();
/* 852 */     e.end_method();
/*     */ 
/* 854 */     e = ce.begin_method(1, new Signature("set" + property, Type.VOID_TYPE, new Type[] { type }), null, null);
/*     */ 
/* 860 */     e.load_this();
/* 861 */     e.load_arg(0);
/* 862 */     e.putfield(fieldName);
/* 863 */     e.return_value();
/* 864 */     e.end_method();
/*     */   }
/*     */ 
/*     */   public static void wrap_undeclared_throwable(CodeEmitter e, Block handler, Type[] exceptions, Type wrapper)
/*     */   {
/* 879 */     Set set = exceptions == null ? Collections.EMPTY_SET : new HashSet(Arrays.asList(exceptions));
/*     */ 
/* 881 */     if (set.contains(Constants.TYPE_THROWABLE)) {
/* 882 */       return;
/*     */     }
/* 884 */     boolean needThrow = exceptions != null;
/* 885 */     if (!set.contains(Constants.TYPE_RUNTIME_EXCEPTION)) {
/* 886 */       e.catch_exception(handler, Constants.TYPE_RUNTIME_EXCEPTION);
/* 887 */       needThrow = true;
/*     */     }
/* 889 */     if (!set.contains(Constants.TYPE_ERROR)) {
/* 890 */       e.catch_exception(handler, Constants.TYPE_ERROR);
/* 891 */       needThrow = true;
/*     */     }
/* 893 */     if (exceptions != null) {
/* 894 */       for (int i = 0; i < exceptions.length; i++) {
/* 895 */         e.catch_exception(handler, exceptions[i]);
/*     */       }
/*     */     }
/* 898 */     if (needThrow) {
/* 899 */       e.athrow();
/*     */     }
/*     */ 
/* 902 */     e.catch_exception(handler, Constants.TYPE_THROWABLE);
/* 903 */     e.new_instance(wrapper);
/* 904 */     e.dup_x1();
/* 905 */     e.swap();
/* 906 */     e.invoke_constructor(wrapper, CSTRUCT_THROWABLE);
/* 907 */     e.athrow();
/*     */   }
/*     */ 
/*     */   public static CodeEmitter begin_method(ClassEmitter e, MethodInfo method) {
/* 911 */     return begin_method(e, method, method.getModifiers());
/*     */   }
/*     */ 
/*     */   public static CodeEmitter begin_method(ClassEmitter e, MethodInfo method, int access) {
/* 915 */     return e.begin_method(access, method.getSignature(), method.getExceptionTypes(), method.getAttribute());
/*     */   }
/*     */ 
/*     */   private static abstract interface ParameterTyper
/*     */   {
/*     */     public abstract Type[] getParameterTypes(MethodInfo paramMethodInfo);
/*     */   }
/*     */ 
/*     */   public static class ArrayDelimiters
/*     */   {
/*     */     private String before;
/*     */     private String inside;
/*     */     private String after;
/*     */ 
/*     */     public ArrayDelimiters(String before, String inside, String after)
/*     */     {
/* 655 */       this.before = before;
/* 656 */       this.inside = inside;
/* 657 */       this.after = after;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.EmitUtils
 * JD-Core Version:    0.6.0
 */