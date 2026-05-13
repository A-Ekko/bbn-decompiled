/*    */ package org.logicalcobwebs.cglib.transform.impl;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.Attribute;
/*    */ import org.logicalcobwebs.cglib.asm.Type;
/*    */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*    */ import org.logicalcobwebs.cglib.core.Constants;
/*    */ import org.logicalcobwebs.cglib.core.Signature;
/*    */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*    */ import org.logicalcobwebs.cglib.transform.ClassEmitterTransformer;
/*    */ 
/*    */ public class AccessFieldTransformer extends ClassEmitterTransformer
/*    */ {
/*    */   private Callback callback;
/*    */ 
/*    */   public AccessFieldTransformer(Callback callback)
/*    */   {
/* 29 */     this.callback = callback;
/*    */   }
/*    */ 
/*    */   public void declare_field(int access, String name, Type type, Object value, Attribute attrs)
/*    */   {
/* 37 */     super.declare_field(access, name, type, value, attrs);
/*    */ 
/* 39 */     String property = TypeUtils.upperFirst(this.callback.getPropertyName(getClassType(), name));
/* 40 */     if (property != null)
/*    */     {
/* 42 */       CodeEmitter e = begin_method(1, new Signature("get" + property, type, Constants.TYPES_EMPTY), null, null);
/*    */ 
/* 48 */       e.load_this();
/* 49 */       e.getfield(name);
/* 50 */       e.return_value();
/* 51 */       e.end_method();
/*    */ 
/* 53 */       e = begin_method(1, new Signature("set" + property, Type.VOID_TYPE, new Type[] { type }), null, null);
/*    */ 
/* 59 */       e.load_this();
/* 60 */       e.load_arg(0);
/* 61 */       e.putfield(name);
/* 62 */       e.return_value();
/* 63 */       e.end_method();
/*    */     }
/*    */   }
/*    */ 
/*    */   public static abstract interface Callback
/*    */   {
/*    */     public abstract String getPropertyName(Type paramType, String paramString);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.impl.AccessFieldTransformer
 * JD-Core Version:    0.6.0
 */