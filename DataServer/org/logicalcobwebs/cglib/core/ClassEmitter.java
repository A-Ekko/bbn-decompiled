/*     */ package org.logicalcobwebs.cglib.core;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.logicalcobwebs.cglib.asm.Attribute;
/*     */ import org.logicalcobwebs.cglib.asm.ClassAdapter;
/*     */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*     */ import org.logicalcobwebs.cglib.asm.CodeAdapter;
/*     */ import org.logicalcobwebs.cglib.asm.CodeVisitor;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ 
/*     */ public class ClassEmitter extends ClassAdapter
/*     */ {
/*     */   private ClassInfo classInfo;
/*     */   private Map fieldInfo;
/*     */   private static int hookCounter;
/*     */   private CodeVisitor rawStaticInit;
/*     */   private CodeEmitter staticInit;
/*     */   private CodeEmitter staticHook;
/*     */   private Signature staticHookSig;
/*     */ 
/*     */   public ClassEmitter(ClassVisitor cv)
/*     */   {
/*  36 */     super(null);
/*  37 */     setTarget(cv);
/*     */   }
/*     */ 
/*     */   public ClassEmitter() {
/*  41 */     super(null);
/*     */   }
/*     */ 
/*     */   public void setTarget(ClassVisitor cv) {
/*  45 */     this.cv = cv;
/*  46 */     this.fieldInfo = new HashMap();
/*     */ 
/*  49 */     this.staticInit = (this.staticHook = null);
/*  50 */     this.staticHookSig = null;
/*     */   }
/*     */ 
/*     */   private static synchronized int getNextHook() {
/*  54 */     return ++hookCounter;
/*     */   }
/*     */ 
/*     */   public ClassInfo getClassInfo() {
/*  58 */     return this.classInfo;
/*     */   }
/*     */ 
/*     */   public void begin_class(int version, int access, String className, Type superType, Type[] interfaces, String sourceFile) {
/*  62 */     Type classType = Type.getType("L" + className.replace('.', '/') + ";");
/*  63 */     this.classInfo = new ClassInfo(classType, superType, interfaces, access) {
/*     */       public Type getType() {
/*  65 */         return this.val$classType;
/*     */       }
/*     */       public Type getSuperType() {
/*  68 */         return this.val$superType != null ? this.val$superType : Constants.TYPE_OBJECT;
/*     */       }
/*     */       public Type[] getInterfaces() {
/*  71 */         return this.val$interfaces;
/*     */       }
/*     */       public int getModifiers() {
/*  74 */         return this.val$access;
/*     */       }
/*     */     };
/*  77 */     this.cv.visit(version, access, this.classInfo.getType().getInternalName(), this.classInfo.getSuperType().getInternalName(), TypeUtils.toInternalNames(interfaces), sourceFile);
/*     */ 
/*  83 */     init();
/*     */   }
/*     */ 
/*     */   public CodeEmitter getStaticHook() {
/*  87 */     if (TypeUtils.isInterface(getAccess())) {
/*  88 */       throw new IllegalStateException("static hook is invalid for this class");
/*     */     }
/*  90 */     if (this.staticHook == null) {
/*  91 */       this.staticHookSig = new Signature("CGLIB$STATICHOOK" + getNextHook(), "()V");
/*  92 */       this.staticHook = begin_method(8, this.staticHookSig, null, null);
/*     */ 
/*  96 */       if (this.staticInit != null) {
/*  97 */         this.staticInit.invoke_static_this(this.staticHookSig);
/*     */       }
/*     */     }
/* 100 */     return this.staticHook;
/*     */   }
/*     */ 
/*     */   protected void init() {
/*     */   }
/*     */ 
/*     */   public int getAccess() {
/* 107 */     return this.classInfo.getModifiers();
/*     */   }
/*     */ 
/*     */   public Type getClassType() {
/* 111 */     return this.classInfo.getType();
/*     */   }
/*     */ 
/*     */   public Type getSuperType() {
/* 115 */     return this.classInfo.getSuperType();
/*     */   }
/*     */ 
/*     */   public void end_class() {
/* 119 */     if ((this.staticHook != null) && (this.staticInit == null))
/*     */     {
/* 121 */       begin_static();
/*     */     }
/* 123 */     if (this.staticInit != null) {
/* 124 */       this.staticHook.return_value();
/* 125 */       this.staticHook.end_method();
/* 126 */       this.rawStaticInit.visitInsn(177);
/* 127 */       this.rawStaticInit.visitMaxs(0, 0);
/* 128 */       this.staticInit = (this.staticHook = null);
/* 129 */       this.staticHookSig = null;
/*     */     }
/* 131 */     this.cv.visitEnd();
/*     */   }
/*     */ 
/*     */   public CodeEmitter begin_method(int access, Signature sig, Type[] exceptions, Attribute attrs) {
/* 135 */     if (this.classInfo == null)
/* 136 */       throw new IllegalStateException("classInfo is null! " + this);
/* 137 */     CodeVisitor v = this.cv.visitMethod(access, sig.getName(), sig.getDescriptor(), TypeUtils.toInternalNames(exceptions), attrs);
/*     */ 
/* 142 */     if ((sig.equals(Constants.SIG_STATIC)) && (!TypeUtils.isInterface(getAccess()))) {
/* 143 */       this.rawStaticInit = v;
/* 144 */       CodeVisitor wrapped = new CodeAdapter(v) {
/*     */         public void visitMaxs(int maxStack, int maxLocals) {
/*     */         }
/*     */ 
/*     */         public void visitInsn(int insn) {
/* 149 */           if (insn != 177)
/* 150 */             super.visitInsn(insn);
/*     */         }
/*     */       };
/* 154 */       this.staticInit = new CodeEmitter(this, wrapped, access, sig, exceptions);
/* 155 */       if (this.staticHook == null)
/*     */       {
/* 157 */         getStaticHook();
/*     */       }
/* 159 */       else this.staticInit.invoke_static_this(this.staticHookSig);
/*     */ 
/* 161 */       return this.staticInit;
/* 162 */     }if (sig.equals(this.staticHookSig))
/* 163 */       return new CodeEmitter(this, v, access, sig, exceptions) {
/*     */         public boolean isStaticHook() {
/* 165 */           return true;
/*     */         }
/*     */       };
/* 169 */     return new CodeEmitter(this, v, access, sig, exceptions);
/*     */   }
/*     */ 
/*     */   public CodeEmitter begin_static()
/*     */   {
/* 174 */     return begin_method(8, Constants.SIG_STATIC, null, null);
/*     */   }
/*     */ 
/*     */   public void declare_field(int access, String name, Type type, Object value, Attribute attrs) {
/* 178 */     FieldInfo existing = (FieldInfo)this.fieldInfo.get(name);
/* 179 */     FieldInfo info = new FieldInfo(access, name, type, value);
/* 180 */     if (existing != null) {
/* 181 */       if (!info.equals(existing))
/* 182 */         throw new IllegalArgumentException("Field \"" + name + "\" has been declared differently");
/*     */     }
/*     */     else {
/* 185 */       this.fieldInfo.put(name, info);
/* 186 */       this.cv.visitField(access, name, type.getDescriptor(), value, attrs);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void define_attribute(Attribute attrs) {
/* 191 */     this.cv.visitAttribute(attrs);
/*     */   }
/*     */ 
/*     */   boolean isFieldDeclared(String name)
/*     */   {
/* 196 */     return this.fieldInfo.get(name) != null;
/*     */   }
/*     */ 
/*     */   FieldInfo getFieldInfo(String name) {
/* 200 */     FieldInfo field = (FieldInfo)this.fieldInfo.get(name);
/* 201 */     if (field == null) {
/* 202 */       throw new IllegalArgumentException("Field " + name + " is not declared in " + getClassType().getClassName());
/*     */     }
/* 204 */     return field;
/*     */   }
/*     */ 
/*     */   public void visit(int version, int access, String name, String superName, String[] interfaces, String sourceFile)
/*     */   {
/* 244 */     begin_class(version, access, name.replace('/', '.'), TypeUtils.fromInternalName(superName), TypeUtils.fromInternalNames(interfaces), sourceFile);
/*     */   }
/*     */ 
/*     */   public void visitEnd()
/*     */   {
/* 253 */     end_class();
/*     */   }
/*     */ 
/*     */   public void visitField(int access, String name, String desc, Object value, Attribute attrs) {
/* 257 */     declare_field(access, name, Type.getType(desc), value, attrs);
/*     */   }
/*     */ 
/*     */   public CodeVisitor visitMethod(int access, String name, String desc, String[] exceptions, Attribute attrs)
/*     */   {
/* 263 */     return begin_method(access, new Signature(name, desc), TypeUtils.fromInternalNames(exceptions), attrs);
/*     */   }
/*     */ 
/*     */   public void visitAttribute(Attribute attrs)
/*     */   {
/* 270 */     define_attribute(attrs);
/*     */   }
/*     */ 
/*     */   static class FieldInfo
/*     */   {
/*     */     int access;
/*     */     String name;
/*     */     Type type;
/*     */     Object value;
/*     */ 
/*     */     public FieldInfo(int access, String name, Type type, Object value)
/*     */     {
/* 214 */       this.access = access;
/* 215 */       this.name = name;
/* 216 */       this.type = type;
/* 217 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o) {
/* 221 */       if (o == null)
/* 222 */         return false;
/* 223 */       if (!(o instanceof FieldInfo))
/* 224 */         return false;
/* 225 */       FieldInfo other = (FieldInfo)o;
/* 226 */       if ((this.access != other.access) || (!this.name.equals(other.name)) || (!this.type.equals(other.type)))
/*     */       {
/* 229 */         return false;
/*     */       }
/* 231 */       if (((this.value == null ? 1 : 0) ^ (other.value == null ? 1 : 0)) != 0) {
/* 232 */         return false;
/*     */       }
/* 234 */       return (this.value == null) || (this.value.equals(other.value));
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 239 */       return this.access ^ this.name.hashCode() ^ this.type.hashCode() ^ (this.value == null ? 0 : this.value.hashCode());
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.ClassEmitter
 * JD-Core Version:    0.6.0
 */