/*     */ package org.logicalcobwebs.cglib.core;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import org.logicalcobwebs.cglib.asm.Attribute;
/*     */ import org.logicalcobwebs.cglib.asm.CodeVisitor;
/*     */ import org.logicalcobwebs.cglib.asm.Label;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ 
/*     */ public class CodeEmitter extends RemappingCodeVisitor
/*     */ {
/*  26 */   private static final Signature BOOLEAN_VALUE = TypeUtils.parseSignature("boolean booleanValue()");
/*     */ 
/*  28 */   private static final Signature CHAR_VALUE = TypeUtils.parseSignature("char charValue()");
/*     */ 
/*  30 */   private static final Signature LONG_VALUE = TypeUtils.parseSignature("long longValue()");
/*     */ 
/*  32 */   private static final Signature DOUBLE_VALUE = TypeUtils.parseSignature("double doubleValue()");
/*     */ 
/*  34 */   private static final Signature FLOAT_VALUE = TypeUtils.parseSignature("float floatValue()");
/*     */ 
/*  36 */   private static final Signature INT_VALUE = TypeUtils.parseSignature("int intValue()");
/*     */ 
/*  38 */   private static final Signature CSTRUCT_NULL = TypeUtils.parseConstructor("");
/*     */ 
/*  40 */   private static final Signature CSTRUCT_STRING = TypeUtils.parseConstructor("String");
/*     */   public static final int ADD = 96;
/*     */   public static final int MUL = 104;
/*     */   public static final int XOR = 130;
/*     */   public static final int USHR = 124;
/*     */   public static final int SUB = 100;
/*     */   public static final int DIV = 108;
/*     */   public static final int NEG = 116;
/*     */   public static final int REM = 112;
/*     */   public static final int AND = 126;
/*     */   public static final int OR = 128;
/*     */   public static final int GT = 157;
/*     */   public static final int LT = 155;
/*     */   public static final int GE = 156;
/*     */   public static final int LE = 158;
/*     */   public static final int NE = 154;
/*     */   public static final int EQ = 153;
/*     */   private ClassEmitter ce;
/*     */   private State state;
/*     */ 
/*     */   CodeEmitter(ClassEmitter ce, CodeVisitor cv, int access, Signature sig, Type[] exceptionTypes)
/*     */   {
/* 106 */     super(cv, access, sig.getArgumentTypes());
/* 107 */     this.ce = ce;
/* 108 */     this.state = new State(ce.getClassInfo(), access, sig, exceptionTypes);
/*     */   }
/*     */ 
/*     */   public CodeEmitter(CodeEmitter wrap) {
/* 112 */     super(wrap);
/* 113 */     this.ce = wrap.ce;
/* 114 */     this.state = wrap.state;
/*     */   }
/*     */ 
/*     */   public boolean isStaticHook() {
/* 118 */     return false;
/*     */   }
/*     */ 
/*     */   public Signature getSignature() {
/* 122 */     return this.state.sig;
/*     */   }
/*     */ 
/*     */   public Type getReturnType() {
/* 126 */     return this.state.sig.getReturnType();
/*     */   }
/*     */ 
/*     */   public MethodInfo getMethodInfo() {
/* 130 */     return this.state;
/*     */   }
/*     */ 
/*     */   public ClassEmitter getClassEmitter() {
/* 134 */     return this.ce;
/*     */   }
/*     */ 
/*     */   public void end_method() {
/* 138 */     visitMaxs(0, 0);
/*     */   }
/*     */ 
/*     */   public Block begin_block() {
/* 142 */     return new Block(this);
/*     */   }
/*     */ 
/*     */   public void catch_exception(Block block, Type exception) {
/* 146 */     if (block.getEnd() == null) {
/* 147 */       throw new IllegalStateException("end of block is unset");
/*     */     }
/* 149 */     this.cv.visitTryCatchBlock(block.getStart(), block.getEnd(), mark(), exception.getInternalName());
/*     */   }
/*     */ 
/*     */   public void goTo(Label label)
/*     */   {
/* 155 */     this.cv.visitJumpInsn(167, label); } 
/* 156 */   public void ifnull(Label label) { this.cv.visitJumpInsn(198, label); } 
/* 157 */   public void ifnonnull(Label label) { this.cv.visitJumpInsn(199, label); }
/*     */ 
/*     */   public void if_jump(int mode, Label label) {
/* 160 */     this.cv.visitJumpInsn(mode, label);
/*     */   }
/*     */ 
/*     */   public void if_icmp(int mode, Label label) {
/* 164 */     if_cmp(Type.INT_TYPE, mode, label);
/*     */   }
/*     */ 
/*     */   public void if_cmp(Type type, int mode, Label label) {
/* 168 */     int intOp = -1;
/* 169 */     int jumpmode = mode;
/* 170 */     switch (mode) { case 156:
/* 171 */       jumpmode = 155; break;
/*     */     case 158:
/* 172 */       jumpmode = 157;
/*     */     }
/* 174 */     switch (type.getSort()) {
/*     */     case 7:
/* 176 */       this.cv.visitInsn(148);
/* 177 */       break;
/*     */     case 8:
/* 179 */       this.cv.visitInsn(152);
/* 180 */       break;
/*     */     case 6:
/* 182 */       this.cv.visitInsn(150);
/* 183 */       break;
/*     */     case 9:
/*     */     case 10:
/* 186 */       switch (mode) {
/*     */       case 153:
/* 188 */         this.cv.visitJumpInsn(165, label);
/* 189 */         return;
/*     */       case 154:
/* 191 */         this.cv.visitJumpInsn(166, label);
/* 192 */         return;
/*     */       }
/* 194 */       throw new IllegalArgumentException("Bad comparison for type " + type);
/*     */     default:
/* 196 */       switch (mode) { case 153:
/* 197 */         intOp = 159; break;
/*     */       case 154:
/* 198 */         intOp = 160; break;
/*     */       case 156:
/* 199 */         swap();
/*     */       case 155:
/* 200 */         intOp = 161; break;
/*     */       case 158:
/* 201 */         swap();
/*     */       case 157:
/* 202 */         intOp = 163;
/*     */       }
/* 204 */       this.cv.visitJumpInsn(intOp, label);
/* 205 */       return;
/*     */     }
/* 207 */     if_jump(jumpmode, label);
/*     */   }
/*     */   public void pop() {
/* 210 */     this.cv.visitInsn(87); } 
/* 211 */   public void pop2() { this.cv.visitInsn(88); } 
/* 212 */   public void dup() { this.cv.visitInsn(89); } 
/* 213 */   public void dup2() { this.cv.visitInsn(92); } 
/* 214 */   public void dup_x1() { this.cv.visitInsn(90); } 
/* 215 */   public void dup_x2() { this.cv.visitInsn(91); } 
/* 216 */   public void dup2_x1() { this.cv.visitInsn(93); } 
/* 217 */   public void dup2_x2() { this.cv.visitInsn(94); } 
/* 218 */   public void swap() { this.cv.visitInsn(95); } 
/* 219 */   public void aconst_null() { this.cv.visitInsn(1); }
/*     */ 
/*     */   public void swap(Type prev, Type type) {
/* 222 */     if (type.getSize() == 1) {
/* 223 */       if (prev.getSize() == 1) {
/* 224 */         swap();
/*     */       } else {
/* 226 */         dup_x2();
/* 227 */         pop();
/*     */       }
/*     */     }
/* 230 */     else if (prev.getSize() == 1) {
/* 231 */       dup2_x1();
/* 232 */       pop2();
/*     */     } else {
/* 234 */       dup2_x2();
/* 235 */       pop2();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void monitorenter() {
/* 240 */     this.cv.visitInsn(194); } 
/* 241 */   public void monitorexit() { this.cv.visitInsn(195); } 
/*     */   public void math(int op, Type type) {
/* 243 */     this.cv.visitInsn(type.getOpcode(op));
/*     */   }
/* 245 */   public void array_load(Type type) { this.cv.visitInsn(type.getOpcode(46)); } 
/* 246 */   public void array_store(Type type) { this.cv.visitInsn(type.getOpcode(79));
/*     */   }
/*     */ 
/*     */   public void cast_numeric(Type from, Type to)
/*     */   {
/* 252 */     if (from != to)
/* 253 */       if (from == Type.DOUBLE_TYPE) {
/* 254 */         if (to == Type.FLOAT_TYPE) {
/* 255 */           this.cv.visitInsn(144);
/* 256 */         } else if (to == Type.LONG_TYPE) {
/* 257 */           this.cv.visitInsn(143);
/*     */         } else {
/* 259 */           this.cv.visitInsn(142);
/* 260 */           cast_numeric(Type.INT_TYPE, to);
/*     */         }
/* 262 */       } else if (from == Type.FLOAT_TYPE) {
/* 263 */         if (to == Type.DOUBLE_TYPE) {
/* 264 */           this.cv.visitInsn(141);
/* 265 */         } else if (to == Type.LONG_TYPE) {
/* 266 */           this.cv.visitInsn(140);
/*     */         } else {
/* 268 */           this.cv.visitInsn(139);
/* 269 */           cast_numeric(Type.INT_TYPE, to);
/*     */         }
/* 271 */       } else if (from == Type.LONG_TYPE) {
/* 272 */         if (to == Type.DOUBLE_TYPE) {
/* 273 */           this.cv.visitInsn(138);
/* 274 */         } else if (to == Type.FLOAT_TYPE) {
/* 275 */           this.cv.visitInsn(137);
/*     */         } else {
/* 277 */           this.cv.visitInsn(136);
/* 278 */           cast_numeric(Type.INT_TYPE, to);
/*     */         }
/*     */       }
/* 281 */       else if (to == Type.BYTE_TYPE)
/* 282 */         this.cv.visitInsn(145);
/* 283 */       else if (to == Type.CHAR_TYPE)
/* 284 */         this.cv.visitInsn(146);
/* 285 */       else if (to == Type.DOUBLE_TYPE)
/* 286 */         this.cv.visitInsn(135);
/* 287 */       else if (to == Type.FLOAT_TYPE)
/* 288 */         this.cv.visitInsn(134);
/* 289 */       else if (to == Type.LONG_TYPE)
/* 290 */         this.cv.visitInsn(133);
/* 291 */       else if (to == Type.SHORT_TYPE)
/* 292 */         this.cv.visitInsn(147);
/*     */   }
/*     */ 
/*     */   public void push(int i)
/*     */   {
/* 299 */     if (i < -1)
/* 300 */       this.cv.visitLdcInsn(new Integer(i));
/* 301 */     else if (i <= 5)
/* 302 */       this.cv.visitInsn(TypeUtils.ICONST(i));
/* 303 */     else if (i <= 127)
/* 304 */       this.cv.visitIntInsn(16, i);
/* 305 */     else if (i <= 32767)
/* 306 */       this.cv.visitIntInsn(17, i);
/*     */     else
/* 308 */       this.cv.visitLdcInsn(new Integer(i));
/*     */   }
/*     */ 
/*     */   public void push(long value)
/*     */   {
/* 313 */     if ((value == 0L) || (value == 1L))
/* 314 */       this.cv.visitInsn(TypeUtils.LCONST(value));
/*     */     else
/* 316 */       this.cv.visitLdcInsn(new Long(value));
/*     */   }
/*     */ 
/*     */   public void push(float value)
/*     */   {
/* 321 */     if ((value == 0.0F) || (value == 1.0F) || (value == 2.0F))
/* 322 */       this.cv.visitInsn(TypeUtils.FCONST(value));
/*     */     else
/* 324 */       this.cv.visitLdcInsn(new Float(value));
/*     */   }
/*     */ 
/*     */   public void push(double value) {
/* 328 */     if ((value == 0.0D) || (value == 1.0D))
/* 329 */       this.cv.visitInsn(TypeUtils.DCONST(value));
/*     */     else
/* 331 */       this.cv.visitLdcInsn(new Double(value));
/*     */   }
/*     */ 
/*     */   public void push(String value)
/*     */   {
/* 336 */     this.cv.visitLdcInsn(value);
/*     */   }
/*     */ 
/*     */   public void newarray() {
/* 340 */     newarray(Constants.TYPE_OBJECT);
/*     */   }
/*     */ 
/*     */   public void newarray(Type type) {
/* 344 */     if (TypeUtils.isPrimitive(type))
/* 345 */       this.cv.visitIntInsn(188, TypeUtils.NEWARRAY(type));
/*     */     else
/* 347 */       emit_type(189, type);
/*     */   }
/*     */ 
/*     */   public void arraylength()
/*     */   {
/* 352 */     this.cv.visitInsn(190);
/*     */   }
/*     */ 
/*     */   public void load_this() {
/* 356 */     if (TypeUtils.isStatic(this.state.access)) {
/* 357 */       throw new IllegalStateException("no 'this' pointer within static method");
/*     */     }
/* 359 */     this.cv.visitVarInsn(25, 0);
/*     */   }
/*     */ 
/*     */   public void load_args()
/*     */   {
/* 366 */     load_args(0, this.state.argumentTypes.length);
/*     */   }
/*     */ 
/*     */   public void load_arg(int index)
/*     */   {
/* 374 */     load_local(this.state.argumentTypes[index], this.state.localOffset + skipArgs(index));
/*     */   }
/*     */ 
/*     */   public void load_args(int fromArg, int count)
/*     */   {
/* 380 */     int pos = this.state.localOffset + skipArgs(fromArg);
/* 381 */     for (int i = 0; i < count; i++) {
/* 382 */       Type t = this.state.argumentTypes[(fromArg + i)];
/* 383 */       load_local(t, pos);
/* 384 */       pos += t.getSize();
/*     */     }
/*     */   }
/*     */ 
/*     */   private int skipArgs(int numArgs) {
/* 389 */     int amount = 0;
/* 390 */     for (int i = 0; i < numArgs; i++) {
/* 391 */       amount += this.state.argumentTypes[i].getSize();
/*     */     }
/* 393 */     return amount;
/*     */   }
/*     */ 
/*     */   private void load_local(Type t, int pos)
/*     */   {
/* 398 */     this.cv.visitVarInsn(t.getOpcode(21), pos);
/*     */   }
/*     */ 
/*     */   private void store_local(Type t, int pos)
/*     */   {
/* 403 */     this.cv.visitVarInsn(t.getOpcode(54), pos);
/*     */   }
/*     */ 
/*     */   public void iinc(Local local, int amount) {
/* 407 */     this.cv.visitIincInsn(local.getIndex(), amount);
/*     */   }
/*     */ 
/*     */   public void store_local(Local local) {
/* 411 */     store_local(local.getType(), local.getIndex());
/*     */   }
/*     */ 
/*     */   public void load_local(Local local) {
/* 415 */     load_local(local.getType(), local.getIndex());
/*     */   }
/*     */ 
/*     */   public void return_value() {
/* 419 */     this.cv.visitInsn(this.state.sig.getReturnType().getOpcode(172));
/*     */   }
/*     */ 
/*     */   public void getfield(String name) {
/* 423 */     ClassEmitter.FieldInfo info = this.ce.getFieldInfo(name);
/* 424 */     int opcode = TypeUtils.isStatic(info.access) ? 178 : 180;
/* 425 */     emit_field(opcode, this.ce.getClassType(), name, info.type);
/*     */   }
/*     */ 
/*     */   public void putfield(String name) {
/* 429 */     ClassEmitter.FieldInfo info = this.ce.getFieldInfo(name);
/* 430 */     int opcode = TypeUtils.isStatic(info.access) ? 179 : 181;
/* 431 */     emit_field(opcode, this.ce.getClassType(), name, info.type);
/*     */   }
/*     */ 
/*     */   public void super_getfield(String name, Type type) {
/* 435 */     emit_field(180, this.ce.getSuperType(), name, type);
/*     */   }
/*     */ 
/*     */   public void super_putfield(String name, Type type) {
/* 439 */     emit_field(181, this.ce.getSuperType(), name, type);
/*     */   }
/*     */ 
/*     */   public void super_getstatic(String name, Type type) {
/* 443 */     emit_field(178, this.ce.getSuperType(), name, type);
/*     */   }
/*     */ 
/*     */   public void super_putstatic(String name, Type type) {
/* 447 */     emit_field(179, this.ce.getSuperType(), name, type);
/*     */   }
/*     */ 
/*     */   public void getfield(Type owner, String name, Type type) {
/* 451 */     emit_field(180, owner, name, type);
/*     */   }
/*     */ 
/*     */   public void putfield(Type owner, String name, Type type) {
/* 455 */     emit_field(181, owner, name, type);
/*     */   }
/*     */ 
/*     */   public void getstatic(Type owner, String name, Type type) {
/* 459 */     emit_field(178, owner, name, type);
/*     */   }
/*     */ 
/*     */   public void putstatic(Type owner, String name, Type type) {
/* 463 */     emit_field(179, owner, name, type);
/*     */   }
/*     */ 
/*     */   void emit_field(int opcode, Type ctype, String name, Type ftype)
/*     */   {
/* 468 */     this.cv.visitFieldInsn(opcode, ctype.getInternalName(), name, ftype.getDescriptor());
/*     */   }
/*     */ 
/*     */   public void super_invoke()
/*     */   {
/* 475 */     super_invoke(this.state.sig);
/*     */   }
/*     */ 
/*     */   public void super_invoke(Signature sig) {
/* 479 */     emit_invoke(183, this.ce.getSuperType(), sig);
/*     */   }
/*     */ 
/*     */   public void invoke_constructor(Type type) {
/* 483 */     invoke_constructor(type, CSTRUCT_NULL);
/*     */   }
/*     */ 
/*     */   public void super_invoke_constructor() {
/* 487 */     invoke_constructor(this.ce.getSuperType());
/*     */   }
/*     */ 
/*     */   public void invoke_constructor_this() {
/* 491 */     invoke_constructor(this.ce.getClassType());
/*     */   }
/*     */ 
/*     */   private void emit_invoke(int opcode, Type type, Signature sig) {
/* 495 */     if ((sig.getName().equals("<init>")) && (opcode != 182) && (opcode == 184));
/* 500 */     this.cv.visitMethodInsn(opcode, type.getInternalName(), sig.getName(), sig.getDescriptor());
/*     */   }
/*     */ 
/*     */   public void invoke_interface(Type owner, Signature sig)
/*     */   {
/* 507 */     emit_invoke(185, owner, sig);
/*     */   }
/*     */ 
/*     */   public void invoke_virtual(Type owner, Signature sig) {
/* 511 */     emit_invoke(182, owner, sig);
/*     */   }
/*     */ 
/*     */   public void invoke_static(Type owner, Signature sig) {
/* 515 */     emit_invoke(184, owner, sig);
/*     */   }
/*     */ 
/*     */   public void invoke_virtual_this(Signature sig) {
/* 519 */     invoke_virtual(this.ce.getClassType(), sig);
/*     */   }
/*     */ 
/*     */   public void invoke_static_this(Signature sig) {
/* 523 */     invoke_static(this.ce.getClassType(), sig);
/*     */   }
/*     */ 
/*     */   public void invoke_constructor(Type type, Signature sig) {
/* 527 */     emit_invoke(183, type, sig);
/*     */   }
/*     */ 
/*     */   public void invoke_constructor_this(Signature sig) {
/* 531 */     invoke_constructor(this.ce.getClassType(), sig);
/*     */   }
/*     */ 
/*     */   public void super_invoke_constructor(Signature sig) {
/* 535 */     invoke_constructor(this.ce.getSuperType(), sig);
/*     */   }
/*     */ 
/*     */   public void new_instance_this() {
/* 539 */     new_instance(this.ce.getClassType());
/*     */   }
/*     */ 
/*     */   public void new_instance(Type type) {
/* 543 */     emit_type(187, type);
/*     */   }
/*     */ 
/*     */   private void emit_type(int opcode, Type type)
/*     */   {
/*     */     String desc;
/*     */     String desc;
/* 548 */     if (TypeUtils.isArray(type))
/* 549 */       desc = type.getDescriptor();
/*     */     else {
/* 551 */       desc = type.getInternalName();
/*     */     }
/* 553 */     this.cv.visitTypeInsn(opcode, desc);
/*     */   }
/*     */ 
/*     */   public void aaload(int index) {
/* 557 */     push(index);
/* 558 */     aaload();
/*     */   }
/*     */   public void aaload() {
/* 561 */     this.cv.visitInsn(50); } 
/* 562 */   public void aastore() { this.cv.visitInsn(83); } 
/* 563 */   public void athrow() { this.cv.visitInsn(191); }
/*     */ 
/*     */   public Label make_label() {
/* 566 */     return new Label();
/*     */   }
/*     */ 
/*     */   public Local make_local() {
/* 570 */     return make_local(Constants.TYPE_OBJECT);
/*     */   }
/*     */ 
/*     */   public Local make_local(Type type) {
/* 574 */     return new Local(nextLocal(type.getSize()), type);
/*     */   }
/*     */ 
/*     */   public void checkcast_this() {
/* 578 */     checkcast(this.ce.getClassType());
/*     */   }
/*     */ 
/*     */   public void checkcast(Type type) {
/* 582 */     if (!type.equals(Constants.TYPE_OBJECT))
/* 583 */       emit_type(192, type);
/*     */   }
/*     */ 
/*     */   public void instance_of(Type type)
/*     */   {
/* 588 */     emit_type(193, type);
/*     */   }
/*     */ 
/*     */   public void instance_of_this() {
/* 592 */     instance_of(this.ce.getClassType());
/*     */   }
/*     */ 
/*     */   public void process_switch(int[] keys, ProcessSwitchCallback callback)
/*     */   {
/*     */     float density;
/*     */     float density;
/* 597 */     if (keys.length == 0)
/* 598 */       density = 0.0F;
/*     */     else {
/* 600 */       density = keys.length / (keys[(keys.length - 1)] - keys[0] + 1);
/*     */     }
/* 602 */     process_switch(keys, callback, density >= 0.5F);
/*     */   }
/*     */ 
/*     */   public void process_switch(int[] keys, ProcessSwitchCallback callback, boolean useTable) {
/* 606 */     if (!isSorted(keys))
/* 607 */       throw new IllegalArgumentException("keys to switch must be sorted ascending");
/* 608 */     Label def = make_label();
/* 609 */     Label end = make_label();
/*     */     try
/*     */     {
/* 612 */       if (keys.length > 0) {
/* 613 */         int len = keys.length;
/* 614 */         int min = keys[0];
/* 615 */         int max = keys[(len - 1)];
/* 616 */         int range = max - min + 1;
/*     */ 
/* 618 */         if (useTable) {
/* 619 */           Label[] labels = new Label[range];
/* 620 */           Arrays.fill(labels, def);
/* 621 */           for (int i = 0; i < len; i++) {
/* 622 */             labels[(keys[i] - min)] = make_label();
/*     */           }
/* 624 */           this.cv.visitTableSwitchInsn(min, max, def, labels);
/* 625 */           for (int i = 0; i < range; i++) {
/* 626 */             Label label = labels[i];
/* 627 */             if (label != def) {
/* 628 */               mark(label);
/* 629 */               callback.processCase(i + min, end);
/*     */             }
/*     */           }
/*     */         } else {
/* 633 */           Label[] labels = new Label[len];
/* 634 */           for (int i = 0; i < len; i++) {
/* 635 */             labels[i] = make_label();
/*     */           }
/* 637 */           this.cv.visitLookupSwitchInsn(def, keys, labels);
/* 638 */           for (int i = 0; i < len; i++) {
/* 639 */             mark(labels[i]);
/* 640 */             callback.processCase(keys[i], end);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 645 */       mark(def);
/* 646 */       callback.processDefault();
/* 647 */       mark(end);
/*     */     }
/*     */     catch (RuntimeException e) {
/* 650 */       throw e;
/*     */     } catch (Error e) {
/* 652 */       throw e;
/*     */     } catch (Exception e) {
/* 654 */       throw new CodeGenerationException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static boolean isSorted(int[] keys) {
/* 659 */     for (int i = 1; i < keys.length; i++) {
/* 660 */       if (keys[i] < keys[(i - 1)])
/* 661 */         return false;
/*     */     }
/* 663 */     return true;
/*     */   }
/*     */ 
/*     */   public void mark(Label label) {
/* 667 */     this.cv.visitLabel(label);
/*     */   }
/*     */ 
/*     */   Label mark() {
/* 671 */     Label label = make_label();
/* 672 */     this.cv.visitLabel(label);
/* 673 */     return label;
/*     */   }
/*     */ 
/*     */   public void push(boolean value) {
/* 677 */     push(value ? 1 : 0);
/*     */   }
/*     */ 
/*     */   public void not()
/*     */   {
/* 684 */     push(1);
/* 685 */     math(130, Type.INT_TYPE);
/*     */   }
/*     */ 
/*     */   public void throw_exception(Type type, String msg) {
/* 689 */     new_instance(type);
/* 690 */     dup();
/* 691 */     push(msg);
/* 692 */     invoke_constructor(type, CSTRUCT_STRING);
/* 693 */     athrow();
/*     */   }
/*     */ 
/*     */   public void box(Type type)
/*     */   {
/* 704 */     if (TypeUtils.isPrimitive(type))
/* 705 */       if (type == Type.VOID_TYPE) {
/* 706 */         aconst_null();
/*     */       } else {
/* 708 */         Type boxed = TypeUtils.getBoxedType(type);
/* 709 */         new_instance(boxed);
/* 710 */         if (type.getSize() == 2)
/*     */         {
/* 712 */           dup_x2();
/* 713 */           dup_x2();
/* 714 */           pop();
/*     */         }
/*     */         else {
/* 717 */           dup_x1();
/* 718 */           swap();
/*     */         }
/* 720 */         invoke_constructor(boxed, new Signature("<init>", Type.VOID_TYPE, new Type[] { type }));
/*     */       }
/*     */   }
/*     */ 
/*     */   public void unbox(Type type)
/*     */   {
/* 733 */     Type t = Constants.TYPE_NUMBER;
/* 734 */     Signature sig = null;
/* 735 */     switch (type.getSort()) {
/*     */     case 0:
/* 737 */       return;
/*     */     case 2:
/* 739 */       t = Constants.TYPE_CHARACTER;
/* 740 */       sig = CHAR_VALUE;
/* 741 */       break;
/*     */     case 1:
/* 743 */       t = Constants.TYPE_BOOLEAN;
/* 744 */       sig = BOOLEAN_VALUE;
/* 745 */       break;
/*     */     case 8:
/* 747 */       sig = DOUBLE_VALUE;
/* 748 */       break;
/*     */     case 6:
/* 750 */       sig = FLOAT_VALUE;
/* 751 */       break;
/*     */     case 7:
/* 753 */       sig = LONG_VALUE;
/* 754 */       break;
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/* 758 */       sig = INT_VALUE;
/*     */     }
/*     */ 
/* 761 */     if (sig == null) {
/* 762 */       checkcast(type);
/*     */     } else {
/* 764 */       checkcast(t);
/* 765 */       invoke_virtual(t, sig);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void create_arg_array()
/*     */   {
/* 779 */     push(this.state.argumentTypes.length);
/* 780 */     newarray();
/* 781 */     for (int i = 0; i < this.state.argumentTypes.length; i++) {
/* 782 */       dup();
/* 783 */       push(i);
/* 784 */       load_arg(i);
/* 785 */       box(this.state.argumentTypes[i]);
/* 786 */       aastore();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void zero_or_null(Type type)
/*     */   {
/* 795 */     if (TypeUtils.isPrimitive(type))
/* 796 */       switch (type.getSort()) {
/*     */       case 8:
/* 798 */         push(0.0D);
/* 799 */         break;
/*     */       case 7:
/* 801 */         push(0L);
/* 802 */         break;
/*     */       case 6:
/* 804 */         push(0.0F);
/* 805 */         break;
/*     */       case 0:
/* 807 */         aconst_null();
/*     */       case 1:
/*     */       case 2:
/*     */       case 3:
/*     */       case 4:
/*     */       case 5:
/*     */       default:
/* 809 */         push(0);
/*     */ 
/* 811 */         break;
/*     */       }
/* 812 */     else aconst_null();
/*     */   }
/*     */ 
/*     */   public void unbox_or_zero(Type type)
/*     */   {
/* 821 */     if (TypeUtils.isPrimitive(type)) {
/* 822 */       if (type != Type.VOID_TYPE) {
/* 823 */         Label nonNull = make_label();
/* 824 */         Label end = make_label();
/* 825 */         dup();
/* 826 */         ifnonnull(nonNull);
/* 827 */         pop();
/* 828 */         zero_or_null(type);
/* 829 */         goTo(end);
/* 830 */         mark(nonNull);
/* 831 */         unbox(type);
/* 832 */         mark(end);
/*     */       }
/*     */     }
/* 835 */     else checkcast(type);
/*     */   }
/*     */ 
/*     */   public void visitMaxs(int maxStack, int maxLocals)
/*     */   {
/* 840 */     if (!TypeUtils.isAbstract(this.state.access))
/* 841 */       this.cv.visitMaxs(0, 0);
/*     */   }
/*     */ 
/*     */   public void invoke(MethodInfo method)
/*     */   {
/* 846 */     ClassInfo classInfo = method.getClassInfo();
/* 847 */     Type type = classInfo.getType();
/* 848 */     Signature sig = method.getSignature();
/* 849 */     if (sig.getName().equals("<init>"))
/* 850 */       invoke_constructor(type, sig);
/* 851 */     else if (TypeUtils.isInterface(classInfo.getModifiers()))
/* 852 */       invoke_interface(type, sig);
/* 853 */     else if (TypeUtils.isStatic(method.getModifiers()))
/* 854 */       invoke_static(type, sig);
/*     */     else
/* 856 */       invoke_virtual(type, sig);
/*     */   }
/*     */ 
/*     */   public void define_attribute(Attribute attrs)
/*     */   {
/* 861 */     this.cv.visitAttribute(attrs);
/*     */   }
/*     */ 
/*     */   private static class State extends MethodInfo
/*     */   {
/*     */     ClassInfo classInfo;
/*     */     int access;
/*     */     Signature sig;
/*     */     Type[] argumentTypes;
/*     */     int localOffset;
/*     */     Type[] exceptionTypes;
/*     */ 
/*     */     State(ClassInfo classInfo, int access, Signature sig, Type[] exceptionTypes)
/*     */     {
/*  75 */       this.classInfo = classInfo;
/*  76 */       this.access = access;
/*  77 */       this.sig = sig;
/*  78 */       this.exceptionTypes = exceptionTypes;
/*  79 */       this.localOffset = (TypeUtils.isStatic(access) ? 0 : 1);
/*  80 */       this.argumentTypes = sig.getArgumentTypes();
/*     */     }
/*     */ 
/*     */     public ClassInfo getClassInfo() {
/*  84 */       return this.classInfo;
/*     */     }
/*     */ 
/*     */     public int getModifiers() {
/*  88 */       return this.access;
/*     */     }
/*     */ 
/*     */     public Signature getSignature() {
/*  92 */       return this.sig;
/*     */     }
/*     */ 
/*     */     public Type[] getExceptionTypes() {
/*  96 */       return this.exceptionTypes;
/*     */     }
/*     */ 
/*     */     public Attribute getAttribute()
/*     */     {
/* 101 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.CodeEmitter
 * JD-Core Version:    0.6.0
 */