/*    */ package org.logicalcobwebs.cglib.proxy;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*    */ import org.logicalcobwebs.cglib.asm.Type;
/*    */ import org.logicalcobwebs.cglib.core.ClassEmitter;
/*    */ import org.logicalcobwebs.cglib.core.ClassInfo;
/*    */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*    */ import org.logicalcobwebs.cglib.core.Constants;
/*    */ import org.logicalcobwebs.cglib.core.EmitUtils;
/*    */ import org.logicalcobwebs.cglib.core.MethodInfo;
/*    */ import org.logicalcobwebs.cglib.core.MethodWrapper;
/*    */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*    */ import org.logicalcobwebs.cglib.core.Signature;
/*    */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*    */ 
/*    */ class MixinEmitter extends ClassEmitter
/*    */ {
/*    */   private static final String FIELD_NAME = "CGLIB$DELEGATES";
/* 30 */   private static final Signature CSTRUCT_OBJECT_ARRAY = TypeUtils.parseConstructor("Object[]");
/*    */ 
/* 32 */   private static final Type MIXIN = TypeUtils.parseType("org.logicalcobwebs.cglib.proxy.Mixin");
/*    */ 
/* 34 */   private static final Signature NEW_INSTANCE = new Signature("newInstance", MIXIN, new Type[] { Constants.TYPE_OBJECT_ARRAY });
/*    */ 
/*    */   public MixinEmitter(ClassVisitor v, String className, Class[] classes, int[] route)
/*    */   {
/* 38 */     super(v);
/*    */ 
/* 40 */     begin_class(46, 1, className, MIXIN, TypeUtils.getTypes(getInterfaces(classes)), "<generated>");
/*    */ 
/* 46 */     EmitUtils.null_constructor(this);
/* 47 */     EmitUtils.factory_method(this, NEW_INSTANCE);
/*    */ 
/* 49 */     declare_field(2, "CGLIB$DELEGATES", Constants.TYPE_OBJECT_ARRAY, null, null);
/*    */ 
/* 51 */     CodeEmitter e = begin_method(1, CSTRUCT_OBJECT_ARRAY, null, null);
/* 52 */     e.load_this();
/* 53 */     e.super_invoke_constructor();
/* 54 */     e.load_this();
/* 55 */     e.load_arg(0);
/* 56 */     e.putfield("CGLIB$DELEGATES");
/* 57 */     e.return_value();
/* 58 */     e.end_method();
/*    */ 
/* 60 */     Set unique = new HashSet();
/* 61 */     for (int i = 0; i < classes.length; i++) {
/* 62 */       Method[] methods = getMethods(classes[i]);
/* 63 */       for (int j = 0; j < methods.length; j++) {
/* 64 */         if (unique.add(MethodWrapper.create(methods[j]))) {
/* 65 */           MethodInfo method = ReflectUtils.getMethodInfo(methods[j]);
/* 66 */           e = EmitUtils.begin_method(this, method, 1);
/* 67 */           e.load_this();
/* 68 */           e.getfield("CGLIB$DELEGATES");
/* 69 */           e.aaload(route != null ? route[i] : i);
/* 70 */           e.checkcast(method.getClassInfo().getType());
/* 71 */           e.load_args();
/* 72 */           e.invoke(method);
/* 73 */           e.return_value();
/* 74 */           e.end_method();
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 79 */     end_class();
/*    */   }
/*    */ 
/*    */   protected Class[] getInterfaces(Class[] classes) {
/* 83 */     return classes;
/*    */   }
/*    */ 
/*    */   protected Method[] getMethods(Class type) {
/* 87 */     return type.getMethods();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.MixinEmitter
 * JD-Core Version:    0.6.0
 */